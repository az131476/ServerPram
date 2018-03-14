package us;

import java.util.Iterator;
import java.util.Map;

import bsh.EvalError;
import bsh.Interpreter;


public class OutDrugWorkT implements Runnable {
	public static OutParamters outParamters;
	protected Interpreter interpreter = new Interpreter();
	public static boolean secondOutDrug = false;
	
	public OutDrugWorkT(String code) {
		// 设备忙碌
		SysOutState.setProcErrorCode("01");
		
		if (ElevatorPosition.positionHashMap.size() == 0) {
			ElevatorPosition.init();
		}
		
		outParamters = new OutParamters();
		outParamters.initOutCode(code);
		
//		try {
//			this.interpreter.source(InitParamters.configPath + "front.script");
//			secondOutDrug = (Boolean)interpreter.get("secondOutDrug");
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
	}
	
	public boolean beltRun() {
		String runCode = "";
		try {
			runCode = interpreter.eval("getBeltMotionCode(\""+ outParamters.getOutterNO() +"\")").toString();
			LogWrite.println(runCode);
			if (!runCode.equals("")) {
				new Modbus2().operateModebus(runCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogWrite.println(e);
		}
		LogWrite.println("[出药]升降机皮带开始转动");
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public void run() {
		LogWrite.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		// 检测翻板状态
		LogWrite.println("翻板状态检测开始");
		while (!FMoveControl.frontSysStop && !OutBeltDoorState.initialize()) {
		//	ControlParamters.frontBeltDoorDealState = true;
			SysOutState.setSolved(false);
			SysOutState.setProcErrorCode("06");
			// 发送对射异常状态
		//	String code = "$S410 2 K1 …… xxxx*";
		//	new CodeSend().send(ASokcetType.TCP_OUT_DRUG, code);
		//	while (!FMoveControl.frontSysStop && !ControlParamters.frontBeltDoorDealState) {
			while (!FMoveControl.frontSysStop && !SysOutState.isSolved()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		LogWrite.println("翻板状态检测结束");
		if (FMoveControl.frontSysStop) {
			LogWrite.println("出药流程停止");
			return;
		}
		
		int outDrugOffsetHeight = 0;
		try {
			outDrugOffsetHeight = (Integer)interpreter.get("outDrugOffsetHeight");
		} catch (EvalError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// 转动皮带
		beltRun();
		
		// 出药动作
		for (int rowNO : outParamters.getDealRowSortList()) {
			OutDrugByRowVO outDrugByRowVO = outParamters.getRowHashMap().get(rowNO);
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
						LogWrite.println("对射异常，等待处理");
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
			int outHeight = ElevatorPosition.positionHashMap.get(rowNO);
			LogWrite.println("[出药]升降机前往行："+rowNO +"，高度："+outHeight);
			
			LifterMotion lifterMotion = new LifterMotion(outHeight);
			Thread moveLift2Thread = new Thread(lifterMotion);
			moveLift2Thread.start();
			// 判断运动到位
			while (true && !FMoveControl.frontSysStop) {
				int currentPos = Integer.parseInt(lifterMotion.getCurrentPosition());
				if (outDrugByRowVO.isPFlag()) {
				//	LogWrite.println("[出药]皮带机当前位置："+currentPos);
					if (Math.abs(currentPos - outHeight) < 10) {
				//		LogWrite.println("[出药]皮带机到达接药位置，开始出药");
						break;
					} 
				} else {
					// 皮带机到达范围内即可用进行出药
				//	LogWrite.println("[出药]皮带机当前位置："+currentPos);
					if (outHeight - currentPos < outDrugOffsetHeight && outHeight - currentPos > -5) {
				//		LogWrite.println("[出药]皮带机到达接药位置，开始出药");
						break;
					} 
				}
				
		//		LogWrite.println("[出药]皮带机正在前往接药位置");
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				// 判断是否出现运动异常
				if (!"00".equals(lifterMotion.getErrCode())) {
					// 升降机运动出现问题
					SysOutState.setProcErrorCode(lifterMotion.getErrCode());
					
					while (!FMoveControl.frontSysStop && !SysOutState.isSolved()) {
						try {
							LogWrite.println("升降机前往出药位置运动问题，等待处理");
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					moveLift2Thread.start();
				}
			}//end while
			
			if (FMoveControl.frontSysStop) {
				LogWrite.println("=============================================================");
				LogWrite.println("===================设备已停用，发药线程终止===================");
				LogWrite.println("=============================================================");
				return;
			}
			
			LogWrite.println("[出药]行：" + rowNO + " 出药开始");
			
			// 出药	待增加出药超时等判断
			OutDrugControlT outDrugControlT = new OutDrugControlT(outDrugByRowVO);
		//	Thread thread = new Thread(outDrugControlT);
		//	thread.start();
			outDrugControlT.run();
			
			LogWrite.println("[出药]行："+rowNO + " 出药完成");
			
			// 判断皮带机是否完成运动
			while (true) {
				while (moveLift2Thread.isAlive()) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// 判断是否出现运动异常
				if (!"00".equals(lifterMotion.getErrCode())) {
					// 升降机运动出现问题
					SysOutState.setProcErrorCode(lifterMotion.getErrCode());
					
					while (!FMoveControl.frontSysStop && !SysOutState.isSolved()) {
						try {
							LogWrite.println("升降机完成完成第一次接药出现异常，等待解决");
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					moveLift2Thread.start();
				} else {
					LogWrite.println("[出药]开始执行下一次出药");
					break;
				}
			}
		}
		LogWrite.println("[出药]全部出药完成");
		
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
		
		// 前往出药口drugCacheHeight_1S
		LogWrite.println("[出药]前往出药口："+outParamters.getOutterNO());
		try {
			int height = Integer.parseInt(interpreter.get("drugCacheHeight_" + outParamters.getOutterNO()).toString());
			LifterMotion lifterMotion = new LifterMotion(height);
			lifterMotion.run();
			while (true) {
				if (!"00".equals(lifterMotion.getErrCode())) {
					// 升降机运动出现问题
					SysOutState.setProcErrorCode(lifterMotion.getErrCode());
					
					while (!FMoveControl.frontSysStop && !SysOutState.isSolved()) {
						try {
							LogWrite.println("升降机完成所有加药准备前往出药口出现异常，等待解决");
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					lifterMotion.run();
				} else {
					LogWrite.println("[出药]到达出药口："+outParamters.getOutterNO());
					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			LogWrite.println(e);
		}
		
		// 返回出药完成指令
		boolean waitReOut = false;
		String returnCode = "$Q406";
		returnCode += outParamters.getKey().length() + outParamters.getKey();
		returnCode += outParamters.getOutterNO() + outParamters.getWinNO();
		returnCode += outParamters.getProcCode().length() + outParamters.getProcCode();
		Iterator iter1 = outParamters.getRowHashMap().entrySet().iterator();
		while (iter1.hasNext()) {
			Map.Entry entry = (Map.Entry) iter1.next();
			int rowNO = (Integer) entry.getKey();
			OutDrugByRowVO outDrugByRowVO = (OutDrugByRowVO)entry.getValue();
			
			Iterator iter2 = outDrugByRowVO.getColHashMap().entrySet().iterator();
			while (iter2.hasNext()) {
				Map.Entry entry2 = (Map.Entry) iter2.next();
				String keyStr = (String)entry2.getKey();
				String needQty = ((String)entry2.getValue()).substring(3, 5);
				String realQty = outDrugByRowVO.getResultHashMap().get(keyStr);
				if (Integer.parseInt(needQty) > Integer.parseInt(realQty)) {
					waitReOut = true;
				}
				returnCode += String.format("%02d", rowNO) + keyStr + realQty;
			}
		}
		returnCode += CRCKey.getKey() + "*";
		LogWrite.println("[出药]发药全部完成，返回出药完成");
		new CodeSend().send(ASokcetType.TCP_OUT_DRUG, returnCode);
		
		// 需补发时等待补发指令，不需要时跳过
		if (waitReOut) {
			//需要补发时，全部发药完成后进行
			
		}
		
		// 启动超时停止皮带
		
	}
}
