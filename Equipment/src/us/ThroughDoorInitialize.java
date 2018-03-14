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
		
		// ��ʼ�����ʱ�䣨�룩��Ϊ0�򲻽��г�ʱ�ж�
		int THROUGHDOOR_INIT_TIMEOUT = ControlParamters.equimentUnitMap.get("EU_F").getInitTimeout();
		
		// ��⼱ͣ�ſ��Ƿ�����
		SpecialStateCheck specialStateCheck = new SpecialStateCheck();
		while (!specialStateCheck.backEmgenceStop()) {
			setErrorCode("EU_F", "11");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LogWrite.println("��˼�ͣ���ſ��쳣���޷����г�ʼ��������");
		}
		while (!(ControlParamters.backAccessControl ? specialStateCheck.backDoorControl() : true)) {
			setErrorCode("EU_F", "12");
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
			setErrorCode("EU_F", "13");
			LogWrite.println("��˶�����Ļ�쳣���޷����г�ʼ��������");
			return;
		}
		setErrorCode("EU_F", "00");
				
		// �жϳ�ҩ���Ƿ��ʼ��
		String rString = Modbus.modbus.executeCode(checkInitCode);
		if (13 != Integer.parseInt(rString.substring(rString.length()-4), 16)) {
			//
		} else {
			if (model == 1) {
				//
			} else {
				setUnitState("EU_F", true);
				LogWrite.println("������ҩ��ҩ�ų�ʼ���Ѿ���ʼ��");
				return;
			}
		}
		
		LogWrite.println("������ҩ��ҩ�ų�ʼ��δ��ʼ������ʼ��ʼ��");
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
				LogWrite.println("������ҩ��ҩ�ų�ʼ�����");
				setUnitState("EU_F", true);
				break;
			} else {
				LogWrite.println("������ҩ��ҩ�����ڳ�ʼ��");
				if (THROUGHDOOR_INIT_TIMEOUT == 0) {
					setErrorCode("EU_F", "21");
					LogWrite.println("������ҩ��ҩ�ų�ʼ����ʱ����ʼ�����ܷ����쳣");
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
