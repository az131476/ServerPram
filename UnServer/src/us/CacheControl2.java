package us;

import java.util.ArrayList;
import java.util.HashMap;

	public class CacheControl2 implements Runnable {
		public static HashMap<CacheType, CacheControl2> cacheHashMap = new HashMap<CacheType, CacheControl2>();
		private Modbus modbus;
		private final static Object basketLock = new Object();
		private ArrayList<BasketStock> basketList;
		
		public CacheControl2(CacheType cacheType, String ipAddress) {
			modbus = new Modbus(ipAddress, 502);
			
			basketList = new ArrayList<BasketStock>();
			// DO 25/26/27/28	19/1A/1B/1C
			// DI 33/34/35/36	21/22/23/24
			BasketStock b1 = new BasketStock();
			b1.setCacheNO("1S");
	        b1.setCheckCode("000000000006010300210001");
	        b1.setLightCode("0000000000060106001B0001");  //000000000006010300210001
	        b1.setCloseCode("0000000000060106001B0000"); //0000000000060106001B0001
	        basketList.add(b1);

	        BasketStock b2 = new BasketStock();
	        b2.setCacheNO("1X");
	        b2.setCheckCode("000000000006010300220001");
	        b2.setLightCode("0000000000060106001C0001");
	        b2.setCloseCode("0000000000060106001C0000");
	        basketList.add(b2);
	        
	        BasketStock b3 = new BasketStock();
			b3.setCacheNO("2S");
	        b3.setCheckCode("000000000006010300210001");
	        b3.setLightCode("0000000000060106001B0001");  //000000000006010300210001
	        b3.setCloseCode("0000000000060106001B0000"); //0000000000060106001B0001
	        basketList.add(b3);
	
	        BasketStock b4 = new BasketStock();
	        b4.setCacheNO("2X");
	        b4.setCheckCode("000000000006010300220001");
	        b4.setLightCode("0000000000060106001C0001");
	        b4.setCloseCode("0000000000060106001C0000");
	        basketList.add(b4);
	        
			CacheControl2.cacheHashMap.put(cacheType, this);
		}
		
		/**
		 * 获取空闲缓存口
		 * @param cache
		 * @return
		 */
		public synchronized String getFreeCache(String cache) {
			String freeCache = "";
			synchronized (basketLock) {
				for (BasketStock basket : basketList) {
					if (basket.getCacheNO().equals(cache)) {//为空
						if (basket.isBasket() && !basket.isLight()) { //有篮子、灭灯 --空闲
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
		 * 控制亮灯
		 * @param cache
		 */
		public synchronized void setLight(String cache) {
			synchronized (basketLock) {
				for (BasketStock basket : basketList) {
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
							LogWrite.println("basket.getCheckCode():"+basket.getCheckCode());
							String rString = modbus.executeCode(basket.getCheckCode());
							LogWrite.println("rString:"+rString);
							if (!rString.substring(rString.length() - 1).equals("1")) {
								basket.setBasket(true);   //0有篮子
								if (basket.isLight()) {
									modbus.executeCode(basket.getLightCode());
								} else {
									modbus.executeCode(basket.getCloseCode());
								}
							} else {
								basket.setLight(false);
								basket.setBasket(false);
								if (lFlag) {
									modbus.executeCode(basket.getLightCode());
								} else {
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
