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
		
		// ��ʼ�����ʱ�䣨�룩��Ϊ0�򲻽��г�ʱ�ж�
		int LIFTER_INIT_TIMEOUT = ControlParamters.equimentUnitMap.get("EU_A").getInitTimeout();
		
		try {
		//	int minposition = ControlParamters.equimentUnitMap.get("EU_A").getMinPosition();
		//	int maxposition = ControlParamters.equimentUnitMap.get("EU_A").getMaxPosition();
		//	LogWrite.println("д���˶����Ʒ�Χ��"+minposition+"    "+maxposition);
		//	String sposition = "";
			
		//	sposition = String.format("%08X", minposition);
		//	Modbus.modbus.executeCode(PlcCodeString.W_AL_MIN_POSITION_STR + sposition.substring(sposition.length()-4) + sposition.substring(sposition.length()-8, sposition.length()-4));

		//	sposition = String.format("%08X", maxposition);
		//	Modbus.modbus.executeCode(PlcCodeString.W_AL_MAX_POSITION_STR + sposition.substring(sposition.length()-4) + sposition.substring(sposition.length()-8, sposition.length()-4));
		} catch (Exception e1) {
			LogWrite.println("δ�����˶���Χ");
		}
		
		// ������״̬
		String cState = OutSensorState.getState();
		if (!"00".equals(cState)) {
			setErrorCode("EU_A", cState);
			return;
		}
		
		// ��⼱ͣ�Ƿ�����
		SpecialStateCheck specialStateCheck = new SpecialStateCheck();
		while (!specialStateCheck.emgenceStop()) {
			setErrorCode("EU_A", "11");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LogWrite.println("�豸ǰ��ͣ���ڼ�ͣ״̬���޷����г�ʼ��������");
		}
		
		// ����ſ��Ƿ�����
		while (!(ControlParamters.frontAccessControl ? specialStateCheck.doorControl() : true)) {
			setErrorCode("EU_A", "12");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LogWrite.println("�������ſ��쳣���޷����г�ʼ��������");
		}
		
		setErrorCode("EU_A", "00");
		
		// �ж��������Ƿ��ʼ�� 13�������
		String rString = Modbus.modbus.executeCode(checkInitCode);
		if (13 != Integer.parseInt(rString.substring(rString.length()-4), 16)) { 
			//
		} else {
			if (model == 1) {
				//
			} else {
				LogWrite.println("�������ѳ�ʼ��");
				setUnitState("EU_A", true);
				return;
			}
		}//end if
		
		LogWrite.println("��������ʼ��ʼ����"+initCode);
		setUnitState("EU_A", false);
		// ��������ʼ��
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
				LogWrite.println("��������ʼ�����");
				setUnitState("EU_A", true);
				break;
			} else {
				if (LIFTER_INIT_TIMEOUT == 0) {
					setErrorCode("EU_A", "21");
					LogWrite.println("��������ʼ����ʱ����ʼ�����ܷ����쳣");
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				LogWrite.println("���������ڳ�ʼ����"+rString);
			}
		}
	}
}
