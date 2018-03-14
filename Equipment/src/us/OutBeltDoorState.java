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
		LogWrite.println("准备执行皮带机翻板初始化");
		try {
			for (int i = 1; i < 3; i++) {
				boolean needCheck = (Boolean)interpreter.get("beltOutDrugDoor_" + i);
				if (!needCheck) {
					LogWrite.println(i+"号翻板处于停用状态，无需检测");
					hasInitialized++;
					continue;
				}
				
				String stateCode = "";
				String readStateStr = interpreter.get("readStateCode_"+i).toString();

				LogWrite.println(i+"号翻板检测开始");
				
				while (true) {
					// 检测状态
					synchronized (SerialPortEx.LOCK_SERIALPORT) {
						int waittime = 0;
						SerialPortEx.serialPortExHashMap.get(i==1?ASerialPortType.DOOR_LEFT:ASerialPortType.DOOR_RIGHT).writeComm(readStateStr, 16);
						while (true) {
							stateCode = SerialPortEx.serialPortExHashMap.get(i==1?ASerialPortType.DOOR_LEFT:ASerialPortType.DOOR_RIGHT).getRecvData();
							if (stateCode.equals("")) {
								if (waittime == 20) {
									LogWrite.println("翻板状态返回指令失败，重新发送");
									SerialPortEx.serialPortExHashMap.get(i==1?ASerialPortType.DOOR_LEFT:ASerialPortType.DOOR_RIGHT).writeComm(readStateStr, 16);
								} else if (waittime > 40){
									LogWrite.println("翻板状态返回指令失败，重新发送也是无效，跳出检测");
									break;
								}
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							} else {
								LogWrite.println("翻板状态返回指令"+ stateCode);
								break;
							}
							waittime++;
						}//end while
					}
					
					// 检测状态是否正常
					String closeCode = (String) interpreter.eval("checkState_"+i+"(\"" + stateCode + "\")");
					if ("".equals(closeCode)) {
						LogWrite.println(i+"号翻板处于关闭状态");
						hasInitialized++;
						break;
					} else {
						LogWrite.println(i+"号翻板处于打开状态，准备关闭，关闭指令："+closeCode);
						// 关闭
						synchronized (SerialPortEx.LOCK_SERIALPORT) {
							int waittime = 0;
							SerialPortEx.serialPortExHashMap.get(i==1?ASerialPortType.DOOR_LEFT:ASerialPortType.DOOR_RIGHT).writeComm(closeCode, 16);
							while (true) {
								stateCode = SerialPortEx.serialPortExHashMap.get(i==1?ASerialPortType.DOOR_LEFT:ASerialPortType.DOOR_RIGHT).getRecvData();
								if (stateCode.equals("")) {
									if (waittime == 20) {
										LogWrite.println("翻板未关闭，再次关闭返回指令失败，重新发送");
										SerialPortEx.serialPortExHashMap.get(i==1?ASerialPortType.DOOR_LEFT:ASerialPortType.DOOR_RIGHT).writeComm(closeCode, 16);
									} else if (waittime > 40){
										LogWrite.println("翻板未关闭，再次关闭返回指令失败，重新发送也是无效，跳出检测");
										break;
									}
									try {
										Thread.sleep(100);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								} else {
									LogWrite.println("翻板未关闭，再次关闭返回指令"+ stateCode);
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
				LogWrite.println("[出药]翻板打开"+code);
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
				LogWrite.println("[出药]翻板关闭"+code);
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
