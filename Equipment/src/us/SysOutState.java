package us;


public class SysOutState {
	private static String procErrorCode = "00";//99 ���ش���02 ��ͣ��03 �ſأ�11 δ���㣬21 �˶���ʱ��30 ��ҩ���쳣��31/32/33 N���쳣
	private static boolean solve = false;
	private static boolean beltStop = false;
	
	public static String getEmgenceState(){
		try {
			return new SpecialStateCheck().emgenceStop() ? "00" : "01";
		} catch (Exception e) {
			// TODO: handle exception
			return "01";
		}
	}
	
	public static String getDoorState(){
		try {
			return new SpecialStateCheck().doorControl() ? "00" : "01";
		} catch (Exception e) {
			// TODO: handle exception
			return "01";
		}
	}
	
	public static String getPorcErrorCode(){
		return procErrorCode;
	}
	
	public static void setProcErrorCode(String error) {
		procErrorCode = error;
	}
	
	public static boolean isSolved() {
		return solve;
	}
	
	public static void setSolved(boolean flag) {
		SysOutState.solve = flag;
	}

	public static boolean isBeltStop() {
		return beltStop;
	}

	public static void setBeltStop(boolean beltStop) {
		SysOutState.beltStop = beltStop;
	}
}
