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
			// ���´���״̬
			LogWrite.println(">>>>>�յ���ҩ��ɣ�"+code);
			
			String procCode = code.substring(14, 26);
			// 
			new PrescriptionPro().setProcess_2_(procCode, 4);
			
			// �����豸����
			LogWrite.println(">>>>>�����豸״̬Ϊ����");
			OutParamters.setEquiptmentFree(true);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
