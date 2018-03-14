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
			
			// ����ҩ����
			LogWrite.println("��ҩ����״̬��⿪ʼ");
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
			LogWrite.println("��ҩ����״̬������");
			if (FMoveControl.frontSysStop) {
				LogWrite.println("��ҩ����ֹͣ");
				return;
			}
			
			// �˶�����ҩλ��
			while (!FMoveControl.frontSysStop && true) {
				int outHeight = ElevatorPosition.positionHashMap.get(rowNO);
				LogWrite.println("[��ҩ]������ǰ���У�"+rowNO +"���߶ȣ�"+outHeight);
				
				LifterMotion lifterMotion = new LifterMotion(outHeight);
				lifterMotion.run();
				
				// �ж��Ƿ�����˶��쳣
				if (!"00".equals(lifterMotion.getErrCode())) {
					// �������˶���������
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
				LogWrite.println("��ҩ����ֹͣ");
				return;
			}
			
			// ��ҩ
			// ��ҩָ��
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
			
			// ����
			synchronized (SerialPortEx.LOCK_SERIALPORT) {
				waittime = 0;
				LogWrite.println("[��ҩ]��ҩָ�"+controlCode);
				SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm(controlCode, 16);
				while (true) {
					String code = SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).getRecvData();
					if (code.equals("")) {
						if (waittime == 20) {
							LogWrite.println("[��ҩ]ָ�ʱ������Ӧ�����·���");
							SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm(controlCode, 16);
						} else if (waittime > 40) {
							LogWrite.println("[��ҩ]ָ�ʱ������Ӧ�����·��ͣ��������");
							break;
						}
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						LogWrite.println("[��ҩ]����ָ�"+ code);
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
			
			LogWrite.println("[��ҩ]�ж��Ƿ��ҩ���");
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
								LogWrite.println("[��ҩ]ָ�ʱ������Ӧ�����·���");
								SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm(controlCode, 16);
							} else if (waittime > 40) {
								LogWrite.println("[��ҩ]ָ�ʱ������Ӧ�����·��ͣ��������");
								break;
							} 
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else {
							LogWrite.println("[��ҩ]�Ƿ��ҩ��ɷ���ָ�"+ code);
							break;
						}
						waittime++;
					}//end while
				}//end synchronized
				
				if (code.toUpperCase().indexOf("AA00") == 0) {
					// ��ʱ���
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	 			} else if (code.toUpperCase().indexOf("AA02") == 0) {
					// ��ҩ���
					outDrugReturnCode = code;
					break;
				}
			}
			
			LogWrite.println("[��ҩ]���γ�ҩ���");
			outDrugReturnCode = outDrugReturnCode.substring(4, outDrugReturnCode.length() - 2);
			int qty = Integer.parseInt(outDrugReturnCode.substring(2, 4), 16);
			if (Integer.parseInt(codeString.substring(5, 7)) - qty > 0) {
				needArrList.add(codeString.substring(0, 5) + String.format("%02d", Integer.parseInt(codeString.substring(5, 7)) - qty));
			}
		}
		
		if (needArrList.size() > 0) {
			// ������δ���?
			String sOutCode = "$Q402";
			sOutCode += OutDrugWorkT.outParamters.getKey().length() + OutDrugWorkT.outParamters.getKey();
			for (String string : needArrList) {
				sOutCode += string;
			}
			sOutCode += CRCKey.getKey() + "*";
			new CodeSend().send(ASokcetType.TCP_OUT_DRUG, sOutCode);
		} else {
			// ���������
			OutDrugControlT.outDrugBusy = false;
		}
	}

}
