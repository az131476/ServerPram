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
		
		// ��ʼ�����ʱ�䣨�룩��Ϊ0�򲻽��г�ʱ�ж�
		int X_INIT_TIMEOUT = ControlParamters.equimentUnitMap.get("EU_B").getInitTimeout();
		
		try {
		//	int xminposition = ControlParamters.equimentUnitMap.get("EU_B").getMinPosition();
		//	int xmaxposition = ControlParamters.equimentUnitMap.get("EU_B").getMaxPosition();
		//	LogWrite.println("д���˶���Χ��"+xminposition+"    "+xmaxposition);
		//	String sposition = "";
		//	sposition = String.format("%08X", xminposition);
		//	Modbus.modbus.executeCode(PlcCodeString.W_AX_MIN_POSITION_STR + sposition.substring(sposition.length()-4) + sposition.substring(sposition.length()-8, sposition.length()-4));
			
		//	sposition = String.format("%08X", xmaxposition);
		//	Modbus.modbus.executeCode(PlcCodeString.W_AX_MAX_POSITION_STR + sposition.substring(sposition.length()-4) + sposition.substring(sposition.length()-8, sposition.length()-4));
		} catch (Exception e1) {
			LogWrite.println("XYδ�����˶���Χ");
		}
		
		// ��⼱ͣ�ſ��Ƿ�����
		SpecialStateCheck specialStateCheck = new SpecialStateCheck();
		while (!specialStateCheck.backEmgenceStop()) {
			setErrorCode("EU_B", "11");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LogWrite.println("��˼�ͣ���ſ��쳣���޷����г�ʼ��������");
		}
		while (!(ControlParamters.backAccessControl ? specialStateCheck.backDoorControl() : true)) {
			setErrorCode("EU_B", "12");
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
			setErrorCode("EU_B", "13");
			LogWrite.println("��˶�����Ļ�쳣���޷����г�ʼ��������");
			return;
		}
		setErrorCode("EU_B", "00");
		
		// �ж�X���Ƿ��ʼ��
		String rString = Modbus.modbus.executeCode(checkInitCode);
		if (13 != Integer.parseInt(rString.substring(rString.length()-4), 16)) {
			//
		} else {
			if (model == 1) {
				//
			} else {
				LogWrite.println("X���ѳ�ʼ��");
				setUnitState("EU_B", true);
				return;
			}
		}
		
		LogWrite.println("X��δ��ʼ������ʼ��ʼ��");
		Modbus.modbus.executeCode(initCode);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// �ж�X���Ƿ��ʼ�����
		while (true && X_INIT_TIMEOUT-- > 0) {
			rString = Modbus.modbus.executeCode(checkInitCode);
			
			if (13 == Integer.parseInt(rString.substring(rString.length()-4), 16)) {
				LogWrite.println("X���ʼ�����");
				setUnitState("EU_B", true);
				break;
			} else {
				LogWrite.println("X�����ڳ�ʼ��");
				if (X_INIT_TIMEOUT == 0) {
					setErrorCode("EU_B", "21");
					LogWrite.println("X���ʼ����ʱ����ʼ�����ܷ����쳣");
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
