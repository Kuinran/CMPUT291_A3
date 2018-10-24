import java.util.Scanner;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class Main {
	public static String[] login() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Email: ");
		String[] usrData = new String[2];
		usrData[0] = scanner.next();
		System.out.println();
		System.out.print("Password: ");
		usrData[1] = scanner.next();
		System.out.println();
		scanner.close();
		return usrData;
	}

	public static void main(String[] args) {
		String[] usr;
		usr = login();
		String sql = String.format("select * from members where email = '%s' and pwd = '%s';", usr[0], usr[1]);
		try {
			Connection conn = JDBC_Connection.connect();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (!rs.next()) {
				System.out.println("Incorrect Login Credentials");
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
}
