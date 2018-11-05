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
		List<Integer> rnolist = new ArrayList<>();
		List<Integer> listavailable = new ArrayList<>(); //[rno1,AvailSeats1,rno2,AvailSeats2,..] sqlite only allows TYPE_FWD
		String sql = "select r.rno,r.price,r.rdate,r.src,r.dst, r.seats - COALESCE(SUM(b.seats),0) AS available " + 
				"from rides r left join bookings b on r.rno = b.rno " + 
				"where r.driver = ? " + 
				"group by r.rno,r.price,r.rdate,r.src,r.dst" ; 
		String sqlnum = "select count(r.rno) from rides r left join bookings b on r.rno = b.rno where r.driver = ?";
		
		try {
		PreparedStatement pst = conn.prepareStatement(sql);
		pst.setString(1, usr);
		ResultSet rs1 = pst.executeQuery();
		
		//this is the total number of rides the driver offers
		PreparedStatement pstnum = conn.prepareStatement(sqlnum);
		pstnum.setString(1, usr);
		ResultSet rs2 = pstnum.executeQuery();
		rs2.next();
		int numrno = rs2.getInt(1);
		System.out.println("Total number of rides: " + numrno);
		int selectrno = -1;
		
		//TODO case where member hasn't offered any rides?(specs don't say what to do) Also case where invalid input entered
		int counter = 1;
		while(true) {
		while(rs1.next()) {
			System.out.println(counter + "- The rno is:" + rs1.getString(1) + " with a price of " + rs1.getString(2) +
					"$ on " + rs1.getString(3) + " from " + rs1.getString(4) + " to " + rs1.getString(5) + 
					" with " + rs1.getString(6) + " seats available");
			rnolist.add(rs1.getInt(1));
			listavailable.add(rs1.getInt(1));
			listavailable.add(rs1.getInt(6));
			if(counter % 5 == 0) {
				counter ++;
				break;
			}
			counter++;	
		}
			if(counter - 1 < numrno) {
				System.out.println("Select a ride(press the corresponding number) or press 0 for more options");
				String repons = scanner.next();
				int numrespons = Integer.parseInt(repons);
				if (numrespons == 0) {
					continue;
				}
				selectrno = rnolist.get(numrespons-1);
				break;
			}
			else if (counter-1>=numrno) {
				System.out.println("Select a ride(press the corresponding number)");
				String repons = scanner.next();
				int numrespons = Integer.parseInt(repons);
				selectrno = rnolist.get(numrespons-1);
				break;
			}
		}
		System.out.println("You selected rno " + selectrno);
		//bookings(bno, email, rno, cost, seats, pickup, dropoff)
		System.out.println("Please enter the email of the member you wish to book a ride too: ");
		String email = scanner.next();
		int seats;
		while(true) {
		System.out.println("Please enter the number of seats you wish to book: ");
		String sseats = scanner.next();
		seats = Integer.parseInt(sseats);
		int actualseats = listavailable.get(listavailable.indexOf(selectrno) + 1);
		if(seats>actualseats) {
			System.out.println("WARNING,the ammount of seats selected to be booked is greater than the available seats");
			System.out.println("Do you wish to proceed?\n1- Yes\n0- No");
			String answer = scanner.next();
			int choice = Integer.parseInt(answer);
			if(choice == 1) {
				break;
			}
			else if(choice == 0) {
				continue;
			}
			else {
				System.out.println("Invalid response");
			}
		}
		break;
		}
		System.out.println("Your number of seats booked is: " + seats);
		System.out.println("Enter the cost per seat: ");
		String c = scanner.next();
		int cost = Integer.parseInt(c);
		System.out.println("Enter the lcode for a pickup location: ");
		String src = scanner.next();
		System.out.println("Enter the lcode for a dropoff location: ");
		String dst = scanner.next();
		
		int bno = generate_bno(usr,scanner,conn);
		
		//bookings(bno, email, rno, cost, seats, pickup, dropoff)
		String finalsql = "insert into bookings values (?,?,?,?,?,?,?)";
		PreparedStatement finalpst = conn.prepareStatement(finalsql);
		finalpst.setInt(1, bno);
		finalpst.setString(2, email);
		finalpst.setInt(3, selectrno);
		finalpst.setInt(4, cost);
		finalpst.setInt(5, seats);
		finalpst.setString(6, src);
		finalpst.setString(7, dst);
		finalpst.executeUpdate();
		
		System.out.println("Thank you for booking with us! Your registered bno is " + bno + 
				" and a email was sent to the booked member. Returning to select screen");
		
		//TODO Verify the email sending works. PLEASE VERIFY!!!!!!!!!!!!!!!!!!!
		//JDBC_Connection.sendMsg(usr, email, "Hello, you have been booked on a ride. If you have any concerns contact " + usr , selectrno, conn);
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
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
		JDBC_Connection.sendMsg(usr, membersemail.get(answer-1), "Canceled your request", rnolist.get(answer-1), conn);
		}
		
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}

	}
	public int generate_bno(String usr, Scanner scanner,Connection conn) {
		List<Integer> bno = new ArrayList<>();
		String sql = "SELECT bno FROM bookings";
		
		try {
				Statement stmt  = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()) {
				while(rs.next()) {
					bno.add(rs.getInt("bno"));
				}
			}else {
				return 0;
			}
		}catch (SQLException e) {
			System.out.println(e.getMessage());
		}		
		int e = bno.get(bno.size()-1);
		return e+1;
	}
}
