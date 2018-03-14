package us;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TooManyListenersException;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;



public class CkSerialPortEx  implements Runnable, SerialPortEventListener {
	public static HashMap<ASerialPortType, CkSerialPortEx> ckSerialPortExHashMap = new HashMap<ASerialPortType, CkSerialPortEx>();
	
	@SuppressWarnings("unchecked")
	private Enumeration portList;
	private CommPortIdentifier port;
	SerialPort serialPort;
	private InputStream inputStream;
	private ArrayList<Integer> dArrayList = new ArrayList<Integer>();
	private final Object dataObject = new Object();
	static byte[] readBuffer = new byte[10];
	int commPort;
	
	public String getRecvData() {
		String string = "";
		try {
			string = String.format("%03d", dArrayList.get(0)) + String.format("%03d", dArrayList.get(1)) + String.format("%03d", dArrayList.get(2));
			LogWrite.println("测量值："+string);
			String str1 = string.substring(0, 3);
			str1 = Integer.toBinaryString(Integer.parseInt(str1));
			str1 = "000000000000000" + str1;
			str1 = str1.substring(str1.length() - 8);
			
			String str2 = string.substring(3, 6);
			str2 = Integer.toBinaryString(Integer.parseInt(str2));
			str2 = "000000000000000" + str2;
			str2 = str2.substring(str2.length() - 8);
			
			String str3 = string.substring(6, 9);
			str3 = Integer.toBinaryString(Integer.parseInt(str3));
			str3 = "000000000000000" + str3;
			str3 = str3.substring(str3.length() - 8);
			if (str1.substring(str1.length() - 1).equals("0") && str2.substring(str2.length() - 1).equals("1") && str3.substring(str3.length() - 1).equals("1")) {
				return "" + Integer.parseInt(str3.substring(5, 7) + str2.substring(2, 7) + str1.substring(0, 7), 2);
			} else {
			//	closeComm();
			//	openComm(commPort);
				return "";
			}
		} catch (Exception e) {
			// TODO: handle exception
			LogWrite.println(e);
			return "";
		}
	//	return string;
		/*
		synchronized (dataObject) {
			if (dArrayList.size() > 4) {
				string = String.format("%03d", dArrayList.get(0)) + String.format("%03d", dArrayList.get(1))
						+ String.format("%03d", dArrayList.get(2)) + String.format("%03d", dArrayList.get(3)) + String.format("%03d", dArrayList.get(4));
			}
		}
		// YL09 传感器
		// 三个字节确定一个长度
		// 第一个字节的3-7位，第二个字节的2-7位，第三个字节的1-9位组合为长度。
		// 第一个字节末尾为1，第二个字节的末尾为1，第三个字节的末尾为0
		try {
			String str1 = string.substring(0, 3);
			str1 = Integer.toBinaryString(Integer.parseInt(str1));
			str1 = "000000000000000" + str1;
			str1 = str1.substring(str1.length() - 8);
			
			String str2 = string.substring(3, 6);
			str2 = Integer.toBinaryString(Integer.parseInt(str2));
			str2 = "000000000000000" + str2;
			str2 = str2.substring(str2.length() - 8);
			
			String str3 = string.substring(6, 9);
			str3 = Integer.toBinaryString(Integer.parseInt(str3));
			str3 = "000000000000000" + str3;
			str3 = str3.substring(str3.length() - 8);
			
			String str4 = string.substring(9, 12);
			str4 = Integer.toBinaryString(Integer.parseInt(str4));
			str4 = "000000000000000" + str4;
			str4 = str4.substring(str4.length() - 8);
			
			String str5 = string.substring(9, 12);
			str5 = Integer.toBinaryString(Integer.parseInt(str5));
			str5 = "000000000000000" + str5;
			str5 = str5.substring(str5.length() - 8);
			
			if (str1.substring(str1.length() - 1).equals("0") && str2.substring(str2.length() - 1).equals("1") && str3.substring(str3.length() - 1).equals("1")) {
			
				return "" + Integer.parseInt(str3.substring(5, 7) + str2.substring(2, 7) + str1.substring(0, 7), 2);
			} else if (str2.substring(str2.length() - 1).equals("0") && str3.substring(str3.length() - 1).equals("1") && str4.substring(str4.length() - 1).equals("1")) {
			
				return "" + Integer.parseInt(str4.substring(5, 7) + str3.substring(2, 7) + str2.substring(0, 7), 2);
			} else if (str3.substring(str3.length() - 1).equals("0") && str4.substring(str4.length() - 1).equals("1") && str5.substring(str5.length() - 1).equals("1")) {
				
				return "" + Integer.parseInt(str5.substring(5, 7) + str4.substring(2, 7) + str3.substring(0, 7), 2);
			} else {
				//
				return "";
			}
		} catch (Exception e) {
			// TODO: handle exception
			return "";
		}
		*/
	}
	public void closeComm() {
		try {
			serialPort.close();
		} catch(Exception e){
			
		}
	}
	
	public boolean openComm(ASerialPortType aSerialPortType, int comID) {
		commPort = comID;
		portList = CommPortIdentifier.getPortIdentifiers();
		String tmpCommString = "";
		while (portList.hasMoreElements()) {
			port = (CommPortIdentifier) portList.nextElement();

			if (tmpCommString.equals(port.getName())) {
				continue;
			}
			tmpCommString = port.getName();

			if (port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (port.getName().equals("COM" + comID)) {
					try {
						serialPort = (SerialPort) port.open("SimpleWriteApp", 2000);
						inputStream = serialPort.getInputStream();
						serialPort.addEventListener(this);
						serialPort.notifyOnDataAvailable(true);
					//	serialPort.notifyOnDataAvailable(false);
						serialPort.setSerialPortParams(9600,
								SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
								SerialPort.PARITY_NONE);
						
						ckSerialPortExHashMap.put(aSerialPortType, this);
					} catch (PortInUseException e) {
						LogWrite.println(comID + "号串口被占用!");
						return false;
					} catch (IOException e) {
						return false;
					} catch (UnsupportedCommOperationException e) {
						return false;
					} catch (TooManyListenersException e) {
						e.printStackTrace();
						return false;
					}// end try
					return true;
				}// end if
			}// end if
		}// end while
		return false;
	}

	@Override
	public void run() {
		//
	}

	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		case SerialPortEvent.DATA_AVAILABLE:

			/*try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
		//	i++;
			try {
				while (inputStream.available() > 0) {
					int numBytes = inputStream.read(readBuffer);
					/*if(numBytes==3){
						j++;
					}*/
				//	System.out.println(i+"  "+j);
					synchronized (dataObject) {
						dArrayList.clear();
						for (int i = 0; i < numBytes; i++) {
							dArrayList.add(i, readBuffer[i] < 0 ? (readBuffer[i] + 256) : readBuffer[i]);
						}
					}
					/*
					if (numBytes < 6) {
						break;
					}
					
					synchronized (dataObject) {
						dArrayList.clear();
						for (int i = 0; i < numBytes; i++) {
							dArrayList.add(i, readBuffer[i] < 0 ? (readBuffer[i] + 256) : readBuffer[i]);
						}
					}
					*/
				}
			} catch (IOException e) {
				LogWrite.println(e);
			}
			break;
		}
	}

	public static void main(String[] args) {
		
	}
}
