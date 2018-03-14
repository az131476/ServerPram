package us;

import bsh.Interpreter;



public class OutBeltDoorState {
	static Interpreter interpreter = new Interpreter();
	
	static {
		try {
			interpreter.source(InitParamters.configPath + "front.script");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static boolean initialize() {
		int hasInitialized = 0;
		LogWrite.println("׼��ִ��Ƥ���������ʼ��");
		try {
			for (int i = 1; i < 3; i++) {
				boolean needCheck = (Boolean)interpreter.get("beltOutDrugDoor_" + i);
				if (!needCheck) {
					LogWrite.println(i+"�ŷ��崦��ͣ��״̬��������");
					hasInitialized++;
					continue;
				}
				
				String stateCode = "";
				String readStateStr = interpreter.get("readStateCode_"+i).toString();

				LogWrite.println(i+"�ŷ����⿪ʼ");
				
				while (true) {
					// ���״̬
					synchronized (SerialPortEx.LOCK_SERIALPORT) {
						int waittime = 0;
						SerialPortEx.serialPortExHashMap.get(i==1?ASerialPortType.DOOR_LEFT:ASerialPortType.DOOR_RIGHT).writeComm(readStateStr, 16);
						while (true) {
							stateCode = SerialPortEx.serialPortExHashMap.get(i==1?ASerialPortType.DOOR_LEFT:ASerialPortType.DOOR_RIGHT).getRecvData();
							if (stateCode.equals("")) {
								if (waittime == 20) {
									LogWrite.println("����״̬����ָ��ʧ�ܣ����·���");
									SerialPortEx.serialPortExHashMap.get(i==1?ASerialPortType.DOOR_LEFT:ASerialPortType.DOOR_RIGHT).writeComm(readStateStr, 16);
								} else if (waittime > 40){
									LogWrite.println("����״̬����ָ��ʧ�ܣ����·���Ҳ����Ч���������");
									break;
								}
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							} else {
								LogWrite.println("����״̬����ָ��"+ stateCode);
								break;
							}
							waittime++;
						}//end while
					}
					
					// ���״̬�Ƿ�����
					String closeCode = (String) interpreter.eval("checkState_"+i+"(\"" + stateCode + "\")");
					if ("".equals(closeCode)) {
						LogWrite.println(i+"�ŷ��崦�ڹر�״̬");
						hasInitialized++;
						break;
					} else {
						LogWrite.println(i+"�ŷ��崦�ڴ�״̬��׼���رգ��ر�ָ�"+closeCode);
						// �ر�
						synchronized (SerialPortEx.LOCK_SERIALPORT) {
							int waittime = 0;
							SerialPortEx.serialPortExHashMap.get(i==1?ASerialPortType.DOOR_LEFT:ASerialPortType.DOOR_RIGHT).writeComm(closeCode, 16);
							while (true) {
								stateCode = SerialPortEx.serialPortExHashMap.get(i==1?ASerialPortType.DOOR_LEFT:ASerialPortType.DOOR_RIGHT).getRecvData();
								if (stateCode.equals("")) {
									if (waittime == 20) {
										LogWrite.println("����δ�رգ��ٴιرշ���ָ��ʧ�ܣ����·���");
										SerialPortEx.serialPortExHashMap.get(i==1?ASerialPortType.DOOR_LEFT:ASerialPortType.DOOR_RIGHT).writeComm(closeCode, 16);
									} else if (waittime > 40){
										LogWrite.println("����δ�رգ��ٴιرշ���ָ��ʧ�ܣ����·���Ҳ����Ч���������");
										break;
									}
									try {
										Thread.sleep(100);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								} else {
									LogWrite.println("����δ�رգ��ٴιرշ���ָ��"+ stateCode);
									break;
								}
								waittime++;
							}//end while
						}//end synchronized
					}
					
					Thread.sleep(1000);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			LogWrite.println(e);
			return false;
		}
		return (hasInitialized == 2);
	}
	
	public static void openOutBeltDoor(String outterNO){
		synchronized (SerialPortEx.LOCK_SERIALPORT) {
			try {
				String code = interpreter.eval("getOpenCode(\""+outterNO+"\")").toString();
				LogWrite.println("[��ҩ]�����"+code);
				if (outterNO.substring(0, 1).equals("1")) {
					SerialPortEx.serialPortExHashMap.get(ASerialPortType.DOOR_LEFT).writeComm(code, 16);
				} else {
					SerialPortEx.serialPortExHashMap.get(ASerialPortType.DOOR_RIGHT).writeComm(code, 16);
				}
			} catch (Exception e) {
				// TODO: handle exception
				LogWrite.println(e);
			}
		}
	}
	
	public static void closeOutBeltDoor(String outterNO){
		synchronized (SerialPortEx.LOCK_SERIALPORT) {
			try {
				String code = interpreter.eval("getCloseCode(\""+outterNO+"\")").toString();
				LogWrite.println("[��ҩ]����ر�"+code);
				if (outterNO.substring(0, 1).equals("1")) {
					SerialPortEx.serialPortExHashMap.get(ASerialPortType.DOOR_LEFT).writeComm(code, 16);
				} else {
					SerialPortEx.serialPortExHashMap.get(ASerialPortType.DOOR_RIGHT).writeComm(code, 16);
				}
			} catch (Exception e) {
				// TODO: handle exception
				LogWrite.println(e);
			}
		}
	}
}
