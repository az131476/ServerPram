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
			LogWrite.println(port+"�˿ڿ����ɹ�");
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
	            //���յ��ͻ��˵���Ϣ
	            String rcvd = Dgram.toString(dp);
	            LogWrite.println("�յ�ָ�" + rcvd + " " + "from address:" + dp.getAddress() + ",port:" + dp.getPort());
	             
	            if (rcvd.substring(0, 5).equals("$Q711")) {
					// sendMessage(dp.getAddress(), dp.getPort(), "");
				} 
	           //  System.out.println("From Client:"+rcvd);
	             
	           /*  String echoString = "From Server Echoed:" + rcvd;
	             DatagramPacket echo = Dgram.toDatagram(echoString,
	                     dp.getAddress(), dp.getPort());
	             //�����ݰ����͸��ͻ���
	             socket.send(echo);*/
			} catch (Exception e) {
				// TODO: handle exception
			}
        }
	}
	
	public void sendMessage(String ip, int port, String code) {
		try {
			DatagramPacket echo = Dgram.toDatagram(code, InetAddress.getByName(ip), port);
	        //�����ݰ����͸��ͻ���
	        socket.send(echo);
		} catch (Exception e) {
			// TODO: handle exception
			LogWrite.println(e);
		}
	}

	public static void main(String[] args) {
		LogWrite.println("����״̬��ض˿ڣ�7015");
		new Thread(new UdpSocket(ASocketType.CACHE, 7015)).start();
	}
}
