package us;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LogWrite {
	private static RandomAccessFile mm;
	private static String logTitle = "0000000000000000";
	private static RandomAccessFile mm2;
	private static String logTitle2 = "0000000000000000";
	private final static Object oLockObject = new Object();
	private final static Object oLockObject2 = new Object();
	
	public static void println(String x) {
		synchronized (oLockObject) {
			Date currentTime = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
			String dateString = formatter.format(currentTime);
			System.out.println(dateString + x);
			String string = "\r\n" + dateString + " " + x;
			if (mm == null || !dateString.substring(0, 10).equals(logTitle)) {
				try {
					mm.close();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
				//	e1.printStackTrace();
				}
				try {
					logTitle = dateString.substring(0, 10);
					mm = new RandomAccessFile(InitParamters.logPath + logTitle+".log", "rw");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
	        try {
	            mm.seek(mm.length());
	            mm.write(string.getBytes());
	        } catch (IOException e1) {
	            e1.printStackTrace();
	        }
		}
	}
	
	public static void println(Exception e) {
		synchronized (oLockObject2) {
			Date currentTime = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
			String dateString = formatter.format(currentTime);
			
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			
			String string = "\r\n" + dateString + " " + sw.toString();
			if (mm2 == null || !dateString.substring(0, 10).equals(logTitle2)) {
				try {
					mm2.close();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
				//	e1.printStackTrace();
				}
				try {
					logTitle2 = dateString.substring(0, 10);
					mm2 = new RandomAccessFile(InitParamters.logPath + logTitle2 + ".err", "rw");
				} catch (FileNotFoundException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
			
	        try {
	            mm2.seek(mm2.length());
	            mm2.write(string.getBytes());
	        } catch (IOException e1) {
	            e1.printStackTrace();
	        }
		}
	}
}
