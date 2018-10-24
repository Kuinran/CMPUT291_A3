import java.util.Scanner;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class Main {
	public static String[] getCred() { // get user and password data
		Scanner scanner = new Scanner(System.in);
		System.out.print("Email: ");
		String[] usrData = new String[2];
		usrData[0] = helpers.safeString(scanner.next());
		System.out.println();
		System.out.print("Password: ");
		usrData[1] = helpers.safeString(scanner.next());
		System.out.println();
		scanner.close();
		return usrData;
	}
	
	public static void login() { // get credentials and check if they match database
		String[] usr = getCred(); // io
		
		String sql = String.format("select * from members where email = '%s' and pwd = '%s';", usr[0], usr[1]);
		try { // connect and check credential
			Connection conn = JDBC_Connection.connect();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (!rs.next()) { // if no matches are returned
				System.out.println("Incorrect Login Credentials, terminating program");
				return;
			}
			System.out.println("Login Successful");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// TODO: Start main menu
		return;
	}
	
	public static void register() { // check input and add new member if not currently in db
		String[] usr = getCred();
		String sql = String.format("select * from members where email = '%s';", usr[0]);
		try { // connect
			Connection conn = JDBC_Connection.connect();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				// user exists already
				System.out.println("User already exists, terminating program");
				return;
			} else {
				// create login
				sql = String.format("insert into members (email, pwd) values ('%s', '%s')", usr[0], usr[1]);
				if (stmt.execute(sql)) {
					System.out.println("Registration Successful");
				} else {
					System.out.println("Registration Failed, terminating program");
					return;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// redirect to login screen
		login();
	}

	public static void main(String[] args) {
		// start program
		System.out.println("'Login' or 'Register' to continue");
		Scanner scanner = new Scanner(System.in);
		String input = scanner.next();
		if (input.equals("Register")) {
			register();
		} else if  (input.equals("Login")) {
			login();
		} else {
			System.out.println("Invalid input, terminating program");
		}	
	}
}
