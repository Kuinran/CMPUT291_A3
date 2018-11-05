import java.io.Console;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Menu_Login {
	private Connection conn;
	private Scanner scanner;
	
	Menu_Login(Scanner scanner, Connection conn) {
		System.out.println("<Login> or <Register> to continue or <Quit> to exit");
		String input = scanner.next().toLowerCase();
		this.scanner = scanner;
		this.conn = conn;
		if (input.equals("register")) {
			register();
		} else if  (input.equals("login")) {
			login();
		} else if (input.equals("quit")) {
			Main.mainState = Main_States.QUIT;
			return;
		} else {
			System.out.println("Invalid input, terminating program");
		}	
	}
	
	private String[] getCred() { // get user and password data, email is case insensitive
		String password = "";
		Console cnsl = null;
		System.out.print("Email: ");
		String[] usrData = new String[2];
		
		usrData[0] = scanner.next().toLowerCase();
		System.out.println();
		System.out.print("Password: ");
		try { // if using cmd line can use console to hide password TODO: test this in console
			cnsl = System.console();
			password = new String(cnsl.readPassword());
		} catch (Exception e) {
			password = scanner.next().toLowerCase();
		}
		usrData[1] = password;
		System.out.println();
		return usrData;
	}
	
	private void login() { // get credentials and check if they match database
		String[] usr = getCred(); // io
		
		String sql = "select * from members where email = ? and pwd = ?";
		
		try ( // connect and check credential
			
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
	
	private void register() { // check input and add new member if not currently in db
		String[] usr = getCred();
		System.out.print("Name: ");
		String name = scanner.next();
		System.out.println();
		System.out.print("Phone # (###-###-####): ");
		String phone = scanner.next();
		
		String sql = "select * from members where email = ?";
		String sqlupdate = "insert into members(email, name, phone, pwd) Values(?,?,?,?)";
		try (
			Connection conn = JDBC_Connection.connect();
			
				PreparedStatement pstmt = conn.prepareStatement(sql);
				PreparedStatement stmt = conn.prepareStatement(sqlupdate))
				{
			pstmt.setString(1, usr[0]);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				// user exists already, query returned nothing
				System.out.println("User already exists, terminating program");
				return;
			} else {
			
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