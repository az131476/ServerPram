package us;

import java.sql.Connection;
import java.sql.SQLException;

public class DBManager {
	private static Connection conn;
	private static MySqlPool connectionPool;
	private static DBManager inst;
	public static String serverIP;
	public static String userName;
	public static String userPassword;
	public static String dbName;
	
	public static void close(Connection conn) {
		try {
			connectionPool.returnConnection(conn);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public DBManager() {
		if (inst != null)
			return;

		connectionPool = new MySqlPool("com.mysql.jdbc.Driver", "jdbc:mysql://"+serverIP+":3306/"+dbName+"?useUnicode=true&characterEncoding=utf8", ""+userName+"", ""+userPassword+""); 
		try {
			connectionPool.createPool();
			inst = this;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Connection getConnection() {
		if (inst == null)
			new DBManager();

		try {
			conn = connectionPool.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return conn;
	}
}
