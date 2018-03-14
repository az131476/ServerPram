package us;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.OutputStream;
import java.net.Socket;



public class Modbus2 {
	// 连接的ID
	private String socketID;
	// 连接服务器的IP
	private String serverIP = "192.168.100.16";
	// 连接服务器的Port
//	private int serverPort = 502;
	
	public String getSocketID() {
		return socketID;
	}

	public void setSocketID(String socketID) {
		this.socketID = socketID;
	}

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}
	
	private byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}
	
	public String Bytes2HexString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}
	
	Socket es;
	OutputStream os;
	FilterInputStream is;
	
	public void run() {
		//OutMsg.println(socketID+" 针对 Modbus "+serverIP+" 端口"+serverPort+" 接收线程开启成功");
		
		/*try {
			while (true) {
				String s = br.readLine();
				synchronized (GlobalData.codeLock) {
					GlobalData.codeQueue.offer(s);
				}
			}
		} catch (Exception e) {
		//	GlobalData.socHashMap.remove(socketID);
			socketflag = false;
			OutMsg.println(socketID+" FATAL ERROR: "+serverIP+" Modbus 已退出或网络故障！接收线程终止！");
		}*/
	}
	
	/**
	 * 发送消息
	 * @param code
	 */
	public synchronized void sendMsg(String code) {
	//	pw.println(code);
	}
	
	@SuppressWarnings("unused")
	public synchronized String operateModebus(String code) {
		try {	
			if (es == null) {
				LogWrite.println("建立Modbus连接");
				es = new Socket(serverIP, 502);
				os = es.getOutputStream();
				is = new BufferedInputStream(es.getInputStream());
				LogWrite.println("建立Modbus连接完毕");
			} else {
				//
			}
		//	Socket es = new Socket(serverIP, 502);
		//	OutputStream os = es.getOutputStream();
		//	FilterInputStream is = new BufferedInputStream(es.getInputStream());
			byte obuf[] = new byte[256];
			byte ibuf[] = new byte[256];
//			/*
//			 * 指令解析
//			 * 写：向寄存器000A和000B两个寄存器中写入数据，分别是42C8和0000
//			 * 0000 0000 00 0B 01 10 0009 0002 04 42 C8 00 00(H)                         request
//			 * 0000:事物处理标识符，通常为0
//			 *      0000:协议标识符，值为0
//			 *           00:长度字段高位，通常为0
//			 *              0B:长度字段地位，指示后面字节的数量
//			 *                 01:设备ID
//			 *                    10:功能码
//			 *                       0009:寄存器(实际操作的是该寄存器后面的那个寄存器)
//			 *                            0002:要操作的寄存器的个数
//			 *                                 04:指示后面字节的数量
//			 *                                    42C8 0000:数据 
//			 * 0000 0000 00 06 01 10 0009 0002                                           right response 
//			 * 0000 0000 00 02 __ __                									 error response 
//			 * 				   __:
//			 * 					  __:
//			 * 读：读寄存器000A和000B中的值												 
//			 * 0000 0000 00 06 01 03 0009 0002											 request
//			 * 0000 0000 00 07 01 03 04 42C8 0000                                        right response
//			 * 0000 0000 00 02 __ __                									 error response  
//			 * */
//			
			int i;
//		
		//	String  code = "00000000000B0110000100020442D80000";
		//	code = "000000000006010300A00002";
			obuf = hexStringToByte(code);			
//Modbus TCP和Modbus RTU的协议到底有什么区别，协议相差00 00 00 00 00 和没有校验。//
			
			String str_obuf = Bytes2HexString(obuf);
		//	System.out.println("发送指令:" + name + " " + str_obuf);
			es.setSoTimeout(3000);
			os.write(obuf);
		//	System.out.println("等待接收");
			i = is.read(ibuf, 0, 256);
			String str_ibuf = Bytes2HexString(ibuf);
			String sub_str = str_ibuf.substring(10, 12);
			int num_sub_str = Integer.parseInt(Integer.toString(Integer.parseInt(sub_str,16)),10);
			str_ibuf = str_ibuf.substring(0,12 + 2*num_sub_str);
			
	//		OutMsgModbus.println("", "接收指令:" + name + " " + str_ibuf);
			
		//	es.close();
			return str_ibuf;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWrite.println("PLC返回指令超时XXXXXXXXXXXXX");
			LogWrite.println(e);
			try {
				es = new Socket(serverIP, 502);
				os = es.getOutputStream();
				is = new BufferedInputStream(es.getInputStream());
				
				byte obuf[] = new byte[256];
				byte ibuf[] = new byte[256];
				
				int i;
				obuf = hexStringToByte(code);			
				String str_obuf = Bytes2HexString(obuf);
			//	System.out.println("=========");
				os.write(obuf);
			//	System.out.println("=========");
				i = is.read(ibuf, 0, 256);
				String str_ibuf = Bytes2HexString(ibuf);
				String sub_str = str_ibuf.substring(10, 12);
				int num_sub_str = Integer.parseInt(Integer.toString(Integer.parseInt(sub_str,16)),10);
				str_ibuf = str_ibuf.substring(0,12 + 2*num_sub_str);
				return str_ibuf;
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
				LogWrite.println("PLC返回指令再次超时XXXXXXXXXXXXX");
				LogWrite.println(e2);
			}
		}
		return "";
	/*	byte obuf[] = new byte[256];
		byte ibuf[] = new byte[256];
	//	code = "00000000000B0110000900020442C80000";
	//	code = "000000000006010300090002";
		code = "00000000000601050016FF00";
		String returnStr = "";
		try {
			OutputStream os = socket.getOutputStream();
			FilterInputStream is = new BufferedInputStream(socket.getInputStream());
			
			String str_obuf = Bytes2HexString(obuf);
			System.out.println("发送指令:" + str_obuf);
			os.write(obuf);
			
			is.read(ibuf, 0, 256);
			String str_ibuf = Bytes2HexString(ibuf);
			String sub_str = str_ibuf.substring(10, 12);
			int num_sub_str = Integer.parseInt(Integer.toString(Integer.parseInt(sub_str,16)),10);
			returnStr = str_ibuf = str_ibuf.substring(0,12 + 2*num_sub_str);
			
			System.out.println("接收指令:"+str_ibuf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			socketflag = false;
		}//end try
		
		return returnStr;*/
	}
	
	public static void main(String[] args) {
		new Modbus2().operateModebus("0000000000060106000B0001");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new Modbus2().operateModebus("0000000000060106000B0000");
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		new Modbus("192.168.100.12", 502);
		
		System.out.println(Modbus.modbus.executeCode("0000000000060106000B0001"));;
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Modbus.modbus.executeCode("0000000000060106000B0000");
	}
}
