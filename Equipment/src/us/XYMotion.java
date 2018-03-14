package us;

import bsh.EvalError;


public class XYMotion extends FUnitMotion {
	private String checkInitCodeH = "0000000000060103"+ String.format("%04X", 70) + "0001";
	private String curPositionCodeH = "0000000000060103"+ String.format("%04X", 26) + "0001";
	private String aimPositionCodeH = "0000000000060106"+ String.format("%04X", 24);
	private String speedCodeH = "0000000000060106"+ String.format("%04X", 68);
	private String motionCodeH = "0000000000060106"+ String.format("%04X", 66) + "0001";
	private String checkMotionCodeH = "0000000000060103"+ String.format("%04X", 66) +"0001";
	
	private String checkInitCodeV = "0000000000060103"+ String.format("%04X", 76) + "0001";
	private String curPositionCodeV = "0000000000060103"+ String.format("%04X", 30) + "0001";
	private String aimPositionCodeV = "0000000000060106"+ String.format("%04X", 28);
	private String speedCodeV = "0000000000060106"+ String.format("%04X", 74);
	private String motionCodeV = "0000000000060106"+ String.format("%04X", 72) + "0001";
	private String checkMotionCodeV = "0000000000060103"+ String.format("%04X", 72) +"0001";
	
	public XYMotion(AMotion aMotion, int horPosition, int verPosition) {
		super(aMotion, horPosition, verPosition);
		errCode = "00";
		try {
			horSpeed = (Integer)interpreter.get("xSpeed");
			verSpeed = (Integer)interpreter.get("ySpeed");
			timeOut = 10 * (Integer)ControlParamters.equimentUnitMap.get("EU_A").getMoveTimeout();
		} catch (EvalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		/*if (model == 1) {
			// 启用对射被挡住XY停止
			Modbus.modbus.operateModebus("XYDF", PlcCodeString.W_IO_USEXYSTOP);
		} else {
			// 停用对射被挡住XY停止
			Modbus.modbus.operateModebus("XYDF", PlcCodeString.W_IO_STOPXYSTOP);
		}*/
		try {
			LogWrite.println("EU_BC：XY执行运动，X目标位置：" + horPosition + " X速度：" + horSpeed + " Y目标位置：" + verPosition + " Y速度：" + verSpeed);
			
			// 判断是否初始化
			String rStringH = Modbus.modbus.executeCode(checkInitCodeH);
			String rStringV = Modbus.modbus.executeCode(checkInitCodeV);
			if (13 - Integer.parseInt(rStringH.substring(rStringH.length()-4), 16) != 0 
					|| 13 - Integer.parseInt(rStringV.substring(rStringV.length()-4), 16) != 0) {
				setErrCode("11");
				LogWrite.println("EU_BC：XY轴初始化异常，无法运行");
				return;
			}
			
			// 检测急停门控是否正常
			SpecialStateCheck specialStateCheck = new SpecialStateCheck();
			if (!specialStateCheck.backEmgenceStop()) {
				setErrCode("02");
				return;
			}
			LogWrite.println("ControlParamters.backAccessControl:"+ControlParamters.backAccessControl);
			LogWrite.println("specialStateCheck.backDoorControl():"+specialStateCheck.backDoorControl());
			LogWrite.println("t:"+(ControlParamters.backAccessControl ? specialStateCheck.backDoorControl() : true));
			if (!(ControlParamters.backAccessControl ? specialStateCheck.backDoorControl() : true)) {
				setErrCode("03");
				return;
			}
			
			// 检测后端对射及放药窗口是否正常，检查后通过上药界面开始初始化
			if (!specialStateCheck.backSensor()) {
				setErrCode("04");
				return;
			}
			
			// 判断X是否执行运动
			if (aMotion == AMotion.XY || aMotion == AMotion.X) {
				String speedX = String.format("%04X", horSpeed);
				// X轴速度	
				Modbus.modbus.executeCode(speedCodeH + speedX);
				// 预期位置值
				String spositionX = String.format("%04X", horPosition);
				Modbus.modbus.executeCode(aimPositionCodeH + spositionX);
			}
			
			// 判断Y是否执行运动
			if (aMotion == AMotion.XY || aMotion == AMotion.Y) {
				String speedY = String.format("%04X", verSpeed);
				// Y轴速度
				Modbus.modbus.executeCode(speedCodeV + speedY);
				// 预期位置值
				//getRobotYValueM
				String spositionY = String.format("%04X", verPosition);
				Modbus.modbus.executeCode(aimPositionCodeV + spositionY);
			}
			
			if (aMotion == AMotion.XY || aMotion == AMotion.X) {
				Modbus.modbus.executeCode(motionCodeH);	
			}
			if (aMotion == AMotion.XY || aMotion == AMotion.Y) {
				Modbus.modbus.executeCode(motionCodeV);
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			while ((aMotion == AMotion.XY || aMotion == AMotion.X) && timeOut > 0) {
				// AC	B0
				rStringH = Modbus.modbus.executeCode(curPositionCodeH);
				// 0000 XXXX 0000 0000 0000 XXXX
				int positionx1 = Integer.parseInt(rStringH.substring(rStringH.length() - 4), 16);
				if (Math.abs(horPosition - positionx1) < 2) {
					LogWrite.println("EU_BC: X位置运动到位");
					break;
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					timeOut--;
				}
				
				// 判断是否因光幕或对射导致停止
				rStringH = Modbus.modbus.executeCode(checkMotionCodeH);
				if (Integer.parseInt(rStringH.substring(rStringH.length()-4), 16) > 90) {
					setErrCode("02");
					return;
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					timeOut--;
				}
			}//end while
			
			while ((aMotion == AMotion.XY || aMotion == AMotion.Y) && timeOut > 0) {
				rStringV = Modbus.modbus.executeCode(curPositionCodeV);
				int position2 = Integer.parseInt(rStringV.substring(rStringV.length() - 4), 16);
				if (Math.abs(verPosition - position2) < 2) {
					LogWrite.println("EU_BC: Y位置运动到位");
					break;
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					timeOut--;
				}
				
				// 判断是否因光幕或对射导致停止
				rStringH = Modbus.modbus.executeCode(checkMotionCodeH);
				if (Integer.parseInt(rStringH.substring(rStringH.length()-4), 16) > 90) {
					setErrCode("02");
					return;
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					timeOut--;
				}
			}
			
			while ((aMotion == AMotion.XY || aMotion == AMotion.X) && timeOut > 0) {
				rStringH = Modbus.modbus.executeCode(checkMotionCodeH);
				if (22 - Integer.parseInt(rStringH.substring(rStringH.length()-4), 16) == 0) {
					// 运动到位，置使用为false，跳出while
					LogWrite.println("EU_BC：X轴运动结束");
					break;
				} else if (Integer.parseInt(rStringH.substring(rStringH.length()-4), 16) > 90) {
					setErrCode("02");
					return;
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					timeOut--;
				}
			}
			while ((aMotion == AMotion.XY || aMotion == AMotion.Y) && timeOut > 0) {
				rStringV = Modbus.modbus.executeCode(checkMotionCodeV);
				if (22 - Integer.parseInt(rStringV.substring(rStringV.length()-4), 16) == 0) {
					// 运动到位，置使用为false，跳出while
					LogWrite.println("EU_A：Y轴运动结束");
					break;
				} else if (Integer.parseInt(rStringV.substring(rStringV.length()-4), 16) > 90) {
					setErrCode("02");
					return;
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					timeOut--;
				}
			}
			
			// 运动超时了
			if (timeOut < 1) {
				setErrCode("21");
				return;
			}
			LogWrite.println("EU_BC：XY运动执行完毕");
			/*
			 * 开始加药或盘点前检测门控或急停是否异常
			 */
//			if (!specialStateCheck.backEmgenceStop()) {
//				setErrCode("02");
//				return;
//			}
//			if (!(ControlParamters.backAccessControl ? specialStateCheck.backDoorControl() : true)) {
//				setErrCode("03");
//				return;
//			}
		} catch (Exception e) {
			// TODO: handle exception
			setErrCode("99");
			LogWrite.println("EU_BC：运动出现严重错误");
			LogWrite.println(e);
		}
	}
}
