package us;

import java.util.HashMap;


public class PrescriptionDeal implements Runnable {
	private boolean turnFlag = false;
	private boolean pause = false;
	
	public void run() {
		try {
			LogWrite.println(">>>>>���ط�ҩ����");
			ProcessNO.init();
			
			
			HashMap<String, String> cacheWinMap = new CacheAndWindow().getWindowHashMap();
			
			LogWrite.println(">>>>>===============================");
			
			while (true) {
				if (pause) {
					LogWrite.println(">>>>>ϵͳ����δ֪�쳣");
					break;
				}
				turnFlag = !turnFlag;
				try {
					// ����豸�Ƿ����
					while (!OutParamters.isEquiptmentFree()) {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						LogWrite.println(">>>>>�豸�����У��Ժ��ٽ����ж�");
					}
					LogWrite.println(">>>>>�豸���У��жϻ����Ƿ����");
					
					// �����ȡ���г�ҩ��
					String cache = "";
					if (turnFlag) {
						LogWrite.println("left");
						cache = CacheControl.cacheHashMap.get(CacheType.LEFT).getFreeCache("");
					} else {
						LogWrite.println("right");
						cache = CacheControl.cacheHashMap.get(CacheType.RIGHT).getFreeCache("");
					}
					if (cache.equals("")) {
						LogWrite.println(">>>>>���໺���޿���λ�ã��������ҩ����");
						Thread.sleep(500);
						continue;
					}
					
					// �жϿ��л����Ƿ��ж�Ӧ��ȡҩ����
					String winArr = cacheWinMap.get(cache.substring(0, 1));
					if (winArr == null) {
						LogWrite.println(">>>>���໺���޶�Ӧȡҩ���ڣ��������ҩ����");
						Thread.sleep(500);
						continue;
					}
					
					// ��ʼ����
					ProcessV processV = new PrescriptionPro().getPatient(winArr);
					if (processV == null) {
						LogWrite.println(">>>>>�޴������������ڣ�"+winArr);
						Thread.sleep(500);
						continue;
					}
					
					// ��״̬1
					LogWrite.println(">>>>>�ô���״̬ Ϊ��1");
					if (!new PrescriptionPro().setProcess_1(processV.getPatientID(), processV.getWindow())) {
						LogWrite.println(">>>>>�޴�������");
						Thread.sleep(500);
						continue;
					}
					
					String procCode = ProcessNO.getProcCode();
					LogWrite.println(">>>>>���ɴ�����ţ�"+procCode);
					new PrescriptionPro().setProcess_2(procCode, processV.getPatientID());
					
					// ���ɳ�����ϸ
					new PrescriptionPro().createOutDetail(procCode);
					
					// ���ɳ�ҩָ��
					String outCode = new PrescriptionPro().createOutCode(procCode);
					if (outCode.equals("")) {
						LogWrite.println(">>>>>��ҩָ��Ϊ�գ���������Ϊ�ǻ�������");
						// �����ֹ���ӡ
						String printCode = "$Q7010001" + procCode.length() + procCode + CRCKey.getKey() + "*";
						UdpSocket.uSocketHashMap.get(ASocketType.PRINT).sendMessage(new CacheAndWindow().getPrinter(cache.substring(0, 1)), 7016, printCode);
					}
					
					outCode = "$Q4012F1" + cache + "01" + procCode.length() + procCode + outCode + CRCKey.getKey() + "*";
					LogWrite.println(">>>>>���ͷ�ҩָ�" + outCode);
					SocketClient.clientHashMap.get(1).sendMsg(outCode);
					
					LogWrite.println(">>>>>3");
					new PrescriptionPro().setProcess_2_(procCode, 3);
					
					// �豸æµ
					OutParamters.setEquiptmentFree(false);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					LogWrite.println(e);
				}
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			LogWrite.println(e);
		}
	}
}
