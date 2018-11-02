import java.util.Scanner;
import java.sql.Connection;

public class Menu_Main {
	private String usr;
	
	Menu_Main (String usr, Scanner scanner, Connection conn) {
		this.usr = usr;
		JDBC_Connection.getMsg(usr, conn);
		processOptions(scanner, conn);
	}
	
	private void processOptions(Scanner scanner, Connection conn) {
		while (true) {
			System.out.println("Main Menu - Type the number next to the service wanted\n");
			System.out.println("1|Offer a ride\n2|Search for rides\n3|Manage bookings");
			System.out.println("4|Post a ride request\n5|Manage existing requests\n6|Logout");
			System.out.println("7|Quit");
			switch (scanner.next()) {
			case "1": new Menu_RideOffer();
			case "2": new Menu_Search();
			case "3": new Menu_Book();
			case "4": new Menu_PostRq(usr, scanner, conn);
			case "5": new Menu_ManageRq(usr, scanner, conn);
			case "6": new Menu_Login(scanner);
			case "7": System.out.println("Stopping"); return;
			default : System.out.println("Invalid option, please try again");
			}
		}
	}
}
