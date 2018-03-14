package us;


public class OutInitCheck implements Runnable {
	public void run() {
		while (true) {
			// 如果设备初始化状态为false则
			// 发送获取设备状态指令
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
