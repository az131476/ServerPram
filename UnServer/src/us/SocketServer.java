package us;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class SocketServer implements Runnable  {
	public static HashMap<String, Socket> socketHashMap = new HashMap<String, Socket>();

	private int PORT = 0; // �˿ں�   
    
	public SocketServer(int port) {
		this.PORT = port;
	}
	
	public void run() {
		try {
			ServerSocket server = new ServerSocket(PORT);
			ExecutorService exec = Executors.newCachedThreadPool();
			LogWrite.println("���������� �˿ں�"+PORT);
			
			Socket client = null;
			while (true) {
				client = server.accept(); // ���տͻ�����  
				exec.execute(new MsgRecieve(client));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}//end try
	}
	
	class MsgRecieve implements Runnable {
		private Socket socket;
		private BufferedReader br;
		private String msg;
		private String key;
  
		public MsgRecieve(Socket socket) throws IOException {
			LogWrite.println("[ASSIST]�ͻ������ӽ��룬�ͻ���IP��ַ��"+socket.getInetAddress().getHostAddress());
			this.socket = socket;
			br = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
	//		pw = new PrintWriter(socket.getOutputStream(), true);
		}

		public void run() {
			try {
				while ((msg = br.readLine()) != null) { 
					// ACK
				//	sendSocketMsg("$ACKC" + msg.substring(msg.length() - 5));
					LogWrite.println("[ASSIST]�յ�ָ�" + msg);
					if (msg.substring(0, 5).equals("$Q601")) {
						key = msg.substring(5, 8);
						LogWrite.println("����ҩ�ܿͻ�������ϵͳ��" + key);
						socketHashMap.put(key, socket);
					} else if (msg.indexOf("<code><key>QUERY_STOCK</key>") > -1) {
						new Thread(new LightAssistStock(msg)).start();
						sendSocketMsg("<code><key>QUERY_STOCK</key><result>1</result></code>");
					} else if (msg.indexOf("<code><key>CLEAR_STOCK</key>") > -1) {
						new Thread(new ClearAssistStock(msg)).start();
						sendSocketMsg("<code><key>CLEAR_STOCK</key><result>1</result></code>");
					}
				}
			} catch (IOException e) {
				LogWrite.println(e);
				e.printStackTrace();
			} finally {
				socketHashMap.remove(key);
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		public synchronized int sendSocketMsg(String msg) {
			try {
				PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
				pw.println(msg);
			} catch (IOException e) {
				// TODO: handle exception
				return 0;
			}
			return 1;
		}
	}
}