package us;



public class LifterMotion extends FUnitMotion {
	private String checkInitCode = "0000000000060103"+ String.format("%04X", 64) + "0001";
	private String curPositionCode = "0000000000060103"+ String.format("%04X", 22) + "0001";
	private String aimPositionCode = "0000000000060106"+ String.format("%04X", 20);
	private String speedCode = "0000000000060106"+ String.format("%04X", 62);
	private String motionCode = "0000000000060106"+ String.format("%04X", 60) + "0001";
	private String checkMotionCode = "0000000000060103"+ String.format("%04X", 60) +"0001";
	
	public LifterMotion(int position) {
		super(position);
		try {
			speed = (Integer)interpreter.get("lifterSpeed");
			timeOut = 10 * (Integer)ControlParamters.equimentUnitMap.get("EU_A").getMoveTimeout();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getCurrentPosition() {
		String rString = Modbus.modbus.executeCode(curPositionCode);
		try {
			return Integer.parseInt(rString.substring(rString.length() - 4), 16) + "";
		} catch (Exception e) {
			// TODO: handle exception
			LogWrite.println("EU_A����ȡ������λ�ó���"+rString);
			LogWrite.println(e);
			
			rString = Modbus.modbus.executeCode(curPositionCode);

			try {
			//	double position2 = HexToInt.HexToDouble(rString.substring(rString.length()-4)+rString.substring(rString.length()-8, rString.length()-4));
			//	return String.valueOf((int) position2);
				return Integer.parseInt(rString.substring(rString.length() - 4), 16) + "";
			} catch (Exception e1) {
				// TODO: handle exception
				LogWrite.println("EU_A����ȡ������λ�ó���2"+rString);
				LogWrite.println(e1);
				return "Error";
			}
		}
	}
	
	public void run() {
		try {
			LogWrite.println("EU_A��������ִ���˶���Ŀ��λ�ã�"+position+" �ٶȣ�"+speed);
			
			setErrCode("00");
			
			// �ж�Ƥ�����Ƿ��ʼ��
			String rString = Modbus.modbus.executeCode(checkInitCode);  //=13������� =11���ڻ���
			if (13 - Integer.parseInt(rString.substring(rString.length()-4), 16) != 0){
				LogWrite.println("EU_A��������δִ�г�ʼ�����޷��˶���"+rString);
				setErrCode("11");
				return;
			}
			// ��ͣ
			// ��⼱ͣ�Ƿ�����
			SpecialStateCheck specialStateCheck = new SpecialStateCheck();
			if (!specialStateCheck.emgenceStop()) {
				setErrCode("02");
				LogWrite.println("EU_A���豸ǰ��ͣ���ڼ�ͣ״̬���޷����г�ʼ��������");
				return;
			}
			
			// �ſ�
			// ����ſ��Ƿ�����
			if (!(ControlParamters.frontAccessControl ? specialStateCheck.doorControl() : true)) {
				setErrCode("03");
				LogWrite.println("EU_A����������ͣ���ſ��쳣���޷����г�ʼ��������");
				return;
			}
			
			// �������ٶ�
			String speedS = String.format("%04X", speed);
			Modbus.modbus.executeCode(speedCode + speedS);
			
			String sposition = String.format("%04X", this.position);
			
			Modbus.modbus.executeCode(aimPositionCode + sposition);
			
			// �˶�
			System.out.println("����ִ���˶���PLC������" + Modbus.modbus.executeCode(motionCode));
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			while (true && timeOut > 0) {
				// �Ƿ񵽴�ָ��λ��
				rString = Modbus.modbus.executeCode(curPositionCode);
			//	double position2 = HexToInt.HexToDouble(rString.substring(rString.length()-4)+rString.substring(rString.length()-8, rString.length()-4));
				int position2 = Integer.parseInt(rString.substring(rString.length() - 4), 16);
				if (Math.abs(this.position - position2) < 2) {
					LogWrite.println("EU_A��������λ���˶���λ");
					break;
				} else {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					timeOut -=  2;
				}
				
				// �˶��Ƿ���ּ�ͣ
				rString = Modbus.modbus.executeCode(checkMotionCode);
				if (Integer.parseInt(rString.substring(rString.length() - 4), 16) > 90) {
					setErrCode("02");
					return;
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					timeOut -= 1;
				}
			}	
			
			while (true && timeOut > 0) {
				// �˶��Ƿ����
				rString = Modbus.modbus.executeCode(checkMotionCode);
				if (22 - Integer.parseInt(rString.substring(rString.length() - 4), 16) == 0) {
					LogWrite.println("EU_A���������˶�����");
					break;
				} else if (Integer.parseInt(rString.substring(rString.length() - 4), 16) > 90) {
					setErrCode("02");
					return;
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					timeOut -= 1;
				}
			}
			
			// �˶���ʱ��
			if (timeOut < 1) {
				setErrCode("21");
				return;
			}
			LogWrite.println("EU_A������������ִ�����");
		} catch (Exception e) {
			// TODO: handle exception
			setErrCode("99");
			LogWrite.println("EU_A���˶��������ش���");
			LogWrite.println(e);
		}
	}
}
