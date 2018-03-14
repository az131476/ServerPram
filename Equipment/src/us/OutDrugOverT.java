package us;

import bsh.Interpreter;


public class OutDrugOverT implements Runnable {
	private String code;
	protected Interpreter interpreter = new Interpreter();
	
	public OutDrugOverT(String code) {
		this.code = code;
		try {
			this.interpreter.source(InitParamters.configPath + "front.script");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public boolean beltRun(String outterNO) {
		String runCode = "";
		try {
			runCode = interpreter.eval("getBeltMotionCode(\""+ outterNO +"\")").toString();
			if (!runCode.equals("")) {
				Modbus.modbus.executeCode(runCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		LogWrite.println("[出药]升降机皮带开始转动");
		return true;
	}
	
	public boolean beltStop() {
		String runCode = "";
		try {
			runCode = interpreter.get("beltMotionStopCode").toString();
			if (!runCode.equals("")) {
				Modbus.modbus.executeCode(runCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		LogWrite.println("[出药]升降机皮带停止转动");
		return true;
	}
	
	public void run() {
		//$Q407 2 K1 00 00 n ZUHAOXXXX*
		//$Q406 2 F1 1S 01 1220160823002214091005iG6*
		LogWrite.println("[出药]收到翻板打开出药指令");
		String outterNO = code.substring(6 + Integer.parseInt(code.substring(5, 6)));
		outterNO = outterNO.substring(0, 2);
		
		// 转动皮带
		//LogWrite.println("[出药]向出药口转动："+outterNO);
		//beltRun(outterNO);//皮带开始转动
		beltStop();//皮带停止转动
		
		
		//皮带反转1.3秒
		String runCode = "";
		try {
			runCode = interpreter.eval("getBeltOppositeMotionCode(\""+ outterNO +"\")").toString();
			if (!runCode.equals("")) {
				Modbus.modbus.executeCode(runCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		LogWrite.println("[出药]升降机皮带开始反转");
		try {
			Thread.sleep(Integer.parseInt(interpreter.get("rollbacktime").toString())); //1000
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		// 
		OutBeltDoorState.openOutBeltDoor(outterNO);//打开翻板
		try {
			Thread.sleep(Integer.parseInt(interpreter.get("turndoorwaittime").toString())); //1200
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		beltRun(outterNO);//皮带开始转动
		try {
			Thread.sleep(Integer.parseInt(interpreter.get("beltOutDrugDoorInterval").toString()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		OutBeltDoorState.closeOutBeltDoor(outterNO);//关闭翻板
		
		beltStop();//皮带停止转动
		
		// 设备空闲了
		SysOutState.setProcErrorCode("00");
		
		String returnCode = code.replaceAll("Q407", "Q408");
		LogWrite.println("[出药]返回翻板出药完成指令");
		new CodeSend().send(ASokcetType.TCP_OUT_DRUG, returnCode);
	}
}
