import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Scanner;

public class Menu_Search {
	private int MAX_RIDES_PER_PAGE = 5;
	private Scanner scanner;
	private String email;
	private Connection conn;
	private enum State {MAIN, PRINT, SUB, MSG, QUIT};
	private State state;
	private ResultSet rs;
	private String[] input;
	
	Menu_Search(String email, Scanner scanner, Connection conn) {
		this.email = email;
		this.scanner = scanner;
		this.conn = conn;
		this.state = State.MAIN;
		this.run();
	}
	
	void run() {
		while (this.state != State.QUIT) { // set condition to if quit state quit loop
			System.out.println("State: " + state);
			if (this.state == State.MAIN) {
				keywordPrompt();
				try {
					searchDb();
					if (rs.next()) {
						// result set exists change state
						searchDb();
						state = State.PRINT;
					} else {
						// result set returned nothing, try a new search
						System.out.println("Sorry, no matches have been found. Please try a new search");
					}
				} catch (SQLException e) {
					System.out.println("An error has occured, please try again");
					e.printStackTrace();
					continue;
				}
			} else if (this.state == State.PRINT) {
				try {
					printPage();
				} catch (SQLException e) {
					System.out.println("An error has occured, returning to previous menu");
					state = State.MAIN;
					continue;
				}
			} else if (this.state == State.SUB) {
				resultPrompt();
			} else if (this.state == State.MSG) {
				try {
					msgPrompt();
				} catch (SQLException e) {
					
				}
			}
		}
		new Menu_Main(this.email, this.scanner, this.conn);
	}
	
	void keywordPrompt() { // gets input and checks for invalid characters, quits to main menu if quit is entered
		System.out.println("Enter 1-3 keywords or type <Quit> to return to the main menu");
		String[] keywords;
		// while input in not empty form a query looking for lcodes with matching conditions
		while (true) {
		keywords = scanner.nextLine().toLowerCase().split(" ");
			if (keywords[0].equals("quit")) {
				state = State.QUIT;
				break;
			}
			if (keywords.length > 3 && keywords.length < 0) {
				System.out.println("Invalid number of keywords");
			} else {
				break;
			}
		}
		this.input = keywords;
	}
	
	void searchDb() throws SQLException { // TODO, fix this
		System.out.println("Searching...");
		// query returning lcode where location matches keywords
		String subQueryString = "select lcode from locations where lcode = ? or city like ? or "
				+ "prov like ? or address like ?";
		if (input.length == 2) {
			subQueryString += "or lcode = ? or city like ? or prov like ? or address like ?";
		} 
		if (input.length == 3) {
			subQueryString += "or lcode = ? or city like ? or prov like ? or address like ?";
		}
		// query returning rno where lcode matches location
		String subQueryStringEnroute = "select rno from enroute where lcode = (" + subQueryString + ")";
		String searchString = "select distinct * from rides r left join cars c on r.cno = c.cno"
				+ "where src = (" + subQueryString + ") or dest = (" + subQueryString + ") or r.rno = ("
				+ subQueryStringEnroute + ")";
		PreparedStatement search = conn.prepareStatement(searchString);
		for (int i = 0; i < input.length; i++) {
			search.setString(1, input[i]);
			search.setString(2, "%" + input[i] + "%");
			search.setString(3, "%" + input[i] + "%");
			search.setString(4, "%" + input[i] + "%");
		}
		rs = search.executeQuery();
	}
	
	void printPage() throws SQLException { // prints 5 rides at once
		int i = 0;
		System.out.println(String.format("Rno|Price|Date-15s|Seats|Luggage Description|Src  |Dest |"
				+ "Driver-16s|Cno|Make-13s|Model-13s|Year|Owner-16s"));
		while (rs.next() || i < MAX_RIDES_PER_PAGE) {
			String str = "%d-3s|%d-5s|%s-15s|%d-5s|%s-19s|%s-5s|%s-5s|%s-16s|%d-3s|%s-13s|%s-13s"
					+ "|%d-4s|%s-16s";
			str = String.format(str, rs.getInt("rno"), rs.getInt("price"), rs.getDate("rdate").toString(),
					rs.getInt("r.seats"), rs.getString("lugDesc"), rs.getString("src"), rs.getString("dest"),
					rs.getString("driver"), rs.getInt("cno"), rs.getString("make"), rs.getString("model"),
					rs.getInt("year"), rs.getString("owner"));
			System.out.println(str);
			i++;
		}
		state = State.SUB;
	}
	
	void resultPrompt() {
		System.out.println("<Next> Next Page | <Quit> Return to Main Menu | <New> New Search | "
				+ "<Msg> Send a Request to Join Ride");
		switch (scanner.next().toLowerCase()) {
		case "next":
			state = State.PRINT;
		case "quit":
			state = State.QUIT;
		case "new":
			state = State.MAIN;
		case "msg":
			state = State.MSG;
		}
	}
	
	void msgPrompt() throws SQLException {
		int rno;
		int seats;
		System.out.println("Enter the Rno of the ride or type <Cancel> to return");
		rno = scanner.nextInt();
		System.out.println("Enter how many seats you would like to book or type <Cancel> to return");
		rno = scanner.nextInt();
		String msg = String.format("Hi! I would like to book %d seats on %d.", rno, seats);
		// need to query to get reciever of msg then insert new message
		PreparedStatement findReciever = conn.prepareStatement("select distinct driver from rides"
				+ "where rno = ?");
		findReciever.setInt(1, rno);
		ResultSet driver = findReciever.executeQuery();
		String reciever = driver.getString("driver");
		JDBC_Connection.sendMsg(email, reciever, msg, rno, conn);
		System.out.println("Would you like to <New> make a new search or <Quit> return to the main menu?");
		switch(scanner.next().toLowerCase()) {
		case "new":
			state = State.MAIN;
		case "quit":
			state = State.QUIT;
		}
	}
}
