package us;

import java.io.InputStream;
import java.util.Properties;


import bsh.Interpreter;


public class SysInitT implements Runnable {
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		// ��ʱһ���
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// ��ȡ���ò�����ʼ��ʼ��	
		Interpreter interpreter = new Interpreter();
		try {
			LogWrite.println(">>>>>>>>>>>>>>>>>>>>ϵͳ��ʼ��ʼ��<<<<<<<<<<<<<<<<<<<<");
			interpreter.source(InitParamters.configPath + "server.script");
			
			LogWrite.println(">>>>�������ò���");
			/*InputStream in1 = SysInitT.class.getClassLoader().getResourceAsStream("config.properties");
			Properties p = new Properties();
			p.load(in1);
            
			System.out.println("properties:ip="+p.getProperty("serverip")+",name=" + p.getProperty("username") + ",age=" + p.getProperty("password"));
			*/
            DBManager.serverIP = interpreter.get("databaseip").toString();//p.getProperty("serverip");
            DBManager.userName = interpreter.get("databaseuser").toString();//p.getProperty("username");
            DBManager.userPassword = interpreter.get("databasepassword").toString();//p.getProperty("password");
            DBManager.dbname = interpreter.get("databasename").toString();
    		// �󻺴�
    		//new Thread(new CacheControl(CacheType.LEFT, interpreter.get("cache_1_ip").toString())).start();
    		
    		// �һ���
    		//new Thread(new CacheControl(CacheType.RIGHT, interpreter.get("cache_2_ip").toString())).start();
    		
 
    		// �󻺴�
    		//new Thread(new CacheControl(CacheType.LEFT, interpreter.get("cache_1_ip").toString())).start();
    		new Thread(new CacheControl(CacheType.LEFT, "192.168.100.12")).start();
    		 //�һ���
    		//new Thread(new CacheControl(CacheType.RIGHT, interpreter.get("cache_2_ip").toString())).start();
    		new Thread(new CacheControl(CacheType.RIGHT, "192.168.100.10")).start();
    		
    		
    		// ��������ҩ��7219�˿�
    		LogWrite.println(">>>>��������ҩ�ܶ˿ڣ�7219");
    		new Thread(new SocketServer(7219)).start();
    		
    		// �����豸��ҩ�˿�
    		LogWrite.println(">>>>׼�������豸�˿ڣ�7211");
    		new SocketClient(interpreter.get("equipment_ip").toString(), 7211);
    		
    		LogWrite.println(">>>>������ӡ�˿ڣ�7016");
    		new Thread(new UdpSocket(ASocketType.PRINT, 7016)).start();
    		
    		LogWrite.println(">>>>>������ҩ����");
    		new Thread(new PrescriptionDeal()).start();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		LogWrite.println(">>>>>>>>>>>>>>>>>>>>ϵͳ��ʼ�����<<<<<<<<<<<<<<<<<<<<");
	}

}
