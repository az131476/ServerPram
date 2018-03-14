package us;



public class LifterMotion extends FUnitMotion {
	private String checkInitCode = "0000000000060103"+ String.format("%04X", 64) + "0001";
	private String curPositionCode = "0000000000060103"+ String.format("%04X", 22) + "0001";
	private String aimPositionCode = "0000000000060106"+ String.format("%04X", 20);
	private String speedCode = "0000000000060106"+ String.format("%04X", 62);
	private String motionCode = "0000000000060106"+ String.format("%04X", 60) + "0001";
	private String checkMotionCode = "0000000000060103"+ String.format("%04X", 60) +"0001";
	
	public LifterMotion(int position) {
		super(position);
		try {
			speed = (Integer)interpreter.get("lifterSpeed");
			timeOut = 10 * (Integer)ControlParamters.equimentUnitMap.get("EU_A").getMoveTimeout();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getCurrentPosition() {
		String rString = Modbus.modbus.executeCode(curPositionCode);
		try {
			return Integer.parseInt(rString.substring(rString.length() - 4), 16) + "";
		} catch (Exception e) {
			// TODO: handle exception
			LogWrite.println("EU_A：获取升降机位置出错"+rString);
			LogWrite.println(e);
			
			rString = Modbus.modbus.executeCode(curPositionCode);

			try {
			//	double position2 = HexToInt.HexToDouble(rString.substring(rString.length()-4)+rString.substring(rString.length()-8, rString.length()-4));
			//	return String.valueOf((int) position2);
				return Integer.parseInt(rString.substring(rString.length() - 4), 16) + "";
			} catch (Exception e1) {
				// TODO: handle exception
				LogWrite.println("EU_A：获取升降机位置出错2"+rString);
				LogWrite.println(e1);
				return "Error";
			}
		}
	}
	
	public void run() {
		try {
			LogWrite.println("EU_A：升降机执行运动，目标位置："+position+" 速度："+speed);
			
			setErrCode("00");
			
			// 判断皮带机是否初始化
			String rString = Modbus.modbus.executeCode(checkInitCode);  //=13回零完毕 =11正在回零
			if (13 - Integer.parseInt(rString.substring(rString.length()-4), 16) != 0){
				LogWrite.println("EU_A：升降机未执行初始化，无法运动："+rString);
				setErrCode("11");
				return;
			}
			// 急停
			// 检测急停是否正常
			SpecialStateCheck specialStateCheck = new SpecialStateCheck();
			if (!specialStateCheck.emgenceStop()) {
				setErrCode("02");
				LogWrite.println("EU_A：设备前急停处于急停状态，无法进行初始化，请检查");
				return;
			}
			
			// 门控
			// 检测门控是否正常
			if (!(ControlParamters.frontAccessControl ? specialStateCheck.doorControl() : true)) {
				setErrCode("03");
				LogWrite.println("EU_A：升降机急停或门控异常，无法进行初始化，请检查");
				return;
			}
			
			// 升降机速度
			String speedS = String.format("%04X", speed);
			Modbus.modbus.executeCode(speedCode + speedS);
			
			String sposition = String.format("%04X", this.position);
			
			Modbus.modbus.executeCode(aimPositionCode + sposition);
			
			// 运动
			System.out.println("发送执行运动，PLC反馈：" + Modbus.modbus.executeCode(motionCode));
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			while (true && timeOut > 0) {
				// 是否到达指定位置
				rString = Modbus.modbus.executeCode(curPositionCode);
			//	double position2 = HexToInt.HexToDouble(rString.substring(rString.length()-4)+rString.substring(rString.length()-8, rString.length()-4));
				int position2 = Integer.parseInt(rString.substring(rString.length() - 4), 16);
				if (Math.abs(this.position - position2) < 2) {
					LogWrite.println("EU_A：升降机位置运动到位");
					break;
				} else {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					timeOut -=  2;
				}
				
				// 运动是否出现急停
				rString = Modbus.modbus.executeCode(checkMotionCode);
				if (Integer.parseInt(rString.substring(rString.length() - 4), 16) > 90) {
					setErrCode("02");
					return;
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					timeOut -= 1;
				}
			}	
			
			while (true && timeOut > 0) {
				// 运动是否完成
				rString = Modbus.modbus.executeCode(checkMotionCode);
				if (22 - Integer.parseInt(rString.substring(rString.length() - 4), 16) == 0) {
					LogWrite.println("EU_A：升降机运动结束");
					break;
				} else if (Integer.parseInt(rString.substring(rString.length() - 4), 16) > 90) {
					setErrCode("02");
					return;
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					timeOut -= 1;
				}
			}
			
			// 运动超时了
			if (timeOut < 1) {
				setErrCode("21");
				return;
			}
			LogWrite.println("EU_A：升降机运行执行完毕");
		} catch (Exception e) {
			// TODO: handle exception
			setErrCode("99");
			LogWrite.println("EU_A：运动出现严重错误");
			LogWrite.println(e);
		}
	}
}
