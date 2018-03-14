package us;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;



public class ClearAssistStock implements Runnable {
	private String patientID;
	private String prescriptionNO;
	private String fetchWindow;
	
	//<code><key>CLEAR_STOCK</key><patientid></patientid><prescriptionno></prescriptionno><windowno></windowno></code>
	public ClearAssistStock(String code) {
		patientID = code.substring(code.indexOf("<patientid>") + 11, code.indexOf("</patientid>"));
		prescriptionNO = code.substring(code.indexOf("<prescriptionno>") + 15, code.indexOf("</prescriptionno>"));
		fetchWindow = code.substring(code.indexOf("<windowno>") + 10, code.indexOf("</windowno>"));
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Connection conn = null;
		try {
			conn = DBManager.getConnection();
			Statement statement = conn.createStatement();
			statement.execute("update dispense_list set patientID=null,patientName=null,state=1 where PatientID='"+patientID+"'");
			statement.execute("update prescriptionlist set procFlg=8,HospitalTime=now() where PatientID='"+patientID+"' and PrescriptionNo='"+prescriptionNO+"'");
			statement.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LogWrite.println(e);
		} finally {
			DBManager.close(conn);
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
