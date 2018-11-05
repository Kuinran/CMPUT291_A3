import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.sql.ResultSet;

public class Menu_Book {
	
private String usr;
	
	public Menu_Book(String usr, Scanner scanner, Connection conn) {
		this.usr = usr;
		Initialize(usr,scanner,conn);
	}
	
	public void Initialize(String usr,Scanner scanner, Connection conn ) {
		while(true) {
		System.out.println("What would you like to do?\n1-Book Member\n2-Cancel booking\n3-Exit to Main Menu");
		String option = scanner.next();
		int answer = Integer.parseInt(option);
		if (answer == 1) {
			book_member(usr,scanner,conn);
		}
		else if (answer == 2) {
			cancel_booking(usr,scanner,conn);
		}
		else if(answer == 3) {
			
			break;
		}
		else {
			System.out.println("invalid input");
		}
		}
	}

	public void book_member(String usr,Scanner scanner, Connection conn) {
		
	}
	public void cancel_booking(String usr,Scanner scanner, Connection conn) {
		
	}
}
