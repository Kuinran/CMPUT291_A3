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
		//bookings(bno, email, rno, cost, seats, pickup, dropoff)
		List<String> membersemail = new ArrayList<>();
		List<Integer> rnolist = new ArrayList<>();
		System.out.println(usr + " Select a booking you would like to cancel(press corresponding number):\n");
		String sql = "select b.bno,b.email,b.rno,b.cost,b.seats,b.pickup,b.dropoff "
				+ "from bookings b, rides r where r.driver = ? and r.rno = b.rno";
		String del = "delete from bookings where rno = ?";
		try {
		PreparedStatement pst = conn.prepareStatement(sql);
		pst.setString(1, usr);
		ResultSet rs1 = pst.executeQuery();
		int counter = 1;
		while(rs1.next()) {
		System.out.println(counter + "- bno: " + rs1.getString(1) + " members email: " + rs1.getString(2) + " rno: " + rs1.getString(3) + 
				" cost: " + rs1.getString(4) + " seats: " + rs1.getString(5) + " pickup: " + rs1.getString(6) + " dropoff: " + rs1.getString(7));
		membersemail.add(rs1.getString(2));
		rnolist.add(rs1.getInt(3));
		counter++;
		
		}
		String a = scanner.next();
		int answer = Integer.parseInt(a);
		PreparedStatement pst2 = conn.prepareStatement(del);
		pst2.setInt(1, rnolist.get(answer-1));
		pst2.executeUpdate();
		System.out.println("rno "+ rnolist.get(answer-1) + " Deleted");
		}
		
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}

	}
}
