package us;

import java.util.HashMap;


public class PrescriptionDeal implements Runnable {
	private boolean turnFlag = false;
	private boolean pause = false;
	
	public void run() {
		try {
			LogWrite.println(">>>>>加载发药配置");
			ProcessNO.init();
			
			
			HashMap<String, String> cacheWinMap = new CacheAndWindow().getWindowHashMap();
			
			LogWrite.println(">>>>>===============================");
			
			while (true) {
				if (pause) {
					LogWrite.println(">>>>>系统出现未知异常");
					break;
				}
				turnFlag = !turnFlag;
				try {
					// 检测设备是否空闲
					while (!OutParamters.isEquiptmentFree()) {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						LogWrite.println(">>>>>设备不空闲，稍候再进行判断");
					}
					LogWrite.println(">>>>>设备空闲，判断缓存是否空闲");
					
					// 交替获取空闲出药口
					String cache = "";
					if (turnFlag) {
						LogWrite.println("left");
						cache = CacheControl.cacheHashMap.get(CacheType.LEFT).getFreeCache("");
					} else {
						LogWrite.println("right");
						cache = CacheControl.cacheHashMap.get(CacheType.RIGHT).getFreeCache("");
					}
					if (cache.equals("")) {
						LogWrite.println(">>>>>本侧缓存无空闲位置，不满足出药条件");
						Thread.sleep(500);
						continue;
					}
					
					// 判断空闲缓存是否有对应的取药窗口
					String winArr = cacheWinMap.get(cache.substring(0, 1));
					if (winArr == null) {
						LogWrite.println(">>>>本侧缓存无对应取药窗口，不满足出药条件");
						Thread.sleep(500);
						continue;
					}
					
					// 开始处理
					ProcessV processV = new PrescriptionPro().getPatient(winArr);
					if (processV == null) {
						LogWrite.println(">>>>>无待处理处方，窗口："+winArr);
						Thread.sleep(500);
						continue;
					}
					
					// 置状态1
					LogWrite.println(">>>>>置处方状态 为：1");
					if (!new PrescriptionPro().setProcess_1(processV.getPatientID(), processV.getWindow())) {
						LogWrite.println(">>>>>无待处理处方");
						Thread.sleep(500);
						continue;
					}
					
					String procCode = ProcessNO.getProcCode();
					LogWrite.println(">>>>>生成处理序号："+procCode);
					new PrescriptionPro().setProcess_2(procCode, processV.getPatientID());
					
					// 生成出库明细
					new PrescriptionPro().createOutDetail(procCode);
					
					// 生成出药指令
					String outCode = new PrescriptionPro().createOutCode(procCode);
					if (outCode.equals("")) {
						LogWrite.println(">>>>>发药指令为空，处方可能为非机发处方");
						// 发送手工打印
						String printCode = "$Q7010001" + procCode.length() + procCode + CRCKey.getKey() + "*";
						UdpSocket.uSocketHashMap.get(ASocketType.PRINT).sendMessage(new CacheAndWindow().getPrinter(cache.substring(0, 1)), 7016, printCode);
					}
					
					outCode = "$Q4012F1" + cache + "01" + procCode.length() + procCode + outCode + CRCKey.getKey() + "*";
					LogWrite.println(">>>>>发送发药指令：" + outCode);
					SocketClient.clientHashMap.get(1).sendMsg(outCode);
					
					LogWrite.println(">>>>>3");
					new PrescriptionPro().setProcess_2_(procCode, 3);
					
					// 设备忙碌
					OutParamters.setEquiptmentFree(false);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					LogWrite.println(e);
				}
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			LogWrite.println(e);
		}
	}
}
