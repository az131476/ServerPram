package us;

public class MainFrame {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LogWrite.println(">>>>>>>>>>>>>>>>>>>>系统开始初始化<<<<<<<<<<<<<<<<<<<<");
		new Thread(new SysInitT()).start();
	}

}
