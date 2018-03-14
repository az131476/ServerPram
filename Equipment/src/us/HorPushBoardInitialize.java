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
		
		// ��ʼ�����ʱ�䣨�룩��Ϊ0�򲻽��г�ʱ�ж�
		int PBOD_BACK_TO_ZERO = ControlParamters.equimentUnitMap.get("EU_E").getInitTimeout();
		
		// ��⼱ͣ�ſ��Ƿ�����
		SpecialStateCheck specialStateCheck = new SpecialStateCheck();
		while (!specialStateCheck.backEmgenceStop()) {
			setErrorCode("EU_E", "11");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LogWrite.println("��˼�ͣ���ſ��쳣���޷����г�ʼ��������");
		}
		while (!(ControlParamters.backAccessControl ? specialStateCheck.backDoorControl() : true)) {
			setErrorCode("EU_E", "12");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LogWrite.println("��˼�ͣ���ſ��쳣���޷����г�ʼ��������");
		}
		
		// ����˶��估��ҩ�����Ƿ�����������ͨ����ҩ���濪ʼ��ʼ��
		if (!specialStateCheck.backSensor()) {
			setErrorCode("EU_E", "13");
			LogWrite.println("��˶�����Ļ�쳣���޷����г�ʼ��������");
			return;
		}
		setErrorCode("EU_E", "00");
		
		// �ж���ҩ�Ƿ��ʼ��
		String rString = Modbus.modbus.executeCode(checkInitCode);
		if (13 != Integer.parseInt(rString.substring(rString.length()-4), 16)) {
			//
		} else {
			if (model == 1) {
				//
			} else {
				setUnitState("EU_E", true);
				LogWrite.println("������ҩ��ҩ�ѳ�ʼ��");
				return;
			}
		}
		
		LogWrite.println("������ҩ��ҩ��ʼ��ʼ��");
		// ������ҩ��ҩ��ʼ��
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
				LogWrite.println("������ҩ��ҩ��ʼ�����");
				setUnitState("EU_E", true);
				break;
			} else {
				LogWrite.println("������ҩ��ҩ���ڳ�ʼ��");
				if (PBOD_BACK_TO_ZERO == 0) {
					setErrorCode("EU_E", "21");
					LogWrite.println("������ҩ��ҩ��ʼ����ʱ����ʼ�����ܷ����쳣");
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
