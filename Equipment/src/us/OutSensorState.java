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
		LogWrite.println("出药对射状态检测开始");
		// 判断当前出药对射	判断完成后开始出药动作
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
							LogWrite.println("接收返回指令失败，重新发送");
							SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG).writeComm(checkStateCode, 16);
						} else if (waittime > 40){
							LogWrite.println("接收返回指令失败，再次发送后接收返回指令失败，串口异常，跳出检测");
							break;
						}
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						LogWrite.println("对射异常检测返回指令"+ code);
						break;
					}
					waittime++;
				}//end while
			}//end synchronized
			
			String stateCode = (String) interpreter.eval("checkOurDrugSensorState(\"" + code + "\")");
			LogWrite.println("出药对射状态检测完毕 "+stateCode);
			return stateCode;
		} catch (Exception e) {
			// TODO: handle exception
			LogWrite.println(e);
		}
		LogWrite.println("出药对射状态检测异常__________ERROR ");
		return "30";
	}
}
