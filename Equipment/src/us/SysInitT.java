package us;



import bsh.Interpreter;

public class SysInitT implements Runnable {
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		// ��ʱһ���
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		LogWrite.println(">>>>>>>>>>>>>>>>>>>>ϵͳ��ʼ��ʼ��<<<<<<<<<<<<<<<<<<<<");
		Interpreter interpreter = new Interpreter();
		try {
			interpreter.source(InitParamters.configPath + "main.script");

			// ��ȡ�Ƿ�ʹ���ſص�����
			ControlParamters.frontAccessControl = (Boolean)interpreter.get("frontAccessControl");
			ControlParamters.backAccessControl = (Boolean)interpreter.get("backAccessControl");
			
			// ������ҩ�˿�
			int outPort = 0;
			try {
				outPort = Integer.parseInt(interpreter.get("outPort").toString());
			} catch (Exception e) {
				outPort = 7211;
			}
			LogWrite.println("��������ҩ�˿ڣ�"+outPort);
			new Thread(new SocketServer(ASokcetType.TCP_OUT_DRUG, outPort)).start();
			
			// ������ҩ�˿�
			int inPort = 0;
			try {
				inPort = Integer.parseInt(interpreter.get("fillInSocketPort").toString());
			} catch (Exception e) {
				inPort = 7210;
			}
			LogWrite.println("��������ҩ�˿ڣ�"+inPort);
			new Thread(new SocketServer(ASokcetType.TCP_IN_DRUG, inPort)).start();
			
			LogWrite.println("������״̬��ض˿ڣ�7015");
			new Thread(new UdpSocket(7015)).start();
			
			// ������ҩ����
			int sport = Integer.parseInt(interpreter.get("outBoardSerialPort").toString());
			SerialPortEx serialPortEx = new SerialPortEx();
			serialPortEx.openComm(ASerialPortType.OUTDRUG, sport);
			
			// ����2�����崮��
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
			
			// ���������̵㴫����485COM����
			for (int i = 1; i < 3; i++) {
				CkSerialPortEx ckSerialPortEx = new CkSerialPortEx();
				if (ckSerialPortEx.openComm((i == 1 ? ASerialPortType.CK_LEFT : ASerialPortType.CK_RIGHT), (Integer)interpreter.get("rangeSensorSerialPort_" + i))) {
					LogWrite.println("��" + i + " ���̵㴫�������ڴ򿪳ɹ�_______SUCCESS");
				} else {
					LogWrite.println("��" + i + " ���̵㴫�������ڴ�ʧ��_______ERROR");
				}
			}
			
			// ����PLC
			if (!ModbusState.getState(interpreter.get("plcIP").toString())) {
				LogWrite.println("��PLC����ʧ��");
			}
			
			// ������
			{
				EquitmentUnit eUnit = new EquitmentUnit("EU_A");
				eUnit.setInitTimeout(Integer.parseInt(interpreter.get("lifterInitTimeout").toString()));
				eUnit.setMoveTimeout(Integer.parseInt(interpreter.get("lifterMoveTimeout").toString()));
				eUnit.setMinPosition(Integer.parseInt(interpreter.get("lifterMinPosition").toString()));
				eUnit.setMaxPosition(Integer.parseInt(interpreter.get("lifterMaxPosition").toString()));
				ControlParamters.equimentUnitMap.put("EU_A", eUnit);
			}
			// X��
			{
				EquitmentUnit eUnit = new EquitmentUnit("EU_B");
				eUnit.setInitTimeout(Integer.parseInt(interpreter.get("xInitTimeout").toString()));
				eUnit.setMoveTimeout(Integer.parseInt(interpreter.get("xMoveTimeout").toString()));
				eUnit.setMinPosition(Integer.parseInt(interpreter.get("xMinPosition").toString()));
				eUnit.setMaxPosition(Integer.parseInt(interpreter.get("xMaxPosition").toString()));
				ControlParamters.equimentUnitMap.put("EU_B", eUnit);
			}
			// Y��
			{
				EquitmentUnit eUnit = new EquitmentUnit("EU_C");
				eUnit.setInitTimeout(Integer.parseInt(interpreter.get("yInitTimeout").toString()));
				eUnit.setMoveTimeout(Integer.parseInt(interpreter.get("yMoveTimeout").toString()));
				eUnit.setMinPosition(Integer.parseInt(interpreter.get("yMinPosition").toString()));
				eUnit.setMaxPosition(Integer.parseInt(interpreter.get("yMaxPosition").toString()));
				ControlParamters.equimentUnitMap.put("EU_C", eUnit);
			}
			// ��ҩ
			{
				EquitmentUnit eUnit = new EquitmentUnit("EU_D");
				eUnit.setInitTimeout(Integer.parseInt(interpreter.get("verPushBoardInitTimeout").toString()));
				eUnit.setMoveTimeout(Integer.parseInt(interpreter.get("verPushBoardMoveTimeout").toString()));
				eUnit.setMinPosition(Integer.parseInt(interpreter.get("verPushBoardMinPosition").toString()));
				eUnit.setMaxPosition(Integer.parseInt(interpreter.get("verPushBoardMaxPosition").toString()));
				ControlParamters.equimentUnitMap.put("EU_D", eUnit);
			}
			// ��ҩ
			{
				EquitmentUnit eUnit = new EquitmentUnit("EU_E");
				eUnit.setInitTimeout(Integer.parseInt(interpreter.get("horPushBoardInitTimeout").toString()));
				eUnit.setMoveTimeout(Integer.parseInt(interpreter.get("horPushBoardMoveTimeout").toString()));
				eUnit.setMinPosition(Integer.parseInt(interpreter.get("horPushBoardMinPosition").toString()));
				eUnit.setMaxPosition(Integer.parseInt(interpreter.get("horPushBoardMaxPosition").toString()));
				ControlParamters.equimentUnitMap.put("EU_E", eUnit);
			}
			// ��ҩ��
			{
				EquitmentUnit eUnit = new EquitmentUnit("EU_F");
				eUnit.setInitTimeout(Integer.parseInt(interpreter.get("throughDoorInitTimeout").toString()));
				eUnit.setMoveTimeout(Integer.parseInt(interpreter.get("throughDoorMoveTimeout").toString()));
				eUnit.setMinPosition(Integer.parseInt(interpreter.get("throughDoorMinPosition").toString()));
				eUnit.setMaxPosition(Integer.parseInt(interpreter.get("throughDoorMaxPosition").toString()));
				ControlParamters.equimentUnitMap.put("EU_F", eUnit);
			}
			
			// �豸Ӳ����ʼ��ʼ��
			LogWrite.println("���豸�ײ�Ӳ����ʼ���г�ʼ������");
			String[] parts = interpreter.get("needInitUnit").toString().split(",");
			for (int i = 0; i < parts.length; i++) {
				if (parts[i].equals("EU_A")) {
					LogWrite.println("EU_A��ʼ��ʼ��");
					new Thread(new LifterInitialize(0)).start();
				} else if (parts[i].equals("EU_B")) {
					LogWrite.println("EU_B��ʼ��ʼ��");
					new Thread(new XInitialize(0)).start();
				} else if (parts[i].equals("EU_C")) {
					LogWrite.println("EU_C��ʼ��ʼ��");
					new Thread(new YInitialize(0)).start();
				} else if (parts[i].equals("EU_D")) {
					LogWrite.println("EU_D��ʼ��ʼ��");
					new Thread(new VerPushBoardInitialize(0)).start();
				} else if (parts[i].equals("EU_E")) {
					LogWrite.println("EU_E��ʼ��ʼ��");
					new Thread(new HorPushBoardInitialize(0)).start();
				} else if (parts[i].equals("EU_F")) {
					LogWrite.println("EU_F��ʼ��ʼ��");
					new Thread(new ThroughDoorInitialize(0)).start();
				} else {
					LogWrite.println("������������" + parts[i]);
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
						LogWrite.println("������������" + parts[i]);
					}//end if
				}//end for
			}//end while
			
			LogWrite.println(">>>>>>>>>>>>>>>>>>>>ϵͳ��ʼ�����<<<<<<<<<<<<<<<<<<<<");
		} catch (Exception e) {
			e.printStackTrace();
			LogWrite.println(e);
		}
	}

}
