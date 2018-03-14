package us;

import java.io.FileNotFoundException;
import java.io.IOException;

import bsh.EvalError;
import bsh.Interpreter;


public class FUnitMotion implements Runnable {
	protected int model;
	protected int position;
	protected int speed;
	protected int horPosition;
	protected int horSpeed;
	protected int verPosition;
	protected int verSpeed;
	protected int timeOut;
	protected Interpreter interpreter = new Interpreter();
	protected AMotion aMotion;
	
	// 01通讯异常，02急停，03门控，04后侧门控
	// 11未回零，02回零异常，11运动异常
	// 21运动超时，22运动过载
	protected String errCode = "00";
	
	public FUnitMotion(int position) {
		try {
			interpreter.source(InitParamters.configPath + "unitmotion.script");
			this.position = position;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (EvalError e1) {
			e1.printStackTrace();
		}
	}
	
	public FUnitMotion(AMotion aMotion, int horPosition, int verPosition) {
		try {
			interpreter.source(InitParamters.configPath + "unitmotion.script");
			this.aMotion = aMotion;
			this.horPosition = horPosition;
			this.verPosition = verPosition;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (EvalError e1) {
			e1.printStackTrace();
		}
	}

	public int getModel() {
		return model;
	}

	public void setModel(int model) {
		this.model = model;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getHorPosition() {
		return horPosition;
	}

	public void setHorPosition(int horPosition) {
		this.horPosition = horPosition;
	}

	public int getHorSpeed() {
		return horSpeed;
	}

	public void setHorSpeed(int horSpeed) {
		this.horSpeed = horSpeed;
	}

	public int getVerPosition() {
		return verPosition;
	}

	public void setVerPosition(int verPosition) {
		this.verPosition = verPosition;
	}

	public int getVerSpeed() {
		return verSpeed;
	}

	public void setVerSpeed(int verSpeed) {
		this.verSpeed = verSpeed;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
