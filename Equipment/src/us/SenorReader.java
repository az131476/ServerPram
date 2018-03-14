package us;

import java.io.FileNotFoundException;
import java.io.IOException;



import bsh.EvalError;
import bsh.Interpreter;


public class SenorReader implements Runnable {
	private double length;
	private Interpreter interpreter;
	CkSerialPortEx serialPort;
	
	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public SenorReader(ASerialPortType aSerialPortType) {
		serialPort = CkSerialPortEx.ckSerialPortExHashMap.get(aSerialPortType);
		
		interpreter = new Interpreter();
		try {
			interpreter.source(InitParamters.configPath + (aSerialPortType == ASerialPortType.CK_LEFT ? "check_1" : "check_2") + ".txt");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (EvalError e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		double checkV = 0;
		for (int i = 0; i < 1; i++) {
			String string = "";
			while (string.equals("")) {
				string = serialPort.getRecvData();
			}
			LogWrite.println("检测值返回值："+string);
			try {
				checkV = Double.parseDouble(interpreter.eval("getValue(\"" + string + "\")").toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (checkV > 0) {
				break;
			}
		}
		
		setLength(checkV);
		LogWrite.println(" 测的距离"+checkV);
	}//end run()

	public static void main(String[] args) {
		
	}
}
