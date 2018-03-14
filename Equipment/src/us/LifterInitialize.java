package us;



public class LifterInitialize extends FUnitInitialize {
	private String initCode = "0000000000060106"+ String.format("%04X", 60) +"0003";
	private String checkInitCode = "0000000000060103"+ String.format("%04X", 64) +"0001";

	public LifterInitialize(int model) {
		super(model);
		// TODO Auto-generated constructor stub
	}

	public void run() {
		setUnitState("EU_A", false);
		
		// 初始化最大时间（秒），为0则不进行超时判断
		int LIFTER_INIT_TIMEOUT = ControlParamters.equimentUnitMap.get("EU_A").getInitTimeout();
		
		try {
		//	int minposition = ControlParamters.equimentUnitMap.get("EU_A").getMinPosition();
		//	int maxposition = ControlParamters.equimentUnitMap.get("EU_A").getMaxPosition();
		//	LogWrite.println("写入运动限制范围："+minposition+"    "+maxposition);
		//	String sposition = "";
			
		//	sposition = String.format("%08X", minposition);
		//	Modbus.modbus.executeCode(PlcCodeString.W_AL_MIN_POSITION_STR + sposition.substring(sposition.length()-4) + sposition.substring(sposition.length()-8, sposition.length()-4));

		//	sposition = String.format("%08X", maxposition);
		//	Modbus.modbus.executeCode(PlcCodeString.W_AL_MAX_POSITION_STR + sposition.substring(sposition.length()-4) + sposition.substring(sposition.length()-8, sposition.length()-4));
		} catch (Exception e1) {
			LogWrite.println("未设置运动范围");
		}
		
		// 检测对射状态
		String cState = OutSensorState.getState();
		if (!"00".equals(cState)) {
			setErrorCode("EU_A", cState);
			return;
		}
		
		// 检测急停是否正常
		SpecialStateCheck specialStateCheck = new SpecialStateCheck();
		while (!specialStateCheck.emgenceStop()) {
			setErrorCode("EU_A", "11");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LogWrite.println("设备前急停处于急停状态，无法进行初始化，请检查");
		}
		
		// 检测门控是否正常
		while (!(ControlParamters.frontAccessControl ? specialStateCheck.doorControl() : true)) {
			setErrorCode("EU_A", "12");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LogWrite.println("升降机门控异常，无法进行初始化，请检查");
		}
		
		setErrorCode("EU_A", "00");
		
		// 判断升降机是否初始化 13回零完毕
		String rString = Modbus.modbus.executeCode(checkInitCode);
		if (13 != Integer.parseInt(rString.substring(rString.length()-4), 16)) { 
			//
		} else {
			if (model == 1) {
				//
			} else {
				LogWrite.println("升降机已初始化");
				setUnitState("EU_A", true);
				return;
			}
		}//end if
		
		LogWrite.println("升降机开始初始化："+initCode);
		setUnitState("EU_A", false);
		// 升降机初始化
		Modbus.modbus.executeCode(initCode);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		setUnitState("LIFT", false);
		
		while (true && LIFTER_INIT_TIMEOUT-- > 0) {
			rString = Modbus.modbus.executeCode(checkInitCode);
			if (13 == Integer.parseInt(rString.substring(rString.length()-4), 16)) {
				LogWrite.println("升降机初始化完毕");
				setUnitState("EU_A", true);
				break;
			} else {
				if (LIFTER_INIT_TIMEOUT == 0) {
					setErrorCode("EU_A", "21");
					LogWrite.println("升降机初始化超时，初始化可能发生异常");
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				LogWrite.println("升降机正在初始化："+rString);
			}
		}
	}
}
