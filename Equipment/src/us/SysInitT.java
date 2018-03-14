package us;



import bsh.Interpreter;

public class SysInitT implements Runnable {
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		// 延时一会儿
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		LogWrite.println(">>>>>>>>>>>>>>>>>>>>系统开始初始化<<<<<<<<<<<<<<<<<<<<");
		Interpreter interpreter = new Interpreter();
		try {
			interpreter.source(InitParamters.configPath + "main.script");

			// 读取是否使用门控的设置
			ControlParamters.frontAccessControl = (Boolean)interpreter.get("frontAccessControl");
			ControlParamters.backAccessControl = (Boolean)interpreter.get("backAccessControl");
			
			// 开启出药端口
			int outPort = 0;
			try {
				outPort = Integer.parseInt(interpreter.get("outPort").toString());
			} catch (Exception e) {
				outPort = 7211;
			}
			LogWrite.println("※开启出药端口："+outPort);
			new Thread(new SocketServer(ASokcetType.TCP_OUT_DRUG, outPort)).start();
			
			// 开启补药端口
			int inPort = 0;
			try {
				inPort = Integer.parseInt(interpreter.get("fillInSocketPort").toString());
			} catch (Exception e) {
				inPort = 7210;
			}
			LogWrite.println("※开启补药端口："+inPort);
			new Thread(new SocketServer(ASokcetType.TCP_IN_DRUG, inPort)).start();
			
			LogWrite.println("※开启状态监控端口：7015");
			new Thread(new UdpSocket(7015)).start();
			
			// 开启出药串口
			int sport = Integer.parseInt(interpreter.get("outBoardSerialPort").toString());
			SerialPortEx serialPortEx = new SerialPortEx();
			serialPortEx.openComm(ASerialPortType.OUTDRUG, sport);
			
			// 开启2个翻板串口
			{
				int sport2 = Integer.parseInt(interpreter.get("switchBoardSerialPort_L").toString());
				SerialPortEx serialPortEx2 = new SerialPortEx();
				serialPortEx2.openComm(ASerialPortType.DOOR_LEFT, sport2);
			}
			{
				int sport2 = Integer.parseInt(interpreter.get("switchBoardSerialPort_R").toString());
				SerialPortEx serialPortEx2 = new SerialPortEx();
				serialPortEx2.openComm(ASerialPortType.DOOR_RIGHT, sport2);
			}
			
			// 开启两个盘点传感器485COM串口
			for (int i = 1; i < 3; i++) {
				CkSerialPortEx ckSerialPortEx = new CkSerialPortEx();
				if (ckSerialPortEx.openComm((i == 1 ? ASerialPortType.CK_LEFT : ASerialPortType.CK_RIGHT), (Integer)interpreter.get("rangeSensorSerialPort_" + i))) {
					LogWrite.println("※" + i + " 号盘点传感器串口打开成功_______SUCCESS");
				} else {
					LogWrite.println("※" + i + " 号盘点传感器串口打开失败_______ERROR");
				}
			}
			
			// 开启PLC
			if (!ModbusState.getState(interpreter.get("plcIP").toString())) {
				LogWrite.println("※PLC连接失败");
			}
			
			// 升降机
			{
				EquitmentUnit eUnit = new EquitmentUnit("EU_A");
				eUnit.setInitTimeout(Integer.parseInt(interpreter.get("lifterInitTimeout").toString()));
				eUnit.setMoveTimeout(Integer.parseInt(interpreter.get("lifterMoveTimeout").toString()));
				eUnit.setMinPosition(Integer.parseInt(interpreter.get("lifterMinPosition").toString()));
				eUnit.setMaxPosition(Integer.parseInt(interpreter.get("lifterMaxPosition").toString()));
				ControlParamters.equimentUnitMap.put("EU_A", eUnit);
			}
			// X轴
			{
				EquitmentUnit eUnit = new EquitmentUnit("EU_B");
				eUnit.setInitTimeout(Integer.parseInt(interpreter.get("xInitTimeout").toString()));
				eUnit.setMoveTimeout(Integer.parseInt(interpreter.get("xMoveTimeout").toString()));
				eUnit.setMinPosition(Integer.parseInt(interpreter.get("xMinPosition").toString()));
				eUnit.setMaxPosition(Integer.parseInt(interpreter.get("xMaxPosition").toString()));
				ControlParamters.equimentUnitMap.put("EU_B", eUnit);
			}
			// Y轴
			{
				EquitmentUnit eUnit = new EquitmentUnit("EU_C");
				eUnit.setInitTimeout(Integer.parseInt(interpreter.get("yInitTimeout").toString()));
				eUnit.setMoveTimeout(Integer.parseInt(interpreter.get("yMoveTimeout").toString()));
				eUnit.setMinPosition(Integer.parseInt(interpreter.get("yMinPosition").toString()));
				eUnit.setMaxPosition(Integer.parseInt(interpreter.get("yMaxPosition").toString()));
				ControlParamters.equimentUnitMap.put("EU_C", eUnit);
			}
			// 拨药
			{
				EquitmentUnit eUnit = new EquitmentUnit("EU_D");
				eUnit.setInitTimeout(Integer.parseInt(interpreter.get("verPushBoardInitTimeout").toString()));
				eUnit.setMoveTimeout(Integer.parseInt(interpreter.get("verPushBoardMoveTimeout").toString()));
				eUnit.setMinPosition(Integer.parseInt(interpreter.get("verPushBoardMinPosition").toString()));
				eUnit.setMaxPosition(Integer.parseInt(interpreter.get("verPushBoardMaxPosition").toString()));
				ControlParamters.equimentUnitMap.put("EU_D", eUnit);
			}
			// 齐药
			{
				EquitmentUnit eUnit = new EquitmentUnit("EU_E");
				eUnit.setInitTimeout(Integer.parseInt(interpreter.get("horPushBoardInitTimeout").toString()));
				eUnit.setMoveTimeout(Integer.parseInt(interpreter.get("horPushBoardMoveTimeout").toString()));
				eUnit.setMinPosition(Integer.parseInt(interpreter.get("horPushBoardMinPosition").toString()));
				eUnit.setMaxPosition(Integer.parseInt(interpreter.get("horPushBoardMaxPosition").toString()));
				ControlParamters.equimentUnitMap.put("EU_E", eUnit);
			}
			// 出药门
			{
				EquitmentUnit eUnit = new EquitmentUnit("EU_F");
				eUnit.setInitTimeout(Integer.parseInt(interpreter.get("throughDoorInitTimeout").toString()));
				eUnit.setMoveTimeout(Integer.parseInt(interpreter.get("throughDoorMoveTimeout").toString()));
				eUnit.setMinPosition(Integer.parseInt(interpreter.get("throughDoorMinPosition").toString()));
				eUnit.setMaxPosition(Integer.parseInt(interpreter.get("throughDoorMaxPosition").toString()));
				ControlParamters.equimentUnitMap.put("EU_F", eUnit);
			}
			
			// 设备硬件开始初始化
			LogWrite.println("※设备底层硬件开始进行初始化……");
			String[] parts = interpreter.get("needInitUnit").toString().split(",");
			for (int i = 0; i < parts.length; i++) {
				if (parts[i].equals("EU_A")) {
					LogWrite.println("EU_A开始初始化");
					new Thread(new LifterInitialize(0)).start();
				} else if (parts[i].equals("EU_B")) {
					LogWrite.println("EU_B开始初始化");
					new Thread(new XInitialize(0)).start();
				} else if (parts[i].equals("EU_C")) {
					LogWrite.println("EU_C开始初始化");
					new Thread(new YInitialize(0)).start();
				} else if (parts[i].equals("EU_D")) {
					LogWrite.println("EU_D开始初始化");
					new Thread(new VerPushBoardInitialize(0)).start();
				} else if (parts[i].equals("EU_E")) {
					LogWrite.println("EU_E开始初始化");
					new Thread(new HorPushBoardInitialize(0)).start();
				} else if (parts[i].equals("EU_F")) {
					LogWrite.println("EU_F开始初始化");
					new Thread(new ThroughDoorInitialize(0)).start();
				} else {
					LogWrite.println("参数设置有误" + parts[i]);
				}//end if
			}//end for
			// check
			boolean initOver = false;
			while (!initOver) {
				initOver = true;

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				for (int i = 0; i < parts.length; i++) {
					if (parts[i].equals("EU_A")) {
						if (!ControlParamters.equimentUnitMap.get(parts[i]).isInitState()) {
							initOver = false;
						}
					} else if (parts[i].equals("EU_B")) {
						if (!ControlParamters.equimentUnitMap.get(parts[i]).isInitState()) {
							initOver = false;
						}
					} else if (parts[i].equals("EU_C")) {
						if (!ControlParamters.equimentUnitMap.get(parts[i]).isInitState()) {
							initOver = false;
						}
					} else if (parts[i].equals("EU_D")) {
						if (!ControlParamters.equimentUnitMap.get(parts[i]).isInitState()) {
							initOver = false;
						}
					} else if (parts[i].equals("EU_E")) {
						if (!ControlParamters.equimentUnitMap.get(parts[i]).isInitState()) {
							initOver = false;
						}
					} else if (parts[i].equals("EU_F")) {
						if (!ControlParamters.equimentUnitMap.get(parts[i]).isInitState()) {
							initOver = false;
						}
					} else {
						LogWrite.println("参数设置有误" + parts[i]);
					}//end if
				}//end for
			}//end while
			
			LogWrite.println(">>>>>>>>>>>>>>>>>>>>系统初始化完毕<<<<<<<<<<<<<<<<<<<<");
		} catch (Exception e) {
			e.printStackTrace();
			LogWrite.println(e);
		}
	}

}
