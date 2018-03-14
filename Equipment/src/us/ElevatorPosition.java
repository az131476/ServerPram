package us;

import java.util.HashMap;

import bsh.Interpreter;


public class ElevatorPosition {
	public static HashMap<Integer, Integer> positionHashMap = new HashMap<Integer, Integer>();

	public static void init() {
		Interpreter interpreter = new Interpreter();
		try {
			interpreter.source(InitParamters.configPath + "front.script");
			for (int i = 1; i < 20; i++) {
				ElevatorPosition.positionHashMap.put(i, Integer.parseInt(interpreter.get("rowHeight_" + i).toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
