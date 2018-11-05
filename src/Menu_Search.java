import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Menu_Search {
	private Scanner scanner;
	private String email;
	private Connection conn;
	private enum State {MAIN, SEARCH, PRINT, SUB, MSG, QUIT};
	private State state;
	private String[] input;
	private List<HashMap<String, String>> result; 
	private ResultSet rs;
	private int pageNumber;
	
	Menu_Search(String email, Scanner scanner, Connection conn) {
		this.email = email;
		this.scanner = scanner.reset();
		this.conn = conn;
		this.state = State.MAIN;
		this.run();
		this.pageNumber = 0;
	}
	
	void run() {
		scanner.nextLine();
		while (this.state != State.QUIT) { // set condition to if quit state quit loop
			System.out.println("State: " + state);
			if (this.state == State.MAIN) {
				keywordPrompt();
			} else if (this.state == State.SEARCH) {
				try {
					searchDb();
					if (!result.isEmpty()) {
						// result set exists change state
						state = State.PRINT;
					} else {
						// result set returned nothing, try a new search
						System.out.println("Sorry, no matches have been found. Please try a new search");
						state = State.MAIN;
					}
				} catch (SQLException e) {
					System.out.println("An error has occured, please try again");
					e.printStackTrace();
					state = State.MAIN;
					continue;
				}
			} else if (this.state == State.PRINT) {
				try {
					printPage();
				} catch (Exception e) {
					System.out.println("An error has occured, returning to previous menu");
					e.printStackTrace();
					state = State.MAIN;
					continue;
				}
			} else if (this.state == State.SUB) {
				resultPrompt();
			} else if (this.state == State.MSG) {
				try {
					msgPrompt();
				} catch (SQLException e) {
					System.out.println("An error has occured, returning to previous menu");
					e.printStackTrace();
					state = State.MAIN;
					continue;
				}
			}
		}
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
				state = State.SEARCH;
				break;
			}
		}
		this.input = keywords;
	}
	
	void searchDb() throws SQLException {
		System.out.println("Searching...");
		// query returning lcode where location matches keywords
		// TODO: fix
		String subQueryString = "select lcode from locations where lcode = ? or city like ? or "
				+ "prov like ? or address like ?";
		if (input.length > 1) {
			subQueryString += " or lcode = ? or city like ? or prov like ? or address like ?";
		} 
		if (input.length > 2) {
			subQueryString += " or lcode = ? or city like ? or prov like ? or address like ?";
		}
		// query returning rno where lcode matches location
		String subQueryStringEnroute = "select rno from enroute where lcode = (" + subQueryString + ")";
		String searchString = "select distinct * from rides r left join cars c on r.cno = c.cno"
				+ " where src in (" + subQueryString + ") or dst in (" + subQueryString + ") or r.rno in ("
				+ subQueryStringEnroute + ")";
		PreparedStatement search = conn.prepareStatement(searchString);
		for (int i = 0; i < input.length; i++) {
			search.setString(i * 12 + 1, input[i]);
			search.setString(i * 12 + 2, '%' + input[i] + '%');
			search.setString(i * 12 + 3, '%' + input[i] + '%');
			search.setString(i * 12 + 4, '%' + input[i] + '%');
			search.setString(i * 12 + 5, input[i]);
			search.setString(i * 12 + 6, '%' + input[i] + '%');
			search.setString(i * 12 + 7, '%' + input[i] + '%');
			search.setString(i * 12 + 8, '%' + input[i] + '%');
			search.setString(i * 12 + 9, input[i]);
			search.setString(i * 12 + 10, '%' + input[i] + '%');
			search.setString(i * 12 + 11, '%' + input[i] + '%');
			search.setString(i * 12 + 12, '%' + input[i] + '%');
		}
		rs = search.executeQuery();
		HashMap<String, String> temp = new HashMap<String, String>();
		result = new ArrayList<HashMap<String, String>>();
		while (rs.next()) {
			temp = new HashMap<String, String>();
			temp.put("rno", Integer.toString(rs.getInt("rno")));
			temp.put("price", Integer.toString(rs.getInt("price")));
			temp.put("rdate", rs.getString("rdate"));
			temp.put("seats", Integer.toString(rs.getInt("seats")));
			temp.put("lugDesc", rs.getString("lugDesc"));
			temp.put("src", rs.getString("src"));
			temp.put("dst", rs.getString("dst"));
			temp.put("driver", rs.getString("driver"));
			temp.put("cno", Integer.toString(rs.getInt("cno")));
			temp.put("make", rs.getString("make"));
			temp.put("model", rs.getString("model"));
			temp.put("year", Integer.toString(rs.getInt("year")));
			temp.put("owner", rs.getString("owner"));
			result.add(temp);
		}
	}
	
	void printPage() { // prints 5 rides at once
		int i = pageNumber * 5;
		System.out.println(String.format("Rno|Price|Date$-15s|Seats|Luggage Description|Src  |Dest |"
				+ "Driver$-16s|Cno|Make$-13s|Model$-13s|Year|Owner$-16s"));
		while (i < result.size()) {
			String str = "%s$-3s|%s$-5s|%s$-15s|%s$-5s|%s$-19s|%s$-5s|%s$-5s|%s$-16s|%s$-3s|%s$-13s|"
					+ "%s$-13s|%s$-4s|%s$-16s";
			str = String.format(str, result.get(i).get("rno"), result.get(i).get("price"), 
					result.get(i).get("rdate"),	result.get(i).get("seats"), result.get(i).get("lugDesc"), 
					result.get(i).get("src"), result.get(i).get("dst"), result.get(i).get("driver"), 
					result.get(i).get("cno"), result.get(i).get("make"), result.get(i).get("model"),
					result.get(i).get("year"), result.get(i).get("owner"));
			System.out.println(str);
			if (i % 5 == 4) {
				break;
			}
			i++;
		}
		state = State.SUB;
	}
	
	void resultPrompt() {
		System.out.println("<Prev> Previous Page | <Next> Next Page | <Quit> Return to Main Menu | "
				+ "<New> New Search | <Msg> Send a Request to Join Ride");
		switch (scanner.next().toLowerCase()) {
		case "prev":
			if (pageNumber == 0) {
				System.out.println("There is no previous page");
				break;
			} else {
				System.out.println(pageNumber);
				pageNumber--;
				pageNumber--;
				state = State.PRINT;
			}
		case "next":
			pageNumber++;
			state = State.PRINT;
			break;
		case "quit":
			pageNumber = 0;
			state = State.QUIT;
			break;
		case "new":
			pageNumber = 0;
			state = State.MAIN;
			scanner.nextLine();
			break;
		case "msg":
			state = State.MSG;
			break;
		}
	}
	
	void msgPrompt() throws SQLException {
		int rno;
		int seats;
		System.out.println("Enter the Rno of the ride or type <Cancel> to return");
		rno = scanner.nextInt();
		System.out.println("Enter how many seats you would like to book or type <Cancel> to return");
		seats = scanner.nextInt();
		String msg = String.format("Hi! I would like to book %d seats on %d.", seats, rno);
		// need to query to get reciever of msg then insert new message
		PreparedStatement findReciever = conn.prepareStatement("select distinct driver from rides "
				+ "where rno = ?");
		findReciever.setInt(1, rno);
		ResultSet driver = findReciever.executeQuery();
		String reciever = driver.getString("driver");
		JDBC_Connection.sendMsg(email, reciever, msg, rno, conn);
		System.out.println("Would you like to <New> make a new search or <Quit> return to the main menu?");
		switch(scanner.next().toLowerCase()) {
		case "new":
			pageNumber = 0;
			state = State.MAIN;
			scanner.nextLine();
			break;
		case "quit":
			pageNumber = 0;
			state = State.QUIT;
			break;
		}
	}
}
