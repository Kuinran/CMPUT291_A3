import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBC_Connection {
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
}
