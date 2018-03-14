package us;



public class ModbusState {
	private static boolean normal;
	public static boolean isNormal() {
		return normal;
	}
	
	public static boolean getState(String ip) {
		try {
			LogWrite.println("※连接MODBUS......");
			Modbus.modbus = new Modbus(ip, 502);
			if ("".equals(Modbus.modbus.executeCode("000000000006010300010001"))) {
				throw new NullPointerException();
			}
			normal = true;
			LogWrite.println("※MODBUS连接成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			normal = false;
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		System.out.println(("$Q999000000*".substring(0, 5)).equals("$Q999"));
	}
}
