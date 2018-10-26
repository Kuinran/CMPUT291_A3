import java.sql.Connection;
import java.sql.Date;
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
	
	public static void sendMsg(String sender, String reciever, String msg, int rno, Connection conn) { // saves a message
		System.out.println("Sending Message");
		String sql = String.format("insert into inbox (email, msgTimestamp, sender, content, rno, seen) "
				+ "values ('%s', datetime('now'), '%s', '%s', '%d', 'n');", reciever, sender, msg, rno);
		try {
			Statement stmt = conn.createStatement();
			if (!stmt.execute(sql)) {
				System.out.println("Message sent!");
			} else {
				System.out.println("Message failed to send");
			}
		} catch (Exception e) {
			System.out.println("Connection to server failed");
		}
	}
	
	public static void getMsg(String usr, Connection conn) { // recieves unread messages
		System.out.println("New Messages: ");
		ResultSet query = null;
		String sql = String.format("select * from inbox where email = '%s' and seen = 'n';", usr);
		try {
			Statement stmt = conn.createStatement();
			query = stmt.executeQuery(sql);
			while (query.next()) {
				String time = query.getString(2);
				String sender = query.getString(3);
				String content = query.getString(4);
				int rno = query.getInt(5);
				System.out.println(String.format("From: %s\n[%s] Ride: %d \n%s\n-------\n", sender, time, rno, content));
			}
			//TODO: Uncomment when doing demo
			//sql = "update inbox set seen = 'y' where seen = 'n';";
			//stmt.execute(sql);
		} catch (Exception e) {
			System.out.println("Connection to server failed");
		}
	}
}
