package us;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.OutputStream;

import java.net.Socket;



public class Modbus {
	public static Modbus modbus;
	private String serverIP;
	private Socket es;
	private OutputStream os;
	private FilterInputStream is;
	
	public Modbus(String ip, int port) {
		this.serverIP = ip;
		modbus = this;
	}

	public static void main(String[] args) {
		new Modbus("192.168.1.154", 502).executeCode("0000000000060106003E0a28");
	}
	
	public byte[] hexStringToByte(String hex) {
		if (hex == null || hex.equals("")) {  
	        return null;  
	    } 
		hex = hex.toUpperCase();
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}
	
	public byte[] hexStringToBytes(String hexString) {
	    if (hexString == null || hexString.equals("")) {
	        return null;  
	    }
	    hexString = hexString.toUpperCase();
	    int length = hexString.length() / 2;
	    char[] hexChars = hexString.toCharArray();
	    byte[] d = new byte[length];
	    for (int i = 0; i < length; i++) {
	        int pos = i * 2;
	        d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
	    }
	    return d;
	}
	
	private byte charToByte(char c) {  
	    return (byte) "0123456789ABCDEF".indexOf(c);  
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
	
	public int StartSocket() {
		try {
			es = new Socket(serverIP, 502);
			os = es.getOutputStream();
			is = new BufferedInputStream(es.getInputStream());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 1;
	}
	
	public synchronized String executeCode(String code) {
		while (true) {
			try {	
				if (es == null) {
					System.out.println("========================");
					es = new Socket(serverIP, 502);
					os = es.getOutputStream();
					is = new BufferedInputStream(es.getInputStream());
				} else {
					//
				}
				byte obuf[] = new byte[256];
				byte ibuf[] = new byte[256];

				obuf = hexStringToBytes(code);			
				
				//Modbus TCP和Modbus RTU的协议到底有什么区别，协议相差00 00 00 00 00 和没有校验。
				
				es.setSoTimeout(5000);
				os.write(obuf);
				is.read(ibuf, 0, 256);
				String str_ibuf = Bytes2HexString(ibuf);
				String sub_str = str_ibuf.substring(10, 12);
				int num_sub_str = Integer.parseInt(Integer.toString(Integer.parseInt(sub_str,16)),10);
				str_ibuf = str_ibuf.substring(0,12 + 2*num_sub_str);
				if ("000000000000".equals(str_ibuf)) {
					throw new NullPointerException();
				}
				return str_ibuf;
			} catch (Exception e) {
				LogWrite.println("PLC通讯异常，稍候重新执行");
				try {
					es.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
				es = null;
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*
		try {	
			if (es == null) {
				System.out.println("========================");
				es = new Socket(serverIP, 502);
				os = es.getOutputStream();
				is = new BufferedInputStream(es.getInputStream());
			} else {
				//
			}
			byte obuf[] = new byte[256];
			byte ibuf[] = new byte[256];

			obuf = hexStringToBytes(code);			
			
			//Modbus TCP和Modbus RTU的协议到底有什么区别，协议相差00 00 00 00 00 和没有校验。
			
			es.setSoTimeout(5000);
			os.write(obuf);
			is.read(ibuf, 0, 256);
			String str_ibuf = Bytes2HexString(ibuf);
			String sub_str = str_ibuf.substring(10, 12);
			int num_sub_str = Integer.parseInt(Integer.toString(Integer.parseInt(sub_str,16)),10);
			str_ibuf = str_ibuf.substring(0,12 + 2*num_sub_str);

			return str_ibuf;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWrite.println("指令异常");
			LogWrite.println(e);
			try {
				es = new Socket(serverIP, 502);
				os = es.getOutputStream();
				is = new BufferedInputStream(es.getInputStream());
				
				byte obuf[] = new byte[256];
				byte ibuf[] = new byte[256];
				
				obuf = hexStringToByte(code);
				os.write(obuf);
				is.read(ibuf, 0, 256);
				String str_ibuf = Bytes2HexString(ibuf);
				String sub_str = str_ibuf.substring(10, 12);
				int num_sub_str = Integer.parseInt(Integer.toString(Integer.parseInt(sub_str,16)),10);
				str_ibuf = str_ibuf.substring(0,12 + 2*num_sub_str);
				return str_ibuf;
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
				LogWrite.println("指令异常");
				LogWrite.println(e2);
			}
		}
		return "";
		*/
	}
}
