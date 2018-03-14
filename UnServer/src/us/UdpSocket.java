package us;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;



public class UdpSocket implements Runnable {
	public static HashMap<ASocketType, UdpSocket> uSocketHashMap = new HashMap<ASocketType, UdpSocket>();
	private DatagramSocket socket;
	private byte[] buf = new byte[1000];
    private DatagramPacket dp = new DatagramPacket(buf, buf.length);
    
	public UdpSocket(ASocketType aSocketType, int port) {
		try {
			socket = new DatagramSocket(port);
			uSocketHashMap.put(aSocketType, this);
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
	            LogWrite.println("收到指令：" + rcvd + " " + "from address:" + dp.getAddress() + ",port:" + dp.getPort());
	             
	            if (rcvd.substring(0, 5).equals("$Q711")) {
					// sendMessage(dp.getAddress(), dp.getPort(), "");
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
	
	public void sendMessage(String ip, int port, String code) {
		try {
			DatagramPacket echo = Dgram.toDatagram(code, InetAddress.getByName(ip), port);
	        //将数据包发送给客户端
	        socket.send(echo);
		} catch (Exception e) {
			// TODO: handle exception
			LogWrite.println(e);
		}
	}

	public static void main(String[] args) {
		LogWrite.println("开启状态监控端口：7015");
		new Thread(new UdpSocket(ASocketType.CACHE, 7015)).start();
	}
}
