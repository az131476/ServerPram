package us;

import java.util.ArrayList;
import java.util.HashMap;



public class CacheControl implements Runnable {
	public static HashMap<CacheType, CacheControl> cacheHashMap = new HashMap<CacheType, CacheControl>();
	private Modbus modbus;
	private final static Object basketLock = new Object();
	private ArrayList<BasketStock> basketList;
	
	public CacheControl(CacheType cacheType, String ipAddress) {
		modbus = new Modbus(ipAddress, 502);
		basketList = new ArrayList<BasketStock>();
		// DO 25/26/27/28	19/1A/1B/1C
		// DI 33/34/35/36	21/22/23/24
		
		/*
		 * 192.168.100.10    1,2 -  19(��)  1A(��)
		 * �ϣ�21  �� �£�22
		 * 
		 * 192.168.100.12     3,5 -  19(��)  1A(��)
		 */
		
		if(cacheType == CacheType.LEFT){
		BasketStock b1 = new BasketStock();
		b1.setCacheNO("1S");   //����
        b1.setCheckCode("000000000006010300210001");
        b1.setLightCode("0000000000060106001B0001");  //27
        b1.setCloseCode("0000000000060106001B0000"); 
        basketList.add(b1);

        BasketStock b2 = new BasketStock();
        b2.setCacheNO("1X");  //����
        b2.setCheckCode("000000000006010300220001");
        b2.setLightCode("0000000000060106001C0001"); //28
        b2.setCloseCode("0000000000060106001C0000");
        basketList.add(b2);
		}
		if(cacheType == CacheType.RIGHT){
        BasketStock b3 = new BasketStock();
		b3.setCacheNO("2S");//����
        b3.setCheckCode("000000000006010300230001");
        b3.setLightCode("000000000006010600190001");  //000000000006010300210001
        b3.setCloseCode("000000000006010600190000"); //25  //0000000000060106001B0001
        basketList.add(b3);

        BasketStock b4 = new BasketStock();
        b4.setCacheNO("2X");//����
        b4.setCheckCode("000000000006010300240001");
        b4.setLightCode("0000000000060106001A0001");//26
        b4.setCloseCode("0000000000060106001A0000");
        basketList.add(b4);
		}
		CacheControl.cacheHashMap.put(cacheType, this);
	}
	
	/**
	 * ��ȡ���л����
	 * @param cache
	 * @return
	 */
	public synchronized String getFreeCache(String cache) {
		String freeCache = "";
		synchronized (basketLock) {
			for (BasketStock basket : basketList) {
				if (basket.getCacheNO().equals(cache)) {//Ϊ��
					if (basket.isBasket() && !basket.isLight()) { //�����ӡ���� --����
						freeCache = basket.getCacheNO();
						LogWrite.println("freeCache1:"+freeCache);
					}
				}
			}
		}
		
		if ("".equals(freeCache)) {
			synchronized (basketLock) {
				for (BasketStock basket : basketList) {
					if (basket.isBasket() && !basket.isLight()) {
						freeCache = basket.getCacheNO();
						LogWrite.println("freeCache2:"+freeCache);
					}
				}
			}
		}
		LogWrite.println("return freeCache:"+freeCache);
		return freeCache;
	}
	
	/**
	 * ��������
	 * @param cache
	 */
	public synchronized void setLight(String cache) {
		synchronized (basketLock) {
			for (BasketStock basket : basketList) {
				LogWrite.println("light-cache:"+cache);
				LogWrite.println("light-basket.getCacheNO"+basket.getCacheNO());
				if (basket.getCacheNO().equals(cache)) {
					basket.setLight(true);
				}
			}
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		boolean lFlag = false;
		while (true) {
			lFlag = !lFlag;
			
			synchronized (basketLock) {
				for (BasketStock basket : basketList) {
					try {
						LogWrite.println("getCacheNO:"+basket.getCacheNO());
						LogWrite.println("basket.getCheckCode():"+basket.getCheckCode());
						String rString = modbus.executeCode(basket.getCheckCode());
						LogWrite.println("rString:"+rString);
						if (rString.substring(rString.length() - 1).equals("1")) { ////
							LogWrite.println("=1");
							basket.setBasket(true);   //0������
							if (basket.isLight()) {
								LogWrite.println("����=1");
								modbus.executeCode(basket.getLightCode()); //����
							} else {
								LogWrite.println("�ص�=1");
								modbus.executeCode(basket.getCloseCode()); //
							}
						} else {
							LogWrite.println("!=1");
							basket.setLight(false);
							basket.setBasket(false);
							if (lFlag) {
								LogWrite.println("����!=1");
								modbus.executeCode(basket.getLightCode());
							} else {
								LogWrite.println("����!=1");
								modbus.executeCode(basket.getCloseCode());
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
						LogWrite.println(e);
					}
				}
			}
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
