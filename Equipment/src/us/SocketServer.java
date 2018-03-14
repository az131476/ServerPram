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
	public static HashMap<ASokcetType, SocketServer> socketServerHashMap = new HashMap<ASokcetType, SocketServer>();
	private Socket clientSocket;
	
	private int PORT = 0; // 端口号   
    
	public SocketServer(ASokcetType socketType, int port) {
		this.PORT = port;
		socketServerHashMap.put(socketType, this);
	}
	
	public void run() {
		try {
			ServerSocket server = new ServerSocket(PORT);
			ExecutorService exec = Executors.newCachedThreadPool();
			LogWrite.println("服务已启动 端口号"+PORT);
			
			Socket client = null;
			while (true) {
				client = server.accept(); // 接收客户连接  
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
	//	private PrintWriter pw;
  
		public MsgRecieve(Socket socket) throws IOException {
			LogWrite.println("客户端连接进入，客户端IP地址："+socket.getInetAddress().getHostAddress());
			this.socket = socket;
			br = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
	//		pw = new PrintWriter(socket.getOutputStream(), true);
		}

		public void run() {
			try {
				while ((msg = br.readLine()) != null) {
					// 连接进入校验
					/*if (msg.substring(0, 5).equals("$Q999")) {
						clientSocket = socket;
					}*/
					clientSocket = socket;
					System.out.println(msg);
					// ACK
				//	sendSocketMsg("$ACKC" + msg.substring(msg.length() - 5));
					if (msg == null) {
						LogWrite.println(">>>>>>>>>>服务端异常关闭<<<<<<<<<<");
						break;
					}
					LogWrite.println("收到指令："+msg);
					msg = "$" + Algorithm.DESDecrypt(msg.substring(1, msg.length()-1), "YN200916") + "*";
					
					if (msg.substring(0, 5).equals("$Q101")) {
						LogWrite.println("接收到初始化查询1：");
						new EquipmentSystemState().getState(ASokcetType.TCP_IN_DRUG, msg);
					} else if (msg.substring(0, 5).equals("$Q102")) {
						LogWrite.println("接收到初始化查询2：");
						new EquipmentSystemState().getState(ASokcetType.TCP_OUT_DRUG, msg);
					} else if (msg.substring(0, 5).equals("$Q401")) {
						new Thread(new OutDrugWorkT(msg)).start();
					} else if (msg.substring(0, 5).equals("$Q403")) {
						new Thread(new SecondOutDrug(msg)).start();
					} else if (msg.substring(0, 5).equals("$Q405")) {
						OutDrugControlT.outDrugBusy = false;
					} else if (msg.substring(0, 5).equals("$Q407")) {
						new Thread(new OutDrugOverT(msg)).start();
					} /*else if (msg.substring(0, 5).equals("$Q301") || msg.substring(0, 5).equals("$Q303")) {
						new Thread(new OrientStock(msg)).start();
					} */else if (msg.substring(0, 5).equals("$Q201")) {
						new Thread(new InDrugWorkT(msg)).start();
					} else if (msg.substring(0, 5).equals("$Q203")) {
						LogWrite.println(">>4-");
						InDrugWorkT.putDrugOver = true;
					} else if (msg.substring(0, 5).equals("$Q205")) {
						InDrugWorkT.exitInDrug = true;
					} else if (msg.substring(0, 5).equals("$Q211")) { //$Q2112F101
						InDrugWorkT.e1Deal = true;
					} else if (msg.substring(0, 5).equals("$Q301") || msg.substring(0, 5).equals("$Q303")) {
						new Thread(new OrientStock(msg)).start();
					} else if (msg.substring(0, 5).equals("$Q305")) {
						OrientStock.e1Deal = true;
					} else if (msg.substring(0, 5).equals("$Q219")) {
						// 降落
						String code = "0000000000060106"+ String.format("%04X", 100) + "0001";
						Modbus.modbus.executeCode(code);
					}else if(msg.substring(0, 5).equals("$Qfwb")){
						new Thread(new ResetButton(msg)).start();
					}
				}
			} catch (IOException e) {
				LogWrite.println(e);
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				clientSocket = null;
			}
		}
	}
	
	public synchronized int sendSocketMsg(String code) {
		try {
			if (null == code || "".equals(code)) {
				return 0;
			}
			code = "$" + Algorithm.DESEncrypt(code.substring(1, code.length()-1), "YN200916") + "*";
			
			PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);
			pw.println(code);
		} catch (IOException e) {
			// TODO: handle exception
			return 0;
		}
		return 1;
	}
}