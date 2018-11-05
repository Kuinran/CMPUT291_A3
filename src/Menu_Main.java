import java.util.Scanner;
import java.sql.Connection;
import java.sql.SQLException;

public class Menu_Main {
	private String usr;
	private enum State {ACTIVE, QUIT};
	private State state;
	Menu_Main (String usr, Scanner scanner, Connection conn) {
		Main.mainState = Main_States.MAIN;
		this.usr = usr;
		JDBC_Connection.getMsg(usr, conn);
		state = State.ACTIVE;
		processOptions(scanner, conn);
	}
	
	private void processOptions(Scanner scanner, Connection conn) {
		while (state == State.ACTIVE) {
			System.out.println("Main Menu - Type the number next to the service wanted\n");
			System.out.println("1|Offer a ride\n2|Search for rides\n3|Manage bookings");
			System.out.println("4|Post a ride request\n5|Manage existing requests\n6|Logout");
			System.out.println("7|Quit");
			switch (scanner.next()) {
			case "1": new Menu_RideOffer(usr, scanner, conn); Main.mainState = Main_States.OFFER; break;
			case "2": new Menu_Search(usr, scanner, conn); Main.mainState = Main_States.SEARCH; break;
			case "3": new Menu_Book(usr, scanner, conn); Main.mainState = Main_States.BOOK; break;
			case "4": new Menu_PostRq(usr, scanner, conn); Main.mainState = Main_States.POST; break;
			case "5": new Menu_ManageRq(usr, scanner, conn); Main.mainState = Main_States.MANAGE_RQ; break;
			case "6": Main.mainState = Main_States.LOGIN; state = State.QUIT; break;
			case "7": System.out.println("Stopping"); state = State.QUIT; 
						Main.mainState = Main_States.QUIT; break;
			default : System.out.println("Invalid option, please try again"); break;
			}
		}
	}
}
