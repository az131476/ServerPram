package us;

public class ResetButton implements Runnable{
	/*
	 * ��ҩ��λ
	 * @see java.lang.Runnable#run()
	 */
	private String icode ;
    public ResetButton(String code){
    	this.icode = code;
    }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			/*
			 * ��ҩ�Ÿ�λ  --92-5C
			 */
			String code = "0000000000060106"+ String.format("%04X", 86) + "005C";
			Modbus.modbus.executeCode(code);
			/*
			 * ��ҩ
			 */
			code = "0000000000060106"+ String.format("%04X", 92) + "005C";
			Modbus.modbus.executeCode(code);
			/*
			 * ��ҩ
			 */
			code = "0000000000060106"+ String.format("%04X", 80) + "005C";
			Modbus.modbus.executeCode(code);
			/*
			 * X��
			 */
			code = "0000000000060106"+ String.format("%04X", 66) + "005C";
			Modbus.modbus.executeCode(code);
			/*
			 * Y��
			 */
			code = "0000000000060106"+ String.format("%04X", 72) + "005C";
			Modbus.modbus.executeCode(code);
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			code = "0000000000060106"+ String.format("%04X", 48) + "0001";
			Modbus.modbus.executeCode(code);
			
			LogWrite.println("��λ���");
			break;
		}
	}
}
