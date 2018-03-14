package us;

import bsh.EvalError;


public class XYMotion extends FUnitMotion {
	private String checkInitCodeH = "0000000000060103"+ String.format("%04X", 70) + "0001";
	private String curPositionCodeH = "0000000000060103"+ String.format("%04X", 26) + "0001";
	private String aimPositionCodeH = "0000000000060106"+ String.format("%04X", 24);
	private String speedCodeH = "0000000000060106"+ String.format("%04X", 68);
	private String motionCodeH = "0000000000060106"+ String.format("%04X", 66) + "0001";
	private String checkMotionCodeH = "0000000000060103"+ String.format("%04X", 66) +"0001";
	
	private String checkInitCodeV = "0000000000060103"+ String.format("%04X", 76) + "0001";
	private String curPositionCodeV = "0000000000060103"+ String.format("%04X", 30) + "0001";
	private String aimPositionCodeV = "0000000000060106"+ String.format("%04X", 28);
	private String speedCodeV = "0000000000060106"+ String.format("%04X", 74);
	private String motionCodeV = "0000000000060106"+ String.format("%04X", 72) + "0001";
	private String checkMotionCodeV = "0000000000060103"+ String.format("%04X", 72) +"0001";
	
	public XYMotion(AMotion aMotion, int horPosition, int verPosition) {
		super(aMotion, horPosition, verPosition);
		errCode = "00";
		try {
			horSpeed = (Integer)interpreter.get("xSpeed");
			verSpeed = (Integer)interpreter.get("ySpeed");
			timeOut = 10 * (Integer)ControlParamters.equimentUnitMap.get("EU_A").getMoveTimeout();
		} catch (EvalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		/*if (model == 1) {
			// ���ö��䱻��סXYֹͣ
			Modbus.modbus.operateModebus("XYDF", PlcCodeString.W_IO_USEXYSTOP);
		} else {
			// ͣ�ö��䱻��סXYֹͣ
			Modbus.modbus.operateModebus("XYDF", PlcCodeString.W_IO_STOPXYSTOP);
		}*/
		try {
			LogWrite.println("EU_BC��XYִ���˶���XĿ��λ�ã�" + horPosition + " X�ٶȣ�" + horSpeed + " YĿ��λ�ã�" + verPosition + " Y�ٶȣ�" + verSpeed);
			
			// �ж��Ƿ��ʼ��
			String rStringH = Modbus.modbus.executeCode(checkInitCodeH);
			String rStringV = Modbus.modbus.executeCode(checkInitCodeV);
			if (13 - Integer.parseInt(rStringH.substring(rStringH.length()-4), 16) != 0 
					|| 13 - Integer.parseInt(rStringV.substring(rStringV.length()-4), 16) != 0) {
				setErrCode("11");
				LogWrite.println("EU_BC��XY���ʼ���쳣���޷�����");
				return;
			}
			
			// ��⼱ͣ�ſ��Ƿ�����
			SpecialStateCheck specialStateCheck = new SpecialStateCheck();
			if (!specialStateCheck.backEmgenceStop()) {
				setErrCode("02");
				return;
			}
			LogWrite.println("ControlParamters.backAccessControl:"+ControlParamters.backAccessControl);
			LogWrite.println("specialStateCheck.backDoorControl():"+specialStateCheck.backDoorControl());
			LogWrite.println("t:"+(ControlParamters.backAccessControl ? specialStateCheck.backDoorControl() : true));
			if (!(ControlParamters.backAccessControl ? specialStateCheck.backDoorControl() : true)) {
				setErrCode("03");
				return;
			}
			
			// ����˶��估��ҩ�����Ƿ�����������ͨ����ҩ���濪ʼ��ʼ��
			if (!specialStateCheck.backSensor()) {
				setErrCode("04");
				return;
			}
			
			// �ж�X�Ƿ�ִ���˶�
			if (aMotion == AMotion.XY || aMotion == AMotion.X) {
				String speedX = String.format("%04X", horSpeed);
				// X���ٶ�	
				Modbus.modbus.executeCode(speedCodeH + speedX);
				// Ԥ��λ��ֵ
				String spositionX = String.format("%04X", horPosition);
				Modbus.modbus.executeCode(aimPositionCodeH + spositionX);
			}
			
			// �ж�Y�Ƿ�ִ���˶�
			if (aMotion == AMotion.XY || aMotion == AMotion.Y) {
				String speedY = String.format("%04X", verSpeed);
				// Y���ٶ�
				Modbus.modbus.executeCode(speedCodeV + speedY);
				// Ԥ��λ��ֵ
				//getRobotYValueM
				String spositionY = String.format("%04X", verPosition);
				Modbus.modbus.executeCode(aimPositionCodeV + spositionY);
			}
			
			if (aMotion == AMotion.XY || aMotion == AMotion.X) {
				Modbus.modbus.executeCode(motionCodeH);	
			}
			if (aMotion == AMotion.XY || aMotion == AMotion.Y) {
				Modbus.modbus.executeCode(motionCodeV);
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			while ((aMotion == AMotion.XY || aMotion == AMotion.X) && timeOut > 0) {
				// AC	B0
				rStringH = Modbus.modbus.executeCode(curPositionCodeH);
				// 0000 XXXX 0000 0000 0000 XXXX
				int positionx1 = Integer.parseInt(rStringH.substring(rStringH.length() - 4), 16);
				if (Math.abs(horPosition - positionx1) < 2) {
					LogWrite.println("EU_BC: Xλ���˶���λ");
					break;
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					timeOut--;
				}
				
				// �ж��Ƿ����Ļ����䵼��ֹͣ
				rStringH = Modbus.modbus.executeCode(checkMotionCodeH);
				if (Integer.parseInt(rStringH.substring(rStringH.length()-4), 16) > 90) {
					setErrCode("02");
					return;
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					timeOut--;
				}
			}//end while
			
			while ((aMotion == AMotion.XY || aMotion == AMotion.Y) && timeOut > 0) {
				rStringV = Modbus.modbus.executeCode(curPositionCodeV);
				int position2 = Integer.parseInt(rStringV.substring(rStringV.length() - 4), 16);
				if (Math.abs(verPosition - position2) < 2) {
					LogWrite.println("EU_BC: Yλ���˶���λ");
					break;
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					timeOut--;
				}
				
				// �ж��Ƿ����Ļ����䵼��ֹͣ
				rStringH = Modbus.modbus.executeCode(checkMotionCodeH);
				if (Integer.parseInt(rStringH.substring(rStringH.length()-4), 16) > 90) {
					setErrCode("02");
					return;
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					timeOut--;
				}
			}
			
			while ((aMotion == AMotion.XY || aMotion == AMotion.X) && timeOut > 0) {
				rStringH = Modbus.modbus.executeCode(checkMotionCodeH);
				if (22 - Integer.parseInt(rStringH.substring(rStringH.length()-4), 16) == 0) {
					// �˶���λ����ʹ��Ϊfalse������while
					LogWrite.println("EU_BC��X���˶�����");
					break;
				} else if (Integer.parseInt(rStringH.substring(rStringH.length()-4), 16) > 90) {
					setErrCode("02");
					return;
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					timeOut--;
				}
			}
			while ((aMotion == AMotion.XY || aMotion == AMotion.Y) && timeOut > 0) {
				rStringV = Modbus.modbus.executeCode(checkMotionCodeV);
				if (22 - Integer.parseInt(rStringV.substring(rStringV.length()-4), 16) == 0) {
					// �˶���λ����ʹ��Ϊfalse������while
					LogWrite.println("EU_A��Y���˶�����");
					break;
				} else if (Integer.parseInt(rStringV.substring(rStringV.length()-4), 16) > 90) {
					setErrCode("02");
					return;
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					timeOut--;
				}
			}
			
			// �˶���ʱ��
			if (timeOut < 1) {
				setErrCode("21");
				return;
			}
			LogWrite.println("EU_BC��XY�˶�ִ�����");
			/*
			 * ��ʼ��ҩ���̵�ǰ����ſػ�ͣ�Ƿ��쳣
			 */
//			if (!specialStateCheck.backEmgenceStop()) {
//				setErrCode("02");
//				return;
//			}
//			if (!(ControlParamters.backAccessControl ? specialStateCheck.backDoorControl() : true)) {
//				setErrCode("03");
//				return;
//			}
		} catch (Exception e) {
			// TODO: handle exception
			setErrCode("99");
			LogWrite.println("EU_BC���˶��������ش���");
			LogWrite.println(e);
		}
	}
}
