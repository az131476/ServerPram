package us;


public class PrescriptionFinish implements Runnable {
	private String code;
	
	public PrescriptionFinish(String code) {
		this.code = code;
	}
	
	@Override
	public void run() {
		try {
			//$Q4082F11S0112201608230022ZOeH*
			// 更新处方状态
			LogWrite.println(">>>>>收到发药完成："+code);
			
			String procCode = code.substring(14, 26);
			// 
			new PrescriptionPro().setProcess_2_(procCode, 4);
			
			// 更新设备空闲
			LogWrite.println(">>>>>更新设备状态为空闲");
			OutParamters.setEquiptmentFree(true);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
