package us;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import bsh.EvalError;
import bsh.Interpreter;


public class UdpSocket implements Runnable {
	public static UdpSocket udpSocket;
	private DatagramSocket socket;
	private byte[] buf = new byte[1000];
    private DatagramPacket dp = new DatagramPacket(buf, buf.length);
    protected Interpreter interpreter = new Interpreter();
    public static OutParamters outParamters;
    String fetchNo;
	public UdpSocket(int port) {
		try {
			socket = new DatagramSocket(port);
			udpSocket = this;
			LogWrite.println(port+"端口开启成功");
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
            try {
            	socket.receive(dp);
	            //接收到客户端的消息
	            String rcvd = Dgram.toString(dp);
	        //    LogWrite.println("收到指令：" + rcvd + " " + "from address:" + dp.getAddress() + ",port:" + dp.getPort());
	            if (rcvd == null) {
					continue;
				}
	            rcvd = "$" + Algorithm.DESDecrypt(rcvd.substring(1, rcvd.length()-1), "YN200916") + "*";
	            if (rcvd.substring(0, 5).equals("$Q711")) {
	            	String code = "$A7122F1" + SysOutState.getEmgenceState() + SysOutState.getDoorState() + SysOutState.getPorcErrorCode() + CRCKey.getKey() + "*";
	            	code = "$" + Algorithm.DESEncrypt(code.substring(1, code.length()-1), "YN200916") + "*";
					sendMessage(dp.getAddress(), dp.getPort(), code);
				} else if (rcvd.substring(0, 5).equals("$Q713")) {
					LogWrite.println("异常解决："+rcvd);
					// $Q7132K1NNxxxx*
					SysOutState.setProcErrorCode("01");
					SysOutState.setSolved(true);
					beltRun();
				}
	           //  System.out.println("From Client:"+rcvd);
	             
	           /*  String echoString = "From Server Echoed:" + rcvd;
	             DatagramPacket echo = Dgram.toDatagram(echoString,
	                     dp.getAddress(), dp.getPort());
	             //将数据包发送给客户端
	             socket.send(echo);*/
			} catch (Exception e) {
				// TODO: handle exception
			}
        }
	}
	public void sendMessage(InetAddress ipAddress, int port, String code) {
		try {
			DatagramPacket echo = Dgram.toDatagram(code,  ipAddress, port);
	        //将数据包发送给客户端
	        socket.send(echo);
		} catch (Exception e) {
			// TODO: handle exception
			LogWrite.println(e);
		}
	}

	public static void main(String[] args) {
	//	LogWrite.println("开启状态监控端口：7015");
	//	System.out.println(Algorithm.DESEncrypt("1111111", "YN200916"));
	}
	public void beltRun() {
		String outterNO;
		fetchNo();
		LogWrite.println("fetchNO:"+fetchNo);
		if(fetchNo.equals("1")||fetchNo.equals("2")){
			outterNO = "2S";
		}else {
			outterNO = "1S";
		}
		String runCode = "";
		try {
			runCode = interpreter.eval("getBeltMotionCode(\""+ outterNO +"\")").toString();
			if (!runCode.equals("")) {
				Modbus.modbus.executeCode(runCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		LogWrite.println("[出药]升降机皮带继续转动,窗口："+outterNO);
	}
	public String fetchNo(){
		Interpreter interpreter = new Interpreter();
		try {
			interpreter.source(InitParamters.configPath + "server.script");
			
			DBManager.serverIP = interpreter.get("databaseip").toString();//p.getProperty("serverip");
			DBManager.userName = interpreter.get("databaseuser").toString();//p.getProperty("username");
			DBManager.userPassword = interpreter.get("databasepassword").toString();//p.getProperty("password");
			DBManager.dbName = interpreter.get("databasename").toString();
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (EvalError e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		Connection con = null;
		String sql = "select FetchWindow,ProcFlg,PrescriptionNo from prescriptionlist where ProcFlg='3'  and DATE_FORMAT(ProcDate,'%Y%m%d') =  DATE_FORMAT(SYSDATE(),'%Y%m%d')";
		try {
			con = DBManager.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while(rs.next()) {
				fetchNo = rs.getString(1);
			}
			rs.close();
			st.close();
		} catch (Exception e) {
			LogWrite.println("窗口号查询异常:"+sql);
		} finally {
			DBManager.close(con);
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		return fetchNo;
	}
}
