package us;



public class ThroughDoorInitialize extends FUnitInitialize {
	private String initCode = "0000000000060106"+ String.format("%04X", 86) +"0003";
	private String checkInitCode = "0000000000060103"+ String.format("%04X", 90) +"0001";
	
	public ThroughDoorInitialize(int model) {
		super(model);
		// TODO Auto-generated constructor stub
	}

	public void run() {
		setUnitState("EU_F", false);
		
		// 初始化最大时间（秒），为0则不进行超时判断
		int THROUGHDOOR_INIT_TIMEOUT = ControlParamters.equimentUnitMap.get("EU_F").getInitTimeout();
		
		// 检测急停门控是否正常
		SpecialStateCheck specialStateCheck = new SpecialStateCheck();
		while (!specialStateCheck.backEmgenceStop()) {
			setErrorCode("EU_F", "11");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LogWrite.println("后端急停或门控异常，无法进行初始化，请检查");
		}
		while (!(ControlParamters.backAccessControl ? specialStateCheck.backDoorControl() : true)) {
			setErrorCode("EU_F", "12");
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
			setErrorCode("EU_F", "13");
			LogWrite.println("后端对射或光幕异常，无法进行初始化，请检查");
			return;
		}
		setErrorCode("EU_F", "00");
				
		// 判断出药门是否初始化
		String rString = Modbus.modbus.executeCode(checkInitCode);
		if (13 != Integer.parseInt(rString.substring(rString.length()-4), 16)) {
			//
		} else {
			if (model == 1) {
				//
			} else {
				setUnitState("EU_F", true);
				LogWrite.println("批量上药出药门初始化已经初始化");
				return;
			}
		}
		
		LogWrite.println("批量上药出药门初始化未初始化，开始初始化");
		Modbus.modbus.executeCode(initCode);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while (true && THROUGHDOOR_INIT_TIMEOUT-- > 0) {
			rString = Modbus.modbus.executeCode(checkInitCode);
			if (13 == Integer.parseInt(rString.substring(rString.length()-4), 16)) {
				LogWrite.println("批量上药出药门初始化完毕");
				setUnitState("EU_F", true);
				break;
			} else {
				LogWrite.println("批量上药出药门正在初始化");
				if (THROUGHDOOR_INIT_TIMEOUT == 0) {
					setErrorCode("EU_F", "21");
					LogWrite.println("批量上药出药门初始化超时，初始化可能发生异常");
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
