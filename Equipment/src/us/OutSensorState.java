package us;

import bsh.Interpreter;



public class OutSensorState {
	static Interpreter interpreter = new Interpreter();
	
	static {
		try {
			interpreter.source(InitParamters.configPath + "front.script");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static String getState() {
		LogWrite.println("��ҩ����״̬��⿪ʼ");
		// �жϵ�ǰ��ҩ����	�ж���ɺ�ʼ��ҩ����
		try {
			String code = "";
			String checkStateCode = interpreter.get("readOurDrugSensorCode").toString();
			synchronized (SerialPortEx.LOCK_SERIALPORT) {
				int waittime = 0;
				SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm(checkStateCode, 16);
				while (true) {
					code = SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).getRecvData();
					
					if (code.equals("")) {
						if (waittime == 20) {
							LogWrite.println("���շ���ָ��ʧ�ܣ����·���");
							SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm(checkStateCode, 16);
						} else if (waittime > 40){
							LogWrite.println("���շ���ָ��ʧ�ܣ��ٴη��ͺ���շ���ָ��ʧ�ܣ������쳣���������");
							break;
						}
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						LogWrite.println("�����쳣��ⷵ��ָ��"+ code);
						break;
					}
					waittime++;
				}//end while
			}//end synchronized
			
			String stateCode = (String) interpreter.eval("checkOurDrugSensorState(\"" + code + "\")");
			LogWrite.println("��ҩ����״̬������ "+stateCode);
			return stateCode;
		} catch (Exception e) {
			// TODO: handle exception
			LogWrite.println(e);
		}
		LogWrite.println("��ҩ����״̬����쳣__________ERROR ");
		return "30";
	}
}
