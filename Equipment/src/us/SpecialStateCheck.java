package us;


public class SpecialStateCheck {
	private String checkFrontEmg = "0000000000060103"+ String.format("%04X", 0) +"0001";
	private String checkFrontDoor = "0000000000060103"+ String.format("%04X", 2) +"0001";
	private String checkBackEmg = "0000000000060103"+ String.format("%04X", 1) +"0001";
	private String checkBackDoor = "0000000000060103"+ String.format("%04X", 3) +"0001";
	private String checkHand = "0000000000060103"+ String.format("%04X", 4) +"0001";
	private String checkStock = "0000000000060103"+ String.format("%04X", 5) +"0001";
	
	/**
	 * @return true 正常 false 异常
	 */
	public boolean emgenceStop() {
		String rString = Modbus.modbus.executeCode(checkFrontEmg);
		if (1 - Integer.parseInt(rString.substring(rString.length() - 4), 16) == 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return true 正常 false 异常
	 */
	public boolean doorControl() {
		String rString = Modbus.modbus.executeCode(checkFrontDoor);
		if (1 - Integer.parseInt(rString.substring(rString.length() - 4), 16) == 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return true 正常 false 异常
	 */
	public boolean backEmgenceStop() {
		String rString = Modbus.modbus.executeCode(checkBackEmg);
		if (1 - Integer.parseInt(rString.substring(rString.length() - 4), 16) == 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return true 正常 false 异常
	 */
	public boolean backDoorControl() {
		String rString = Modbus.modbus.executeCode(checkBackDoor);
		if (1 - Integer.parseInt(rString.substring(rString.length() - 4), 16) == 0) {
			LogWrite.println("backDoorControl:"+"false");
			return false;
		}
		LogWrite.println("backDoorControl:"+"true");
		return true;
	}
	
	/**
	 * @return true 正常 false 异常
	 */
	public boolean backSensor() {
		String rString = Modbus.modbus.executeCode(checkHand);
		if (1 - Integer.parseInt(rString.substring(rString.length() - 4), 16) == 0) {
			return false;
		} 
		rString = Modbus.modbus.executeCode(checkStock);
		if (1 - Integer.parseInt(rString.substring(rString.length() - 4), 16) == 0) {
			return false;
		}
		return true;
	}
}
