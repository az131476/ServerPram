package us;


public class PrescriptionOut implements Runnable {
	private String code;
	
	public PrescriptionOut(String code) {
		this.code = code;
	}
	
	@Override
	public void run() {
		// 解析出药结果
		// $Q4062K11S0112201608160001010010002020001100100B2jz*
		LogWrite.println(">>>>>收到结果反馈："+code);
		try {
			String cache = code.substring(8, 10);
			String procCode = code.substring(14, 26);

			// 处理出药结果
			LogWrite.println(">>>>>处理出药明细反馈");
			new PrescriptionPro().finishDetail(procCode, code.substring(26, code.length() - 5));
			
			// 发送出药完成，打开翻板指令
			LogWrite.println(">>>>>打开翻板出药");
			String outCode = "$Q407" + code.substring(5, 26) + CRCKey.getKey() + "*";
			SocketClient.clientHashMap.get(1).sendMsg(outCode);
			
			// 发送打印指令
			String printCode = "$Q701" + code.substring(8, 26) + CRCKey.getKey() + "*";
			UdpSocket.uSocketHashMap.get(ASocketType.PRINT).sendMessage(new CacheAndWindow().getPrinter(cache.substring(0, 1)), 7016, printCode);
			
			// 发送亮灯
			LogWrite.println("code发送亮灯："+code);
			if ("1".equals(code.substring(8, 9))) {
				CacheControl.cacheHashMap.get(CacheType.LEFT).setLight(code.substring(8, 10));
			} else {
				CacheControl.cacheHashMap.get(CacheType.RIGHT).setLight(code.substring(8, 10));
			}
			/*if ("1".equals(code.substring(8, 9))) {
			   CacheControl.cacheHashMap.get(CacheType.LEFT).setLight(code.substring(9, 10));
		    } else {
			   CacheControl.cacheHashMap.get(CacheType.RIGHT).setLight(code.substring(9, 10));
		    }*/
			
			new PrescriptionPro().finfishClear(procCode);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LogWrite.println(e);
		}
	}

}
