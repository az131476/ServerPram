package us;

public class EquitmentUnit {
	private String unitName;
	private boolean initState;
	private int minPosition;
	private int maxPosition;
	private int initTimeout;
	private int moveTimeout;
	private int minSpeed;
	private int maxSpeed;
	private int speed;
	//01����1��ʧ�ܣ�02����2��03PLC����ʧ��
	//11��ͣ���£�12�ſ��쳣��13��ҩ�϶����쳣��14����ر��쳣
	//21���㳬ʱ��22�˶���ʱ
	//31/32/33/34/35/36 1/2/3/4/5/6���쳣��30���ʱͨѶ�쳣
	private String errCode;
	
	public EquitmentUnit() {
		
	}
	
	public EquitmentUnit(String unitName) {
		this.unitName = unitName;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public boolean isInitState() {
		return initState;
	}

	public void setInitState(boolean initState) {
		this.initState = initState;
	}

	public int getMinPosition() {
		return minPosition;
	}

	public void setMinPosition(int minPosition) {
		this.minPosition = minPosition;
	}

	public int getMaxPosition() {
		return maxPosition;
	}

	public void setMaxPosition(int maxPosition) {
		this.maxPosition = maxPosition;
	}

	public int getInitTimeout() {
		return initTimeout;
	}

	public void setInitTimeout(int initTimeout) {
		this.initTimeout = initTimeout;
	}

	public int getMoveTimeout() {
		return moveTimeout;
	}

	public void setMoveTimeout(int moveTimeout) {
		this.moveTimeout = moveTimeout;
	}

	public int getMinSpeed() {
		return minSpeed;
	}

	public void setMinSpeed(int minSpeed) {
		this.minSpeed = minSpeed;
	}

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public String getErrCode() {
		return (errCode == null || errCode.equals("")) ? "00" : errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

}
