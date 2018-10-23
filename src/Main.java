import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
	public String[] login() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("User: ");
		String[] usrData = new String[2];
		usrData[0] = scanner.next();
		System.out.println();
		System.out.print("Password: ");
		usrData[1] = scanner.next();
		System.out.println();
		return usrData;
	}
	
	public static void main() {
		
	}
}
