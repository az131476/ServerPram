package us;



public class YInitialize extends FUnitInitialize {
	private String initCode = "0000000000060106"+ String.format("%04X", 72) +"0003";
	private String checkInitCode = "0000000000060103"+ String.format("%04X", 76) +"0001";
	
	public YInitialize(int model) {
		super(model);
		// TODO Auto-generated constructor stub
	}
	
	public void run() {
		setUnitState("EU_C", false);
		
		// ��ʼ�����ʱ�䣨�룩��Ϊ0�򲻽��г�ʱ�ж�
		int Y_INIT_TIMEOUT = ControlParamters.equimentUnitMap.get("EU_C").getInitTimeout();
		
		try {
		//	int yminposition = ControlParamters.equimentUnitMap.get("EU_C").getMinPosition();
		//	int ymaxposition = ControlParamters.equimentUnitMap.get("EU_C").getMaxPosition();
		//	LogWrite.println("д���˶���Χ��"+yminposition+"     "+ymaxposition);
		//	String sposition = "";
			
		//	sposition = String.format("%08X", yminposition);
		//	Modbus.modbus.executeCode(PlcCodeString.W_AY_MIN_POSITION_STR + sposition.substring(sposition.length()-4) + sposition.substring(sposition.length()-8, sposition.length()-4));
			
		//	sposition = String.format("%08X", ymaxposition);
		//	Modbus.modbus.executeCode(PlcCodeString.W_AY_MAX_POSITION_STR + sposition.substring(sposition.length()-4) + sposition.substring(sposition.length()-8, sposition.length()-4));
			
		} catch (Exception e1) {
			LogWrite.println("Yδ�����˶���Χ");
		}
		
		// ��⼱ͣ�ſ��Ƿ�����
		SpecialStateCheck specialStateCheck = new SpecialStateCheck();
		while (!specialStateCheck.backEmgenceStop()) {
			setErrorCode("EU_C", "11");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LogWrite.println("��˼�ͣ���ſ��쳣���޷����г�ʼ��������");
		}
		while (!(ControlParamters.backAccessControl ? specialStateCheck.backDoorControl() : true)) {
			setErrorCode("EU_C", "12");
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
			setErrorCode("EU_C", "13");
			LogWrite.println("��˶�����Ļ�쳣���޷����г�ʼ��������");
			return;
		}
		setErrorCode("EU_C", "00");
		
		// �ж�Y���Ƿ��ʼ��
		String rString2 = Modbus.modbus.executeCode(checkInitCode);
		if (13 != Integer.parseInt(rString2.substring(rString2.length()-4), 16)) {
			//
		} else {
			if (model == 1) {
				//
			} else {
				setUnitState("EU_C", true);
				LogWrite.println("Y���ѳ�ʼ��");
				return;
			}
		}
		
		LogWrite.println("Y�Ὺʼ��ʼ��");
		// Y���ʼ��
		Modbus.modbus.executeCode(initCode);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// �ж�Y���Ƿ��ʼ�����
		while (true && Y_INIT_TIMEOUT-- > 0) {
			rString2 = Modbus.modbus.executeCode(checkInitCode);
			if (13 == Integer.parseInt(rString2.substring(rString2.length()-4), 16)) {
				LogWrite.println("Y���ʼ�����");
				setUnitState("EU_C", true);
				break;
			} else {
				LogWrite.println("Y�����ڳ�ʼ��");
				if (Y_INIT_TIMEOUT == 0) {
					setErrorCode("EU_C", "01");
					LogWrite.println("Y���ʼ����ʱ����ʼ�����ܷ����쳣");
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}//end while
	}

}
