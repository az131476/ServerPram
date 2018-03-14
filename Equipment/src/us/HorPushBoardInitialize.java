package us;



public class HorPushBoardInitialize extends FUnitInitialize {
	private String initCode = "0000000000060106"+ String.format("%04X", 80) +"0003";
	private String checkInitCode = "0000000000060103"+ String.format("%04X", 84) +"0001";
	
	public HorPushBoardInitialize(int model) {
		super(model);
		// TODO Auto-generated constructor stub
	}

	public void run() {
		setUnitState("EU_E", false);
		
		// 初始化最大时间（秒），为0则不进行超时判断
		int PBOD_BACK_TO_ZERO = ControlParamters.equimentUnitMap.get("EU_E").getInitTimeout();
		
		// 检测急停门控是否正常
		SpecialStateCheck specialStateCheck = new SpecialStateCheck();
		while (!specialStateCheck.backEmgenceStop()) {
			setErrorCode("EU_E", "11");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LogWrite.println("后端急停或门控异常，无法进行初始化，请检查");
		}
		while (!(ControlParamters.backAccessControl ? specialStateCheck.backDoorControl() : true)) {
			setErrorCode("EU_E", "12");
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
			setErrorCode("EU_E", "13");
			LogWrite.println("后端对射或光幕异常，无法进行初始化，请检查");
			return;
		}
		setErrorCode("EU_E", "00");
		
		// 判断齐药是否初始化
		String rString = Modbus.modbus.executeCode(checkInitCode);
		if (13 != Integer.parseInt(rString.substring(rString.length()-4), 16)) {
			//
		} else {
			if (model == 1) {
				//
			} else {
				setUnitState("EU_E", true);
				LogWrite.println("批量上药齐药已初始化");
				return;
			}
		}
		
		LogWrite.println("批量上药齐药开始初始化");
		// 批量上药齐药初始化
		Modbus.modbus.executeCode(initCode);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while (true && PBOD_BACK_TO_ZERO-- > 0) {
			rString = Modbus.modbus.executeCode(checkInitCode);
			if (13 == Integer.parseInt(rString.substring(rString.length()-4), 16)) {
				LogWrite.println("批量上药齐药初始化完毕");
				setUnitState("EU_E", true);
				break;
			} else {
				LogWrite.println("批量上药齐药正在初始化");
				if (PBOD_BACK_TO_ZERO == 0) {
					setErrorCode("EU_E", "21");
					LogWrite.println("批量上药齐药初始化超时，初始化可能发生异常");
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
