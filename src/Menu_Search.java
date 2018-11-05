import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Menu_Search {
	// initialize variables that all functions use
	private int MAX_RIDES_PER_PAGE = 5;
	private Scanner scanner;
	private String email;
	private Connection conn;
	// enums for FSM
	private enum State {MAIN, SEARCH, PRINT, SUB, MSG, QUIT};
	private State state;
	private String[] input;
	private List<HashMap<String, String>> result; 
	private int pageNumber;
	
	// Constructor fills out initial data and sets state to MAIN
	Menu_Search(String email, Scanner scanner, Connection conn) {
		this.email = email;
		this.scanner = scanner.reset();
		this.conn = conn;
		this.state = State.MAIN;
		this.run();
		this.pageNumber = 0;
	}
	
	// Runtime loop
	void run() {
		// clear the scanner buffer by jumping to a new line
		scanner.nextLine();
		// until the state is QUIT run this class
		while (this.state != State.QUIT) {
			// System.out.println("State: " + state);
			if (this.state == State.MAIN) {
				// get input and then set state to SEARCH
				keywordPrompt();
			} else if (this.state == State.SEARCH) {
				// try to find results
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
				// print 5 results, page number will note which sets of data you should see
				try {
					printPage();
				} catch (Exception e) {
					System.out.println("An error has occured, returning to previous menu");
					e.printStackTrace();
					state = State.MAIN;
					continue;
				}
			} else if (this.state == State.SUB) {
				// if printing is successful get user input for next part
				resultPrompt();
			} else if (this.state == State.MSG) {
				// send a message
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
			// case insensitive and split search terms by space
			keywords = scanner.nextLine().toLowerCase().split(" ");
			// if user wants to quit change state and exit function
			if (keywords[0].equals("quit")) {
				state = State.QUIT;
				break;
			}
			// if the input is 0 length or is more than 3 keywords prompt usr to try again
			if (keywords.length > 3 && keywords.length < 0) {
				System.out.println("Invalid number of keywords");
			} else {
				// successful entry, try searching for the keywords
				state = State.SEARCH;
				break;
			}
		}
		// save keywords in an array once validated
		this.input = keywords;
	}
	
	// searches a database and stores the results in a array of hashmaps with keys = columns
	void searchDb() throws SQLException {
		System.out.println("Searching...");
		// query returning lcode where location matches keywords
		String subQueryString = "select lcode from locations where (lcode = ? or city like ? or "
				+ "prov like ? or address like ?)";
		// query returning rno where lcode matches location
		String subQueryStringEnroute = "select rno from enroute where lcode in (" + subQueryString + ")";
		// query built from subQueries that find rides with lcodes that satisfy one of the fields in
		// locations
		String searchString = "select distinct * from rides r left join cars c on r.cno = c.cno"
				+ " where (src in (" + subQueryString + ") or dst in (" + subQueryString + 
				") or r.rno in ("+ subQueryStringEnroute + "))";
		// if there is another keyword narrow search by concat
		if (input.length > 1) {
			searchString += " and (src in ("+ subQueryString + ") or dst in (" + subQueryString
					+ ") or r.rno in ("+ subQueryStringEnroute + "))";
		}
		// if there is another keyword narrow search by concat
		if (input.length > 2) {
			searchString += " and (src in ("+ subQueryString + ") or dst in (" + subQueryString
					+ ") or r.rno in ("+ subQueryStringEnroute + "))";
		}
		// System.out.println(searchString);
		PreparedStatement search = conn.prepareStatement(searchString);
		// populate statement using for loop, each input is inserted 12 times
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
		ResultSet rs = search.executeQuery();
		// set hashmap which will be inserted to array
		HashMap<String, String> temp = new HashMap<String, String>();
		// clear result prior to populating
		result = new ArrayList<HashMap<String, String>>();
		// fill result will all returned result sets
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
	// prints results at number dictated by MAX_RIDES_PER_PAGE
	void printPage() {
		// set starting index
		int i = pageNumber * MAX_RIDES_PER_PAGE;
		// print header
		System.out.println(String.format("Rno|Price|%1$-10s|Seats|Luggage Description|Src  |Dest |"
				+ "%2$-20s|Cno|%3$-13s|%4$-13s|Year|%5$-16s", "Date", "Driver", "Make", "Model", "Owner"));
		// print results until there are no more results or page limit is reached
		while (i < result.size()) {
			String str = "%1$-3s|%2$-5s|%3$-10s|%4$-5s|%5$-19s|%6$-5s|%7$-5s|%8$-20s|%9$-3s|%10$-13s|"
					+ "%11$-13s|%12$-4s|%13$-16s";
			str = String.format(str, result.get(i).get("rno"), result.get(i).get("price"), 
					result.get(i).get("rdate"),	result.get(i).get("seats"), result.get(i).get("lugDesc"), 
					result.get(i).get("src"), result.get(i).get("dst"), result.get(i).get("driver"), 
					result.get(i).get("cno"), result.get(i).get("make"), result.get(i).get("model"),
					result.get(i).get("year"), result.get(i).get("owner"));
			System.out.println(str);
			// if page limit is reached break from loop
			if (i % MAX_RIDES_PER_PAGE == MAX_RIDES_PER_PAGE - 1) {
				break;
			}
			i++;
		}
		System.out.println();
		// change state to sub if successful
		state = State.SUB;
	}
	
	// prints submenu after results have been found
	void resultPrompt() {
		// print prompt
		System.out.println("<Prev> Previous Page | <Next> Next Page | <Quit> Return to Main Menu | "
				+ "<New> New Search | <Msg> Send a Request to Join Ride");
		switch (scanner.next().toLowerCase()) {
		case "prev":
			if (pageNumber == 0) {
				System.out.println("There is no previous page");
				break;
			} else {
				// more page back if possible
				System.out.println(pageNumber);
				pageNumber--;
				pageNumber--;
				state = State.PRINT;
			}
		case "next":
			// increment page number
			pageNumber++;
			state = State.PRINT;
			break;
		case "quit":
			// change state and break
			pageNumber = 0;
			state = State.QUIT;
			break;
		case "new":
			// change state back to start
			pageNumber = 0;
			state = State.MAIN;
			scanner.nextLine();
			break;
		case "msg":
			// msg state
			state = State.MSG;
			break;
		}
	}
	
	// gets the rno and # of seats then sends a msg 
	void msgPrompt() throws SQLException {
		int rno;
		int seats;
		// get rno
		System.out.println("Enter the Rno of the ride or type <Cancel> to return");
		rno = scanner.nextInt();
		// get seats
		System.out.println("Enter how many seats you would like to book or type <Cancel> to return");
		seats = scanner.nextInt();
		// format msg
		String msg = String.format("Hi! I would like to book [%d] seat[s] on ride [%d].", seats, rno);
		// need to query to get reciever of msg then insert new message
		PreparedStatement findReciever = conn.prepareStatement("select distinct driver from rides "
				+ "where rno = ?");
		findReciever.setInt(1, rno);
		ResultSet driver = findReciever.executeQuery();
		String reciever = driver.getString("driver");
		// send msg
		JDBC_Connection.sendMsg(email, reciever, msg, rno, conn);
		// new prompt
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
