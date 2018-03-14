package us;

import java.io.InputStream;
import java.util.Properties;


import bsh.Interpreter;


public class SysInitT implements Runnable {
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		// 延时一会儿
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// 读取配置参数开始初始化	
		Interpreter interpreter = new Interpreter();
		try {
			LogWrite.println(">>>>>>>>>>>>>>>>>>>>系统开始初始化<<<<<<<<<<<<<<<<<<<<");
			interpreter.source(InitParamters.configPath + "server.script");
			
			LogWrite.println(">>>>加载配置参数");
			/*InputStream in1 = SysInitT.class.getClassLoader().getResourceAsStream("config.properties");
			Properties p = new Properties();
			p.load(in1);
            
			System.out.println("properties:ip="+p.getProperty("serverip")+",name=" + p.getProperty("username") + ",age=" + p.getProperty("password"));
			*/
            DBManager.serverIP = interpreter.get("databaseip").toString();//p.getProperty("serverip");
            DBManager.userName = interpreter.get("databaseuser").toString();//p.getProperty("username");
            DBManager.userPassword = interpreter.get("databasepassword").toString();//p.getProperty("password");
            DBManager.dbname = interpreter.get("databasename").toString();
    		// 左缓存
    		//new Thread(new CacheControl(CacheType.LEFT, interpreter.get("cache_1_ip").toString())).start();
    		
    		// 右缓存
    		//new Thread(new CacheControl(CacheType.RIGHT, interpreter.get("cache_2_ip").toString())).start();
    		
 
    		// 左缓存
    		//new Thread(new CacheControl(CacheType.LEFT, interpreter.get("cache_1_ip").toString())).start();
    		new Thread(new CacheControl(CacheType.LEFT, "192.168.100.12")).start();
    		 //右缓存
    		//new Thread(new CacheControl(CacheType.RIGHT, interpreter.get("cache_2_ip").toString())).start();
    		new Thread(new CacheControl(CacheType.RIGHT, "192.168.100.10")).start();
    		
    		
    		// 开启辅助药架7219端口
    		LogWrite.println(">>>>开启辅助药架端口：7219");
    		new Thread(new SocketServer(7219)).start();
    		
    		// 连接设备发药端口
    		LogWrite.println(">>>>准备连接设备端口：7211");
    		new SocketClient(interpreter.get("equipment_ip").toString(), 7211);
    		
    		LogWrite.println(">>>>开启打印端口：7016");
    		new Thread(new UdpSocket(ASocketType.PRINT, 7016)).start();
    		
    		LogWrite.println(">>>>>开启出药流程");
    		new Thread(new PrescriptionDeal()).start();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		LogWrite.println(">>>>>>>>>>>>>>>>>>>>系统初始化完毕<<<<<<<<<<<<<<<<<<<<");
	}

}
