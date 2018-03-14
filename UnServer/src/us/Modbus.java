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
		try {	
			if (es == null) {
				es = new Socket(serverIP, 502);
				os = es.getOutputStream();
				is = new BufferedInputStream(es.getInputStream());
			} else {
				//
			}
			byte obuf[] = new byte[256];
			byte ibuf[] = new byte[256];

			obuf = hexStringToByte(code);			
			
			//Modbus TCP和Modbus RTU的协议到底有什么区别，协议相差00 00 00 00 00 和没有校验。
			//tcp协议即在rtu前面加五个0
			es.setSoTimeout(3000);
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
			LogWrite.println("指令异常1："+code);
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
				LogWrite.println("指令异常2");
				LogWrite.println(e2);
			}
		}
		return "";
	}
}
