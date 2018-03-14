package us;

import java.util.ArrayList;

public class SecondOutDrug implements Runnable {
	private String code;
	//$Q403 2 F1 00 000 00 00 00 0 xxxx*
	public SecondOutDrug(String code) {
		this.code = code;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		code = code.substring(8, code.length() - 5);
		ArrayList<String> needArrList = new ArrayList<String>();
		
		while (!code.equals("")) {
			String codeString = code.substring(0, 12);
			
			code = code.substring(12);
			
			int rowNO = Integer.parseInt(codeString.substring(0, 2));
			
			// 检测出药对射
			LogWrite.println("出药对射状态检测开始");
			while (!FMoveControl.frontSysStop) {
				String sensorState = OutSensorState.getState();
				if ("00".equals(sensorState)) {
					break;
				}
				
				SysOutState.setSolved(false);
				SysOutState.setProcErrorCode(sensorState);
				while (!FMoveControl.frontSysStop && !SysOutState.isSolved()) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			LogWrite.println("出药对射状态检测结束");
			if (FMoveControl.frontSysStop) {
				LogWrite.println("出药流程停止");
				return;
			}
			
			// 运动至出药位置
			while (!FMoveControl.frontSysStop && true) {
				int outHeight = ElevatorPosition.positionHashMap.get(rowNO);
				LogWrite.println("[出药]升降机前往行："+rowNO +"，高度："+outHeight);
				
				LifterMotion lifterMotion = new LifterMotion(outHeight);
				lifterMotion.run();
				
				// 判断是否出现运动异常
				if (!"00".equals(lifterMotion.getErrCode())) {
					// 升降机运动出现问题
					SysOutState.setProcErrorCode(lifterMotion.getErrCode());
					while (!FMoveControl.frontSysStop && !SysOutState.isSolved()) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else {
					break;
				}
			}
			if (FMoveControl.frontSysStop) {
				LogWrite.println("出药流程停止");
				return;
			}
			
			// 出药
			// 出药指令
			String controlCode = "AA01" + String.format("%02X", rowNO);
			controlCode += String.format("%02X", Integer.parseInt(codeString.substring(2, 5)));
			controlCode += String.format("%02X", Integer.parseInt(codeString.substring(5, 7)));
			if (codeString.substring(7, 9).toUpperCase().equals("00") || codeString.substring(9, 11).toUpperCase().equals("00")) {
				controlCode += "3264";
			} else {
				if (codeString.substring(7, 9).toUpperCase().equals("EE")){
					controlCode += "ED";
				} else {
					controlCode += codeString.substring(7, 9);
				}
				if (codeString.substring(9, 11).toUpperCase().equals("EE")){
					controlCode += "ED";
				} else {
					controlCode += codeString.substring(9, 11);
				}
			}
			controlCode += "EE";
			
			int waittime = 0;
			
			// 发送
			synchronized (SerialPortEx.LOCK_SERIALPORT) {
				waittime = 0;
				LogWrite.println("[出药]出药指令："+controlCode);
				SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm(controlCode, 16);
				while (true) {
					String code = SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).getRecvData();
					if (code.equals("")) {
						if (waittime == 20) {
							LogWrite.println("[出药]指令长时间无响应，重新发送");
							SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm(controlCode, 16);
						} else if (waittime > 40) {
							LogWrite.println("[出药]指令长时间无响应，重新发送，跳出检测");
							break;
						}
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						LogWrite.println("[出药]返回指令："+ code);
						break;
					}
					waittime++;
				}//end while
			}//end synchronized
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}//end try
			
			LogWrite.println("[出药]判断是否出药完成");
			String outDrugReturnCode = "";
			while (true) {
				String code = "";
				synchronized (SerialPortEx.LOCK_SERIALPORT) {
					waittime = 0;
					SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm("AA00EE", 16);
					while (true) {
						code = SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).getRecvData();
						if (code.equals("")) {
							if (waittime == 20) {
								LogWrite.println("[出药]指令长时间无响应，重新发送");
								SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm(controlCode, 16);
							} else if (waittime > 40) {
								LogWrite.println("[出药]指令长时间无响应，重新发送，跳出检测");
								break;
							} 
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else {
							LogWrite.println("[出药]是否出药完成返回指令："+ code);
							break;
						}
						waittime++;
					}//end while
				}//end synchronized
				
				if (code.toUpperCase().indexOf("AA00") == 0) {
					// 延时检测
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	 			} else if (code.toUpperCase().indexOf("AA02") == 0) {
					// 出药完成
					outDrugReturnCode = code;
					break;
				}
			}
			
			LogWrite.println("[出药]本次出药完成");
			outDrugReturnCode = outDrugReturnCode.substring(4, outDrugReturnCode.length() - 2);
			int qty = Integer.parseInt(outDrugReturnCode.substring(2, 4), 16);
			if (Integer.parseInt(codeString.substring(5, 7)) - qty > 0) {
				needArrList.add(codeString.substring(0, 5) + String.format("%02d", Integer.parseInt(codeString.substring(5, 7)) - qty));
			}
		}
		
		if (needArrList.size() > 0) {
			// 补发仍未完成?
			String sOutCode = "$Q402";
			sOutCode += OutDrugWorkT.outParamters.getKey().length() + OutDrugWorkT.outParamters.getKey();
			for (String string : needArrList) {
				sOutCode += string;
			}
			sOutCode += CRCKey.getKey() + "*";
			new CodeSend().send(ASokcetType.TCP_OUT_DRUG, sOutCode);
		} else {
			// 补发完成了
			OutDrugControlT.outDrugBusy = false;
		}
	}

}
