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
	//01串口1打开失败，02串口2，03PLC连接失败
	//11急停按下，12门控异常，13上药断对射异常，14翻板关闭异常
	//21回零超时，22运动超时
	//31/32/33/34/35/36 1/2/3/4/5/6区异常，30检测时通讯异常
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
