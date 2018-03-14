package us;



public class XInitialize extends FUnitInitialize {
	private String initCode = "0000000000060106"+ String.format("%04X", 66) +"0003";
	private String checkInitCode = "0000000000060103"+ String.format("%04X", 70) +"0001";
	
	public XInitialize(int model) {
		super(model);
		// TODO Auto-generated constructor stub
	}

	public void run() {
		setUnitState("EU_B", false);
		
		// 初始化最大时间（秒），为0则不进行超时判断
		int X_INIT_TIMEOUT = ControlParamters.equimentUnitMap.get("EU_B").getInitTimeout();
		
		try {
		//	int xminposition = ControlParamters.equimentUnitMap.get("EU_B").getMinPosition();
		//	int xmaxposition = ControlParamters.equimentUnitMap.get("EU_B").getMaxPosition();
		//	LogWrite.println("写入运动范围："+xminposition+"    "+xmaxposition);
		//	String sposition = "";
		//	sposition = String.format("%08X", xminposition);
		//	Modbus.modbus.executeCode(PlcCodeString.W_AX_MIN_POSITION_STR + sposition.substring(sposition.length()-4) + sposition.substring(sposition.length()-8, sposition.length()-4));
			
		//	sposition = String.format("%08X", xmaxposition);
		//	Modbus.modbus.executeCode(PlcCodeString.W_AX_MAX_POSITION_STR + sposition.substring(sposition.length()-4) + sposition.substring(sposition.length()-8, sposition.length()-4));
		} catch (Exception e1) {
			LogWrite.println("XY未设置运动范围");
		}
		
		// 检测急停门控是否正常
		SpecialStateCheck specialStateCheck = new SpecialStateCheck();
		while (!specialStateCheck.backEmgenceStop()) {
			setErrorCode("EU_B", "11");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LogWrite.println("后端急停或门控异常，无法进行初始化，请检查");
		}
		while (!(ControlParamters.backAccessControl ? specialStateCheck.backDoorControl() : true)) {
			setErrorCode("EU_B", "12");
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
			setErrorCode("EU_B", "13");
			LogWrite.println("后端对射或光幕异常，无法进行初始化，请检查");
			return;
		}
		setErrorCode("EU_B", "00");
		
		// 判断X轴是否初始化
		String rString = Modbus.modbus.executeCode(checkInitCode);
		if (13 != Integer.parseInt(rString.substring(rString.length()-4), 16)) {
			//
		} else {
			if (model == 1) {
				//
			} else {
				LogWrite.println("X轴已初始化");
				setUnitState("EU_B", true);
				return;
			}
		}
		
		LogWrite.println("X轴未初始化，开始初始化");
		Modbus.modbus.executeCode(initCode);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// 判断X轴是否初始化完毕
		while (true && X_INIT_TIMEOUT-- > 0) {
			rString = Modbus.modbus.executeCode(checkInitCode);
			
			if (13 == Integer.parseInt(rString.substring(rString.length()-4), 16)) {
				LogWrite.println("X轴初始化完毕");
				setUnitState("EU_B", true);
				break;
			} else {
				LogWrite.println("X轴正在初始化");
				if (X_INIT_TIMEOUT == 0) {
					setErrorCode("EU_B", "21");
					LogWrite.println("X轴初始化超时，初始化可能发生异常");
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
