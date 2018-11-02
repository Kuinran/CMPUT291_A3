import java.io.Console;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.sql.PreparedStatement;

public class Menu_Login {
	Menu_Login(Scanner scanner) {
		System.out.println("'Login' or 'Register' to continue");
		String input = scanner.next().toLowerCase();
		if (input.equals("register")) {
			register(scanner);
		} else if  (input.equals("login")) {
			login(scanner);
		} else {
			System.out.println("Invalid input, terminating program");
		}	
	}
	
	private static String[] getCred(Scanner scanner) { // get user and password data, email is case insensitive
		String password = "";
		Console cnsl = null;
		System.out.print("Email: ");
		String[] usrData = new String[2];
		//usrData[0] = Helpers.safeString(scanner.next().toLowerCase());
		usrData[0] = scanner.next().toLowerCase();
		System.out.println();
		System.out.print("Password: ");
		try { // if using cmd line can use console to hide password TODO: test this in console
			cnsl = System.console();
			password = cnsl.readPassword().toString();
		} catch (Exception e) {
			password = scanner.next().toLowerCase();
		}
		//usrData[1] = Helpers.safeString(password);
		usrData[1] = password;
		System.out.println();
		return usrData;
	}
	
	private static void login(Scanner scanner) { // get credentials and check if they match database
		String[] usr = getCred(scanner); // io
	
		//String sql = String.format("select * from members where email = '%s' and pwd = '%s';", usr[0], usr[1]);		
		String sql = "select * from members where email = ? and pwd = ?";
		try  // connect and check credential
			(Connection conn = JDBC_Connection.connect();
			PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, usr[0]);
			pstmt.setString(2, usr[1]);
			ResultSet rs = pstmt.executeQuery(sql);
			if (!rs.next()) { // if no matches are returned
				System.out.println("Incorrect Login Credentials, terminating program");
				return;
			}
			System.out.println("Login Successful");
			new Menu_Main(usr[0], scanner, conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return;
	}
	
	private static void register(Scanner scanner) { // check input and add new member if not currently in db
		String[] usr = getCred(scanner);
		System.out.print("Name: ");
		String name = Helpers.safeString(scanner.next());
		System.out.println();
		System.out.print("Phone # (###-###-####): ");
		String phone = (Helpers.safeString(scanner.next()));
		String sql = String.format("select * from members where email = '%s';", usr[0]);
		try { // connect
			Connection conn = JDBC_Connection.connect();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				// user exists already, query returned nothing
				System.out.println("User already exists, terminating program");
				return;
			} else {
				sql = "INSERT INTO members(email, name, phone, pwd) VALUES (?,?,?,?)";
				try (PreparedStatement pstmt = conn.prepareStatement(sql)){
							pstmt.setString(1, usr[0]);
							pstmt.setString(2, name);
							pstmt.setString(3, phone);
							pstmt.setString(4, usr[1]);
						}
				// create login
				//sql = String.format("insert into members (email, name, phone, pwd) "
				//		+ "values ('%s', '%s', '%s', '%s')", usr[0], name, phone, usr[1]);
				if (!stmt.execute(sql)) { // successful insertion
					System.out.println("Registration Successful");
				} else {
					System.out.println("Registration Failed, terminating program");
					return;
				}
				new Menu_Main(usr[0], scanner, conn);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
