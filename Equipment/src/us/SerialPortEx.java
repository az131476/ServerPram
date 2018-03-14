package us;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;



public class SerialPortEx implements Runnable, SerialPortEventListener {
	public static HashMap<ASerialPortType, SerialPortEx> serialPortExHashMap = new HashMap<ASerialPortType, SerialPortEx>();
	public final static Object LOCK_SERIALPORT = new Object();
	
	private CommPortIdentifier port;
	private SerialPort serialPort;
	private OutputStream outputStream;
	private InputStream inputStream;
	private	ArrayList<Integer> recvData = new ArrayList<Integer>();
	private final Object recvObject = new Object();
	
	@SuppressWarnings("unchecked")
	public boolean openComm(ASerialPortType aSerialPortType, int comID) {
		Enumeration portList = CommPortIdentifier.getPortIdentifiers();
		String tmpCommString = "";
		while (portList.hasMoreElements()) {
			port = (CommPortIdentifier) portList.nextElement();

			if (tmpCommString.equals(port.getName())) {
				continue;
			}
			tmpCommString = port.getName();
			
			if (port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (port.getName().equals("COM" + comID)) {
					LogWrite.println("※准备开启串口："+comID);
					try {
						serialPort = (SerialPort)port.open("SimpleWriteApp", 2000);
						outputStream = serialPort.getOutputStream();
						inputStream = serialPort.getInputStream();
						serialPort.addEventListener(this);
						serialPort.notifyOnDataAvailable(true);
						serialPort.setSerialPortParams(9600,
	                            SerialPort.DATABITS_8,
	                            SerialPort.STOPBITS_1,
	                            SerialPort.PARITY_NONE);
						
						serialPortExHashMap.put(aSerialPortType, this);
						
						LogWrite.println("※开启串口成功："+comID);
					} catch (Exception e) {
						e.printStackTrace();
						LogWrite.println("※开启串口失败");
						LogWrite.println(e);
						return false;
					}//end try
					return true;
				}//end if
			}//end if
		}//end while
		return false;
	}
	
	public String getRecvData() {
		String str = "";
		synchronized (recvObject) {
			for (int i = 0; i < recvData.size(); i++) {
				str += String.format("%02X", recvData.get(i));
				if (recvData.get(i) == 0xEE) {
					return str;
				} else {
					//
				}
			}
		}
		return "";
	}

	public void setRecvData(int[] recvData, int len) {
		/*String str = "";
		for (int i = 0; i < len; i++) {
			String temp = "000" + recvData[i];
			str += temp.substring(temp.length() - 3) + " ";
		}
		this.recvData = str;*/
		synchronized (recvObject) {
			for (int i = 0; i < recvData.length; i++) {
				this.recvData.add(recvData[i]);
			}
		}
		
		/*String string = "返回值接受未完成，继续等待";
		for (int i = 0; i < this.recvData.size(); i++) {
			string += this.recvData.get(i) + " ";
		}
		System.out.println(string);*/
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
	}
	
	/**
	 * 以多进制写串口数据，目前只支持16进制
	 * @param code
	 * @param radix 16 按十六进制
	 */
	public boolean writeComm(String code, int radix) {
		/*
		int i1 = 0x81;	i1 = i1 > 127 ? i1 - 256 : i1;
		int i2 = 0x04; i2 = i2 > 127 ? i2 - 256 : i2;
		int i3 = 0x41;	i3 = i3 > 127 ? i3 - 256 : i3;
		int i4 = 0x44;	i4 = i4 > 127 ? i4 - 256 : i4;
		byte[] data = new byte[] {(byte)i1, (byte)i2, (byte)i3, (byte)i4};
		*/
		try {
			synchronized (recvData) {
				recvData.clear();
			}
			
			byte[] data = new byte[code.length()/2];
			for (int i = 0; i < code.length()/2; i++) {
				int num = Integer.parseInt(code.substring(2*i, 2*(i+1)), 16);
				num = num > 127 ? num - 256 : num;
				data[i] = (byte)num;
			}
			
			try {
				outputStream.write(data);
				return true;
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
				LogWrite.println("单片机出药控制指令发送失败！");
			}
		} catch (Exception e) {
			// TODO: handle exception
			LogWrite.println(e);
		}
		return false;
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		// TODO Auto-generated method stub
		switch(event.getEventType()) {
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
            byte[] readBuffer = new byte[100];
            
            try {
                while (inputStream.available() > 0) {
                    int numBytes = inputStream.read(readBuffer);
                //    System.out.println(numBytes);
                    int[] recv = new int[numBytes];
                    for(int i = 0;i < numBytes;i++){
                    	recv[i] = readBuffer[i] < 0 ? (readBuffer[i] + 256) : readBuffer[i]; 
                    }
                    setRecvData(recv, numBytes);
                }
                //81 06 59 00 00 5E 
                //-127 6 89 0 0  94
                //81 06 59 4A 24 30 
                //129
                //667
                
            //    System.out.println(new String(readBuffer).trim());
            //    setRecvData(new String(readBuffer).trim());
            } catch (IOException e) {}
            break;
        }
	}

	public void close() {
		serialPort.close();
	}
	
	public static void main(String[] args) {
		
	}
}
