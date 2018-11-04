import java.util.Scanner;
import java.sql.Connection;
import java.sql.SQLException;
public class Main {
	public static void main(String[] args) {
		// start program
		Connection conn = null;
		try {
			conn = JDBC_Connection.connect();
		} catch (SQLException e) {System.out.println("Failed to connect to database");}
		Scanner scanner = new Scanner(System.in);
		new Menu_Login(scanner, conn);
		scanner.close();
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {}
	}
}
