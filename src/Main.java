import java.util.Scanner;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
	public static Main_States mainState;
	
	public static void main(String[] args) {
		// start program
		Connection conn = null;
		// if there is no arguments
		if (args.length < 1) {
			try {
				conn = JDBC_Connection.connect();
			} catch (SQLException e) {System.out.println("Failed to connect to database");}
		} else { // if a database path is provided
			try {
				conn = JDBC_Connection.connect(args[0]);
			} catch (SQLException e) {System.out.println("Failed to connect to database");}
		}
		// initialize input scanner
		Scanner scanner = new Scanner(System.in);
		while (mainState != Main_States.QUIT) {
			mainState = Main_States.LOGIN;
			new Menu_Login(scanner, conn);
		}
		scanner.close();
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {}
	}
}
