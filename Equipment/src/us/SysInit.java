package us;

import javax.servlet.http.HttpServlet;



/**
 * Servlet implementation class SysInit
 */
public class SysInit extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@Override
	public void init(){
		new Thread(new SysInitT()).start();
	}
}
