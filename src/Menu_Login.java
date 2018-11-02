import java.io.Console;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

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
		
		try (//{ // connect and check credential
			Connection conn = JDBC_Connection.connect();
			//Statement stmt = conn.createStatement();
				PreparedStatement pstmt = conn.prepareStatement(sql))
				{
			pstmt.setString(1, usr[0]);
			pstmt.setString(2, usr[1]);
			
			
			ResultSet rs = pstmt.executeQuery();
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
		String name = scanner.next();
		System.out.println();
		System.out.print("Phone # (###-###-####): ");
		String phone = scanner.next();
		//String sql = String.format("select * from members where email = '%s';", usr[0]);
		String sql = "select * from members where email = ?";
		
		try (//{ // connect
			Connection conn = JDBC_Connection.connect();
			//Statement stmt = conn.createStatement();
				PreparedStatement pstmt = conn.prepareStatement(sql))
				
				{
			pstmt.setString(1, usr[0]);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				// user exists already, query returned nothing
				System.out.println("User already exists, terminating program");
				return;
			} else {
				// create login
				//sql = String.format("insert into members (email, name, phone, pwd) "
						//+ "values ('%s', '%s', '%s', '%s')", usr[0], name, phone, usr[1]);
				sql = "insert into members(email, name, phone, pwd) Values(?,?,?,?)";
				//fix connections
				Connection connn = JDBC_Connection.connect();
				PreparedStatement stmt = connn.prepareStatement(sql);
				stmt.setString(1, usr[0]);
				stmt.setString(2, name);
				stmt.setString(3, phone);
				stmt.setString(4, usr[1]);
				int update = stmt.executeUpdate();
				if (update>0) { // successful insertion
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
