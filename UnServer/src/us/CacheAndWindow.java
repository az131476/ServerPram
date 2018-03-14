package us;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;


public class CacheAndWindow {
	public HashMap<String, String> getWindowHashMap() throws Exception {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		Connection conn = null;
		try {
			conn = DBManager.getConnection();
			Statement statement = conn.createStatement();
			ResultSet rSet = statement.executeQuery("select CacheNO,WindowNO from fetchwindowlist where cacheNO>''");
			while (rSet.next()) {
				if (hashMap.get(rSet.getString(1)) == null) {
					hashMap.put(rSet.getString(1), rSet.getString(2));
				} else {
					hashMap.put(rSet.getString(1), hashMap.get(rSet.getString(1)) + "," + rSet.getString(2));
				}
			}
			rSet.close();
			statement.close();
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		} finally {
			DBManager.close(conn);
			conn.close();
		}
		return hashMap;
	}
	
	public String getPrinter(String cache) {
		String printer = "";
		Connection conn = null;
		try {
			conn = DBManager.getConnection();
			Statement statement = conn.createStatement();
			ResultSet rSet = statement.executeQuery("select PrintIP from cachelist where cacheNO='"+cache+"'");
			while (rSet.next()) {
				printer = rSet.getString(1);
			}
			rSet.close();
			statement.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			DBManager.close(conn);
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return printer;
	}
}
