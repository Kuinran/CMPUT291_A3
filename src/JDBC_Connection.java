import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;

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
	
	public static ResultSet getMsg(String usr) { // recieves unread messages
		ResultSet query = null;
		return query;
	}
}
