package us;



public class YInitialize extends FUnitInitialize {
	private String initCode = "0000000000060106"+ String.format("%04X", 72) +"0003";
	private String checkInitCode = "0000000000060103"+ String.format("%04X", 76) +"0001";
	
	public YInitialize(int model) {
		super(model);
		// TODO Auto-generated constructor stub
	}
	
	public void run() {
		setUnitState("EU_C", false);
		
		// 初始化最大时间（秒），为0则不进行超时判断
		int Y_INIT_TIMEOUT = ControlParamters.equimentUnitMap.get("EU_C").getInitTimeout();
		
		try {
		//	int yminposition = ControlParamters.equimentUnitMap.get("EU_C").getMinPosition();
		//	int ymaxposition = ControlParamters.equimentUnitMap.get("EU_C").getMaxPosition();
		//	LogWrite.println("写入运动范围："+yminposition+"     "+ymaxposition);
		//	String sposition = "";
			
		//	sposition = String.format("%08X", yminposition);
		//	Modbus.modbus.executeCode(PlcCodeString.W_AY_MIN_POSITION_STR + sposition.substring(sposition.length()-4) + sposition.substring(sposition.length()-8, sposition.length()-4));
			
		//	sposition = String.format("%08X", ymaxposition);
		//	Modbus.modbus.executeCode(PlcCodeString.W_AY_MAX_POSITION_STR + sposition.substring(sposition.length()-4) + sposition.substring(sposition.length()-8, sposition.length()-4));
			
		} catch (Exception e1) {
			LogWrite.println("Y未设置运动范围");
		}
		
		// 检测急停门控是否正常
		SpecialStateCheck specialStateCheck = new SpecialStateCheck();
		while (!specialStateCheck.backEmgenceStop()) {
			setErrorCode("EU_C", "11");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LogWrite.println("后端急停或门控异常，无法进行初始化，请检查");
		}
		while (!(ControlParamters.backAccessControl ? specialStateCheck.backDoorControl() : true)) {
			setErrorCode("EU_C", "12");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LogWrite.println("后端急停或门控异常，无法进行初始化，请检查");
		}
		
		// 检测后端对射及放药窗口是否正常，检查后通过上药界面开始初始化
		if (!specialStateCheck.backSensor()) {
			setErrorCode("EU_C", "13");
			LogWrite.println("后端对射或光幕异常，无法进行初始化，请检查");
			return;
		}
		setErrorCode("EU_C", "00");
		
		// 判断Y轴是否初始化
		String rString2 = Modbus.modbus.executeCode(checkInitCode);
		if (13 != Integer.parseInt(rString2.substring(rString2.length()-4), 16)) {
			//
		} else {
			if (model == 1) {
				//
			} else {
				setUnitState("EU_C", true);
				LogWrite.println("Y轴已初始化");
				return;
			}
		}
		
		LogWrite.println("Y轴开始初始化");
		// Y轴初始化
		Modbus.modbus.executeCode(initCode);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// 判断Y轴是否初始化完毕
		while (true && Y_INIT_TIMEOUT-- > 0) {
			rString2 = Modbus.modbus.executeCode(checkInitCode);
			if (13 == Integer.parseInt(rString2.substring(rString2.length()-4), 16)) {
				LogWrite.println("Y轴初始化完毕");
				setUnitState("EU_C", true);
				break;
			} else {
				LogWrite.println("Y轴正在初始化");
				if (Y_INIT_TIMEOUT == 0) {
					setErrorCode("EU_C", "01");
					LogWrite.println("Y轴初始化超时，初始化可能发生异常");
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}//end while
	}

}
