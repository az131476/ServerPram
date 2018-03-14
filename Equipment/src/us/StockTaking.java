package us;

import bsh.Interpreter;


public class StockTaking implements Runnable {
	protected Interpreter interpreter = new Interpreter();
	
	public StockTaking() {
		try {
			this.interpreter.source(InitParamters.configPath + "back.script");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public StockTaking(String code) {
		
	}
	
	public int getLen(int side) {
		int ckLen = 0;
		if (side == 1) {
			ckLen = Integer.parseInt(CkSerialPortEx.ckSerialPortExHashMap.get(ASerialPortType.CK_LEFT).getRecvData());
		} else {
			ckLen = Integer.parseInt(CkSerialPortEx.ckSerialPortExHashMap.get(ASerialPortType.CK_RIGHT).getRecvData());
		}
		LogWrite.println("盘点显示长度："+ckLen);
		try {
			ckLen = Integer.parseInt(interpreter.eval("checkLength_"+side+"("+ckLen+")").toString());
		} catch (Exception e) {
			// TODO: handle exception
			return 9999;
		}
		return ckLen;
	}
	
	public int getCount(int ch, int packageLen) {
		return 0;
	}
	
	public int getQuantity(int side, int packageLen) {
		int ckLen = 0;
		if (side == 1) {
			ckLen = Integer.parseInt(CkSerialPortEx.ckSerialPortExHashMap.get(ASerialPortType.CK_LEFT).getRecvData());
		} else {
			ckLen = Integer.parseInt(CkSerialPortEx.ckSerialPortExHashMap.get(ASerialPortType.CK_RIGHT).getRecvData());
		}
		LogWrite.println("盘点显示长度："+ckLen);
		try {
			ckLen = Integer.parseInt(interpreter.eval("checkLength_"+side+"("+ckLen+")").toString());
		} catch (Exception e) {
			// TODO: handle exception
			return -99;
		}
		return (int)(ckLen*1.0/packageLen + 0.5);
	}
	
	public void run() {
		
	}
}
