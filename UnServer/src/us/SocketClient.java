package us;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;




public class SocketClient implements Runnable {
	public static HashMap<Integer, SocketClient> clientHashMap = new HashMap<Integer, SocketClient>();
	private ExecutorService exec = Executors.newCachedThreadPool();
	private BufferedReader br;
	private PrintWriter pw;
	private boolean connectFlag = false;
	private Socket socket;
	private String ipAddress;
	private int port;
	
	public boolean isConnectFlag() {
		return connectFlag;
	}

	public SocketClient(String ipAddress, int port) {
    	this.ipAddress = ipAddress;
    	this.port = port;
    	clientHashMap.put(1, this);
    	exec.execute(new connectServer(this));
    }

	public void run() {
		LogWrite.println("���ӷ���ˣ�"+ipAddress+":"+port+" �ɹ�");
		
		LogWrite.println("��ʼ��ѯ�豸״̬");
		new Thread(new OutInitCheck()).start();

		// 
		try {
			while (true) {
				String msg = br.readLine();
				
				if (msg == null) {
					LogWrite.println(">>>>>>>>>>������쳣�ر�<<<<<<<<<<");
					connectFlag = false;
					break;
				}
				LogWrite.println("�յ�ָ�"+msg);
				msg = "$" + Algorithm.DESDecrypt(msg.substring(1, msg.length()-1), "YN200916") + "*";
				LogWrite.println("ָ�"+msg);
				//$A102 2K1 200200101000 10100000000000000020018e5*
				if (msg.substring(0, 5).equals("$A102")) {
					//$A1022K103200200114101000000000000000200y156*
					LogWrite.println("��ǰ�豸״̬��"+msg);
					//$A400 2 K1 2 00 xxxx*
					if (msg.substring(8, 20).equals("200200200200")) {
						OutParamters.setEquiptmentInit(true);
						OutParamters.setEquiptmentFree(true);
						LogWrite.println(">>>>>>>>>>�豸״̬����<<<<<<<<<<");
					}
				} else if (msg.substring(0, 5).equals("$Q406")) {
					new Thread(new PrescriptionOut(msg)).start();
				} else if (msg.substring(0, 5).equals("$Q408")) {
					new Thread(new PrescriptionFinish(msg)).start();
				}
				
			}
		} catch (Exception e) {
			connectFlag = false;
			e.printStackTrace();
			LogWrite.println("������쳣�ر�: " + this.toString());
		}
	}
    
	/**
	 * ������Ϣ
	 * @param code
	 */
	public synchronized boolean sendMsg(String code) {
		try {
			if (null == code || "".equals(code)) {
				return false;
			}
			code = "$" + Algorithm.DESEncrypt(code.substring(1, code.length()-1), "YN200916") + "*";
			pw.println(code);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			LogWrite.println(e);
			return false;
		}
	}
	
	class connectServer implements Runnable {
		private SocketClient socketClient;
        
		public connectServer(SocketClient socketClient) {
        	this.socketClient = socketClient;
		}
		
		public void run() {
			LogWrite.println("���������߳̿���������ˣ�" + ipAddress + ":" + port);
			try {
				while (true) {
					while (!socketClient.connectFlag) {
						LogWrite.println("�������ӷ���ˣ�" + ipAddress + ":" + port);
                		//����
						try {
							socketClient.socket = new Socket(socketClient.ipAddress, socketClient.port);
							LogWrite.println("���ӷ���˳ɹ���" + ipAddress + ":" + port);
							connectFlag = true;
							br = new BufferedReader(new InputStreamReader(socketClient.socket.getInputStream())); 
							pw = new PrintWriter(socketClient.socket.getOutputStream(), true);
							
							socketClient.connectFlag = true;
							new Thread(socketClient).start();
						} catch (Exception e1) {
        					// TODO Auto-generated catch block
							e1.printStackTrace();
							LogWrite.println(e1);
						}//end try
                		// ��ʱ2s
						Thread.sleep(2000);
					}//end while
					
                	// ��ʱ2s
					Thread.sleep(2000);
				}//end while
			} catch (Exception e) {
				e.printStackTrace();
			}//end try
		}
	} 
}
