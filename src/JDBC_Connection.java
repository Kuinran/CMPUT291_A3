import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

public class JDBC_Connection { // class for connecting and reusable functions involving connections
	public static Connection connect() throws SQLException{
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:C:/sqlite/CMPUT291/tables.db");
			System.out.println("Connection Successful");
		} catch (SQLException e){
			System.out.println("Error: Could not connect to server");
		}
		return conn;
	}
	
	public static void sendMsg(String sender, String reciever, String msg) { // saves a message
		
	}
	
	public static void getMsg(String usr, Connection conn) { // recieves unread messages
		ResultSet query = null;
		String sql = String.format("select * from inbox where email = '%s' and seen = 'n'", usr);
		try {
			Statement stmt = conn.createStatement();
			query = stmt.executeQuery(sql);
			while (query.next()) {
				Timestamp time = query.getTimestamp(2);
				String sender = query.getString(3);
				String content = query.getString(4);
				int rno = query.getInt(5);
				System.out.println(String.format("From: %s Ride: %d [%s] %s\n", sender, rno, time.toString(), content));
			}
		} catch (Exception e) {
			System.out.println("Connection to server failed");
		}
	}
}
