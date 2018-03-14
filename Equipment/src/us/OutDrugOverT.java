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
		LogWrite.println("[��ҩ]������Ƥ����ʼת��");
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
		LogWrite.println("[��ҩ]������Ƥ��ֹͣת��");
		return true;
	}
	
	public void run() {
		//$Q407 2 K1 00 00 n ZUHAOXXXX*
		//$Q406 2 F1 1S 01 1220160823002214091005iG6*
		LogWrite.println("[��ҩ]�յ�����򿪳�ҩָ��");
		String outterNO = code.substring(6 + Integer.parseInt(code.substring(5, 6)));
		outterNO = outterNO.substring(0, 2);
		
		// ת��Ƥ��
		//LogWrite.println("[��ҩ]���ҩ��ת����"+outterNO);
		//beltRun(outterNO);//Ƥ����ʼת��
		beltStop();//Ƥ��ֹͣת��
		
		
		//Ƥ����ת1.3��
		String runCode = "";
		try {
			runCode = interpreter.eval("getBeltOppositeMotionCode(\""+ outterNO +"\")").toString();
			if (!runCode.equals("")) {
				Modbus.modbus.executeCode(runCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		LogWrite.println("[��ҩ]������Ƥ����ʼ��ת");
		try {
			Thread.sleep(Integer.parseInt(interpreter.get("rollbacktime").toString())); //1000
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		// 
		OutBeltDoorState.openOutBeltDoor(outterNO);//�򿪷���
		try {
			Thread.sleep(Integer.parseInt(interpreter.get("turndoorwaittime").toString())); //1200
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		beltRun(outterNO);//Ƥ����ʼת��
		try {
			Thread.sleep(Integer.parseInt(interpreter.get("beltOutDrugDoorInterval").toString()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		OutBeltDoorState.closeOutBeltDoor(outterNO);//�رշ���
		
		beltStop();//Ƥ��ֹͣת��
		
		// �豸������
		SysOutState.setProcErrorCode("00");
		
		String returnCode = code.replaceAll("Q407", "Q408");
		LogWrite.println("[��ҩ]���ط����ҩ���ָ��");
		new CodeSend().send(ASokcetType.TCP_OUT_DRUG, returnCode);
	}
}
