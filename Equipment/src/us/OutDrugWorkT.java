package us;

import java.util.Iterator;
import java.util.Map;

import bsh.EvalError;
import bsh.Interpreter;


public class OutDrugWorkT implements Runnable {
	public static OutParamters outParamters;
	protected Interpreter interpreter = new Interpreter();
	public static boolean secondOutDrug = false;
	
	public OutDrugWorkT(String code) {
		// �豸æµ
		SysOutState.setProcErrorCode("01");
		
		if (ElevatorPosition.positionHashMap.size() == 0) {
			ElevatorPosition.init();
		}
		
		outParamters = new OutParamters();
		outParamters.initOutCode(code);
		
//		try {
//			this.interpreter.source(InitParamters.configPath + "front.script");
//			secondOutDrug = (Boolean)interpreter.get("secondOutDrug");
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
	}
	
	public boolean beltRun() {
		String runCode = "";
		try {
			runCode = interpreter.eval("getBeltMotionCode(\""+ outParamters.getOutterNO() +"\")").toString();
			LogWrite.println(runCode);
			if (!runCode.equals("")) {
				new Modbus2().operateModebus(runCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogWrite.println(e);
		}
		LogWrite.println("[��ҩ]������Ƥ����ʼת��");
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public void run() {
		LogWrite.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		// ��ⷭ��״̬
		LogWrite.println("����״̬��⿪ʼ");
		while (!FMoveControl.frontSysStop && !OutBeltDoorState.initialize()) {
		//	ControlParamters.frontBeltDoorDealState = true;
			SysOutState.setSolved(false);
			SysOutState.setProcErrorCode("06");
			// ���Ͷ����쳣״̬
		//	String code = "$S410 2 K1 ���� xxxx*";
		//	new CodeSend().send(ASokcetType.TCP_OUT_DRUG, code);
		//	while (!FMoveControl.frontSysStop && !ControlParamters.frontBeltDoorDealState) {
			while (!FMoveControl.frontSysStop && !SysOutState.isSolved()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		LogWrite.println("����״̬������");
		if (FMoveControl.frontSysStop) {
			LogWrite.println("��ҩ����ֹͣ");
			return;
		}
		
		int outDrugOffsetHeight = 0;
		try {
			outDrugOffsetHeight = (Integer)interpreter.get("outDrugOffsetHeight");
		} catch (EvalError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// ת��Ƥ��
		beltRun();
		
		// ��ҩ����
		for (int rowNO : outParamters.getDealRowSortList()) {
			OutDrugByRowVO outDrugByRowVO = outParamters.getRowHashMap().get(rowNO);
			// ����ҩ����
			LogWrite.println("��ҩ����״̬��⿪ʼ");
			while (!FMoveControl.frontSysStop) {
				String sensorState = OutSensorState.getState();
				if ("00".equals(sensorState)) {
					break;
				}
				SysOutState.setSolved(false);
				SysOutState.setProcErrorCode(sensorState);
				while (!FMoveControl.frontSysStop && !SysOutState.isSolved()) {
					try {
						LogWrite.println("�����쳣���ȴ�����");
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			LogWrite.println("��ҩ����״̬������");
			if (FMoveControl.frontSysStop) {
				LogWrite.println("��ҩ����ֹͣ");
				return;
			}
			
			// �˶�����ҩλ��
			int outHeight = ElevatorPosition.positionHashMap.get(rowNO);
			LogWrite.println("[��ҩ]������ǰ���У�"+rowNO +"���߶ȣ�"+outHeight);
			
			LifterMotion lifterMotion = new LifterMotion(outHeight);
			Thread moveLift2Thread = new Thread(lifterMotion);
			moveLift2Thread.start();
			// �ж��˶���λ
			while (true && !FMoveControl.frontSysStop) {
				int currentPos = Integer.parseInt(lifterMotion.getCurrentPosition());
				if (outDrugByRowVO.isPFlag()) {
				//	LogWrite.println("[��ҩ]Ƥ������ǰλ�ã�"+currentPos);
					if (Math.abs(currentPos - outHeight) < 10) {
				//		LogWrite.println("[��ҩ]Ƥ���������ҩλ�ã���ʼ��ҩ");
						break;
					} 
				} else {
					// Ƥ�������ﷶΧ�ڼ����ý��г�ҩ
				//	LogWrite.println("[��ҩ]Ƥ������ǰλ�ã�"+currentPos);
					if (outHeight - currentPos < outDrugOffsetHeight && outHeight - currentPos > -5) {
				//		LogWrite.println("[��ҩ]Ƥ���������ҩλ�ã���ʼ��ҩ");
						break;
					} 
				}
				
		//		LogWrite.println("[��ҩ]Ƥ��������ǰ����ҩλ��");
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				// �ж��Ƿ�����˶��쳣
				if (!"00".equals(lifterMotion.getErrCode())) {
					// �������˶���������
					SysOutState.setProcErrorCode(lifterMotion.getErrCode());
					
					while (!FMoveControl.frontSysStop && !SysOutState.isSolved()) {
						try {
							LogWrite.println("������ǰ����ҩλ���˶����⣬�ȴ�����");
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					moveLift2Thread.start();
				}
			}//end while
			
			if (FMoveControl.frontSysStop) {
				LogWrite.println("=============================================================");
				LogWrite.println("===================�豸��ͣ�ã���ҩ�߳���ֹ===================");
				LogWrite.println("=============================================================");
				return;
			}
			
			LogWrite.println("[��ҩ]�У�" + rowNO + " ��ҩ��ʼ");
			
			// ��ҩ	�����ӳ�ҩ��ʱ���ж�
			OutDrugControlT outDrugControlT = new OutDrugControlT(outDrugByRowVO);
		//	Thread thread = new Thread(outDrugControlT);
		//	thread.start();
			outDrugControlT.run();
			
			LogWrite.println("[��ҩ]�У�"+rowNO + " ��ҩ���");
			
			// �ж�Ƥ�����Ƿ�����˶�
			while (true) {
				while (moveLift2Thread.isAlive()) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// �ж��Ƿ�����˶��쳣
				if (!"00".equals(lifterMotion.getErrCode())) {
					// �������˶���������
					SysOutState.setProcErrorCode(lifterMotion.getErrCode());
					
					while (!FMoveControl.frontSysStop && !SysOutState.isSolved()) {
						try {
							LogWrite.println("�����������ɵ�һ�ν�ҩ�����쳣���ȴ����");
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					moveLift2Thread.start();
				} else {
					LogWrite.println("[��ҩ]��ʼִ����һ�γ�ҩ");
					break;
				}
			}
		}
		LogWrite.println("[��ҩ]ȫ����ҩ���");
		
		// ����ҩ����
		LogWrite.println("��ҩ����״̬��⿪ʼ");
		while (!FMoveControl.frontSysStop) {
			String sensorState = OutSensorState.getState();
			if ("00".equals(sensorState)) {
				break;
			}
			
			SysOutState.setSolved(false);
			SysOutState.setProcErrorCode(sensorState);
			while (!FMoveControl.frontSysStop && !SysOutState.isSolved()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		LogWrite.println("��ҩ����״̬������");
		if (FMoveControl.frontSysStop) {
			LogWrite.println("��ҩ����ֹͣ");
			return;
		}
		
		// ǰ����ҩ��drugCacheHeight_1S
		LogWrite.println("[��ҩ]ǰ����ҩ�ڣ�"+outParamters.getOutterNO());
		try {
			int height = Integer.parseInt(interpreter.get("drugCacheHeight_" + outParamters.getOutterNO()).toString());
			LifterMotion lifterMotion = new LifterMotion(height);
			lifterMotion.run();
			while (true) {
				if (!"00".equals(lifterMotion.getErrCode())) {
					// �������˶���������
					SysOutState.setProcErrorCode(lifterMotion.getErrCode());
					
					while (!FMoveControl.frontSysStop && !SysOutState.isSolved()) {
						try {
							LogWrite.println("������������м�ҩ׼��ǰ����ҩ�ڳ����쳣���ȴ����");
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					lifterMotion.run();
				} else {
					LogWrite.println("[��ҩ]�����ҩ�ڣ�"+outParamters.getOutterNO());
					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			LogWrite.println(e);
		}
		
		// ���س�ҩ���ָ��
		boolean waitReOut = false;
		String returnCode = "$Q406";
		returnCode += outParamters.getKey().length() + outParamters.getKey();
		returnCode += outParamters.getOutterNO() + outParamters.getWinNO();
		returnCode += outParamters.getProcCode().length() + outParamters.getProcCode();
		Iterator iter1 = outParamters.getRowHashMap().entrySet().iterator();
		while (iter1.hasNext()) {
			Map.Entry entry = (Map.Entry) iter1.next();
			int rowNO = (Integer) entry.getKey();
			OutDrugByRowVO outDrugByRowVO = (OutDrugByRowVO)entry.getValue();
			
			Iterator iter2 = outDrugByRowVO.getColHashMap().entrySet().iterator();
			while (iter2.hasNext()) {
				Map.Entry entry2 = (Map.Entry) iter2.next();
				String keyStr = (String)entry2.getKey();
				String needQty = ((String)entry2.getValue()).substring(3, 5);
				String realQty = outDrugByRowVO.getResultHashMap().get(keyStr);
				if (Integer.parseInt(needQty) > Integer.parseInt(realQty)) {
					waitReOut = true;
				}
				returnCode += String.format("%02d", rowNO) + keyStr + realQty;
			}
		}
		returnCode += CRCKey.getKey() + "*";
		LogWrite.println("[��ҩ]��ҩȫ����ɣ����س�ҩ���");
		new CodeSend().send(ASokcetType.TCP_OUT_DRUG, returnCode);
		
		// �貹��ʱ�ȴ�����ָ�����Ҫʱ����
		if (waitReOut) {
			//��Ҫ����ʱ��ȫ����ҩ��ɺ����
			
		}
		
		// ������ʱֹͣƤ��
		
	}
}
