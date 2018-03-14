package us;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class LightAssistStock implements Runnable {
	private String patientID;
	private String prescriptionNO;
	private String fetchWindow;
	
	//<code><key>QUERY_STOCK</key><patientid></patientid><prescriptionno></prescriptionno><windowno></windowno></code>
	public LightAssistStock(String code) {
		patientID = code.substring(code.indexOf("<patientid>") + 11, code.indexOf("</patientid>"));
		prescriptionNO = code.substring(code.indexOf("<prescriptionno>") + 15, code.indexOf("</prescriptionno>"));
		fetchWindow = code.substring(code.indexOf("<windowno>") + 10, code.indexOf("</windowno>"));
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Connection conn = null;
		try {
			String lightFlg = "00";
			String tipInfo = "";
			String curTipInfo = "";
			conn = DBManager.getConnection();
			Statement statement = conn.createStatement();
			ResultSet rSet = statement.executeQuery("select AreaID,RowNo,ColNo,LightFlg from dispense_list where PatientID='"+patientID+"'");
			while (rSet.next()) {
				if (fetchWindow.equals(rSet.getString(1))) {
					lightFlg = rSet.getString(4);
					curTipInfo = rSet.getString(1) + " - " + rSet.getString(2) + " - " + rSet.getString(3);
				} else {
					tipInfo = rSet.getString(1) + " - " + rSet.getString(2) + " - " + rSet.getString(3);
				}
			}
			rSet.close();
			
			// 判断是否正在配药
			if (curTipInfo.equals("") && tipInfo.equals("")) {
				curTipInfo = "正在配药";
			}
			
			statement.close();
			
			// 发送亮灯、提示
			// $Q602 000 A01 XXXXXXXXX XXXX*
			String code = "$Q602" + String.format("A%02d", Integer.parseInt(fetchWindow)) + "A" + lightFlg + (curTipInfo.equals("") ? tipInfo : curTipInfo) + CRCKey.getKey() + "*";
			new CodeSender().send(SocketServer.socketHashMap.get("A"+fetchWindow), code);
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
