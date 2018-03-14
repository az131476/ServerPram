package us;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;


public class ProcessNO {
	private static AtomicInteger counter = null;
	
	public static void init() {
		String procCode = "";
		Connection conn = null;
		try {
			conn = DBManager.getConnection();
			Statement statement = conn.createStatement();
			ResultSet rSet = statement.executeQuery("select procCode from prescriptionlist order by procCode desc");
			if (rSet.next()) {
				procCode = rSet.getString(1);
			}
			rSet.close();
			statement.close();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			DBManager.close(conn);
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		procCode = procCode.equals("") ? "0" : procCode.substring(procCode.length()-4);
		counter = new AtomicInteger(Integer.parseInt(procCode));
	}
	
	public static String getProcCode() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String dateStr = formatter.format(new Date());
		return dateStr + String.format("%04d",counter.incrementAndGet());
	}
}
