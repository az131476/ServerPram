package us;

public class MainFrame {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LogWrite.println(">>>>>>>>>>>>>>>>>>>>ϵͳ��ʼ��ʼ��<<<<<<<<<<<<<<<<<<<<");
		new Thread(new SysInitT()).start();
	}

}
