package us;


public class OutInitCheck implements Runnable {
	public void run() {
		while (true) {
			// ����豸��ʼ��״̬Ϊfalse��
			// ���ͻ�ȡ�豸״ָ̬��
			if (!OutParamters.isEquiptmentInit()) {
				SocketClient.clientHashMap.get(1).sendMsg("$Q1022K1000*");
			} else {
				break;
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
