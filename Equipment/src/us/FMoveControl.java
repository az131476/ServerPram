package us;

import java.io.FileNotFoundException;
import java.io.IOException;


import bsh.EvalError;
import bsh.Interpreter;

public class FMoveControl {
	protected int model;// model=0发药模式、=1手工运动模式
	protected int position;
	protected int yposition;
	protected int speed;
	protected int yspeed;
	protected int timeOut;
	protected Interpreter interpreter;
	public static boolean backSysStop = false;
	public static boolean frontSysStop = false;
	
	public FMoveControl(String code){
		// @P100 0(设备ID位数) ??(设备ID) 0(按绝对位置) 0(速度位数) ??(速度) 1(后面坐标的位数) ??? 00*
		code = code.substring(6  + Integer.parseInt(code.substring(5, 6)), code.length() - 3);
		// 设置速度
		speed = Integer.parseInt(code.substring(2, 2 + Integer.parseInt(code.substring(1, 2))));
		code = code.substring(2 + Integer.parseInt(code.substring(1, 2)));
		// 设置位置
		position = Integer.parseInt(code.substring(1));
	}
	
	public FMoveControl(int position){
		this.position = position;
		try {
			interpreter.source(InitParamters.configPath + "param.txt");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (EvalError e1) {
			e1.printStackTrace();
		}
	}
	
	public FMoveControl(int model, int position){
		this.model = model;
		try {
			interpreter.source(InitParamters.configPath + "param.txt");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (EvalError e1) {
			e1.printStackTrace();
		}
	}
	
	public FMoveControl(int model, int position, int position2){
		this.model = model;
		try {
			interpreter.source(InitParamters.configPath + "param.txt");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (EvalError e1) {
			e1.printStackTrace();
		}
	}
}
