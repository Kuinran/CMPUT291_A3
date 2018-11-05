import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;

public class JDBC_Connection { // class for connecting and reusable functions involving connections
	public static Connection connect() throws SQLException {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:tables.db");
			System.out.println("Connection Successful");
		} catch (SQLException e) {
			System.out.println("Error: Could not connect to server");
		}
		return conn;
	}
	
	public static Connection connect(String url) throws SQLException {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + url);
			System.out.println("Connection Successful");
		} catch (SQLException e) {
			System.out.println("Error: Could not connect to server");
		}
		return conn;
	}
	
	public static void sendMsg(String sender, String reciever, String msg, int rno, Connection conn) { // saves a message
		System.out.println("Sending Message");

		String sql = "insert into inbox(email, msgTimestamp, sender, content, rno, seen) values(?,?,?,?,?,?)";
		java.util.Date date = new java.util.Date();
		java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql);){
			pstmt.setString(1, reciever);
			pstmt.setString(2, timestamp.toString());
			pstmt.setString(3, sender);
			pstmt.setString(4, msg);
			pstmt.setInt(5, rno);
			pstmt.setString(6, "n");
			if (pstmt.executeUpdate() > 0) {
				System.out.println("Message sent!");
			} else {
				System.out.println("Message failed to send");
			}
		} catch (Exception e) {
			System.out.println("Connection to server failed");
			e.printStackTrace();
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
			sql = "update inbox set seen = 'y' where seen = 'n';";
			stmt.execute(sql);
		} catch (Exception e) {
			System.out.println("Connection to server failed");
			e.printStackTrace();
		}
	}
}
