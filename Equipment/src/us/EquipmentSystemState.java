package us;

import bsh.Interpreter;


public class EquipmentSystemState {
	public void getState(ASokcetType aSokcetType, String code) {
		//$Q101 2 F1 000 000 000 000 000 000 000 000 000 000 000 xxxx*
		// PLC 200 出药端口 皮带机 上药端口 x Y 
		String aCode = code.substring(0, 8).replaceAll("Q", "A");
		aCode += ModbusState.isNormal() ? "200" : "103";
		aCode += "200";
		
		aCode += (SerialPortEx.serialPortExHashMap.get(ASerialPortType.OUTDRUG) == null) ? "101" : "200";
		aCode += ControlParamters.equimentUnitMap.get("EU_A").isInitState() ? "200" : (ControlParamters.equimentUnitMap.get("EU_A").getErrCode().equals("00") ? "000" : "1" + ControlParamters.equimentUnitMap.get("EU_A").getErrCode());
		
		aCode += (CkSerialPortEx.ckSerialPortExHashMap.get(ASerialPortType.CK_LEFT) == null || CkSerialPortEx.ckSerialPortExHashMap.get(ASerialPortType.CK_LEFT) == null) ? "101" : "200";
		aCode += ControlParamters.equimentUnitMap.get("EU_B").isInitState() ? "200" : (ControlParamters.equimentUnitMap.get("EU_B").getErrCode().equals("00") ? "000" : "1" + ControlParamters.equimentUnitMap.get("EU_B").getErrCode());
		aCode += ControlParamters.equimentUnitMap.get("EU_C").isInitState() ? "200" : (ControlParamters.equimentUnitMap.get("EU_C").getErrCode().equals("00") ? "000" : "1" + ControlParamters.equimentUnitMap.get("EU_C").getErrCode());
		aCode += ControlParamters.equimentUnitMap.get("EU_D").isInitState() ? "200" : (ControlParamters.equimentUnitMap.get("EU_D").getErrCode().equals("00") ? "000" : "1" + ControlParamters.equimentUnitMap.get("EU_D").getErrCode());
		aCode += ControlParamters.equimentUnitMap.get("EU_E").isInitState() ? "200" : (ControlParamters.equimentUnitMap.get("EU_E").getErrCode().equals("00") ? "000" : "1" + ControlParamters.equimentUnitMap.get("EU_E").getErrCode());
		aCode += ControlParamters.equimentUnitMap.get("EU_F").isInitState() ? "200" : (ControlParamters.equimentUnitMap.get("EU_F").getErrCode().equals("00") ? "000" : "1" + ControlParamters.equimentUnitMap.get("EU_F").getErrCode());
		
		aCode += "200";
		aCode += CRCKey.getKey();
		aCode += "*";
		
		SocketServer.socketServerHashMap.get(aSokcetType).sendSocketMsg(aCode);
	}
	
	public static void main(String[] args) {
		Interpreter interpreter = new Interpreter();
		try {
			interpreter.source(InitParamters.configPath + "main.script");
			
			// 升降机
			{
				EquitmentUnit eUnit = new EquitmentUnit("EU_A");
				eUnit.setInitTimeout(Integer.parseInt(interpreter.get("lifterInitTimeout").toString()));
				eUnit.setMoveTimeout(Integer.parseInt(interpreter.get("lifterMoveTimeout").toString()));
				eUnit.setMinPosition(Integer.parseInt(interpreter.get("lifterMinPosition").toString()));
				eUnit.setMaxPosition(Integer.parseInt(interpreter.get("lifterMaxPosition").toString()));
				eUnit.setErrCode("21");
				ControlParamters.equimentUnitMap.put("EU_A", eUnit);
			}
			// X轴
			{
				EquitmentUnit eUnit = new EquitmentUnit("EU_B");
				eUnit.setInitTimeout(Integer.parseInt(interpreter.get("xInitTimeout").toString()));
				eUnit.setMoveTimeout(Integer.parseInt(interpreter.get("xMoveTimeout").toString()));
				eUnit.setMinPosition(Integer.parseInt(interpreter.get("xMinPosition").toString()));
				eUnit.setMaxPosition(Integer.parseInt(interpreter.get("xMaxPosition").toString()));
				ControlParamters.equimentUnitMap.put("EU_B", eUnit);
			}
			// Y轴
			{
				EquitmentUnit eUnit = new EquitmentUnit("EU_C");
				eUnit.setInitTimeout(Integer.parseInt(interpreter.get("yInitTimeout").toString()));
				eUnit.setMoveTimeout(Integer.parseInt(interpreter.get("yMoveTimeout").toString()));
				eUnit.setMinPosition(Integer.parseInt(interpreter.get("yMinPosition").toString()));
				eUnit.setMaxPosition(Integer.parseInt(interpreter.get("yMaxPosition").toString()));
				ControlParamters.equimentUnitMap.put("EU_C", eUnit);
			}
			// 拨药
			{
				EquitmentUnit eUnit = new EquitmentUnit("EU_D");
				eUnit.setInitTimeout(Integer.parseInt(interpreter.get("verPushBoardInitTimeout").toString()));
				eUnit.setMoveTimeout(Integer.parseInt(interpreter.get("verPushBoardMoveTimeout").toString()));
				eUnit.setMinPosition(Integer.parseInt(interpreter.get("verPushBoardMinPosition").toString()));
				eUnit.setMaxPosition(Integer.parseInt(interpreter.get("verPushBoardMaxPosition").toString()));
				ControlParamters.equimentUnitMap.put("EU_D", eUnit);
			}
			// 齐药
			{
				EquitmentUnit eUnit = new EquitmentUnit("EU_E");
				eUnit.setInitTimeout(Integer.parseInt(interpreter.get("horPushBoardInitTimeout").toString()));
				eUnit.setMoveTimeout(Integer.parseInt(interpreter.get("horPushBoardMoveTimeout").toString()));
				eUnit.setMinPosition(Integer.parseInt(interpreter.get("horPushBoardMinPosition").toString()));
				eUnit.setMaxPosition(Integer.parseInt(interpreter.get("horPushBoardMaxPosition").toString()));
				ControlParamters.equimentUnitMap.put("EU_E", eUnit);
			}
			// 出药门
			{
				EquitmentUnit eUnit = new EquitmentUnit("EU_F");
				eUnit.setInitTimeout(Integer.parseInt(interpreter.get("throughDoorInitTimeout").toString()));
				eUnit.setMoveTimeout(Integer.parseInt(interpreter.get("throughDoorMoveTimeout").toString()));
				eUnit.setMinPosition(Integer.parseInt(interpreter.get("throughDoorMinPosition").toString()));
				eUnit.setMaxPosition(Integer.parseInt(interpreter.get("throughDoorMaxPosition").toString()));
				ControlParamters.equimentUnitMap.put("EU_F", eUnit);
			}
			
			new EquipmentSystemState().getState(ASokcetType.TCP_IN_DRUG, "$Q1012F1000000000000000000000000000000000xxxx*");
			
		} catch (Exception e) {
			e.printStackTrace();
			LogWrite.println(e);
		}
	}
}
