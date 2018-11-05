import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Menu_ManageRq {
	private String Uemail; // user email
	private Scanner scanner;
	private Connection conn;
	private String EmailIn, ListEmail[];//email of poster, list of emails for checking
	private int id, size;//used to check valid input
	private enum State {MAIN, SEARCH, DELETE, QUIT, MSG, NUKE, PRINT};//state machine
	private State state;
	Menu_ManageRq(String usr, Scanner scanner, Connection conn) {
		this.Uemail = usr;
		this.scanner = scanner.reset();
		this.conn = conn;
		this.state = State.MAIN;
		this.mode();//run
	}

	void mode () {
		//scanner.nextLine();
		while(this.state != State.QUIT) {//set exit condition 
			// System.out.println("State: " + state);
			if (this.state == State.MAIN ) {//ask user for input
				Prompt();
			} else if (this.state == State.SEARCH) {//if search
				try{
					search();
				}  catch (SQLException e) {
					System.out.println(e.getMessage());	
					state = State.MAIN;
					continue;
				}
				
			} else if (this.state == State.DELETE) {//if delete
				try {
					delete();
				} catch (SQLException e) {
					System.out.println(e.getMessage());
					state = State.MAIN;
					continue;
				}
				
			} else if (this.state == State.MSG) {//if user wants to send message
				sendMsg();
				
			} else if (this.state == State.NUKE) {//delete row from requests
				try {
					DeleteRow();
				} catch (SQLException e) {
					System.out.println(e.getMessage());
					state = State.MAIN;
					continue;
				}
			} 
		}
	}
	
	void Prompt() {//asks for user input, set machine state accordingly
		System.out.println("Enter 'Search', 'Delete', or 'quit'");
		while(true) {
			String input = scanner.next().toLowerCase();
			if (input.equals("quit")) {
				state = State.QUIT;
				break;
			}
			if (input.equals("search")) {
				state = State.SEARCH;
				break;
			}
			if (input.equals("delete")) {
				state = State.DELETE;
				break;
			} else {
				System.out.println("Invalid input");
				state = State.MAIN;
				break;
			}
		}
	}
	
	private void search() throws SQLException {//search function
		System.out.println("Enter a city or location code of the pickup location, or exit to return");
		String location = scanner.next().toLowerCase();//lcode, city or exit to return to top menu
		
		switch(location) {//if exit
		case "exit":
			state = State.MAIN;
			break;
		default:
			int i = parseUlocation(location);//check if lcode or city
			if (i == 0) {
				System.out.println("Invalid input, please try again");//if not, return to menu
				state = State.SEARCH;
				break;
			}
			this.ListEmail = ListSearch(i, location);
			msgPrompt();//ask user to send message
		}
	}
	
	private void msgPrompt() {//ask user to send message
		//TODO fix invalid email error
		System.out.println("Would you like to send a message? enter yes or no.");
		
		switch(scanner.next().toLowerCase()) {
		case "no"://exit case
			state = State.MAIN;
			break;
		case "yes"://if user wants to send message
			System.out.println("Please enter the email of the user you wish to message");
			String email = scanner.next().toLowerCase();
			for (int i = 0; i < ListEmail.length; i++) {//check if email is valid
				if (email == ListEmail[i]) {
					this.EmailIn = email;
					state = State.MSG;//set state machine
					break;
				}
			}
			System.out.println("invald email");//invalid email, returns to search menu
			state = State.SEARCH;
			break;
		default:
			System.out.println("invalid option");//invalid option, returns to search menu
			state = State.SEARCH;
		}
	}
	
	
	private void sendMsg() {//send the message
		System.out.println("Please enter a message, pressing 'enter' will end the message");
		String message = scanner.next();
		JDBC_Connection.sendMsg(Uemail, EmailIn, message, -1, conn);//message function in JDBC_Connection
		
	}
	
	private int parseUlocation(String location)throws SQLException {//check if input is lcode or city
		String code = "select * from locations where lcode = ?";//init sql query to check lcode
		String city = "select * from locations where city = ?";//init sql query to check city
		
		PreparedStatement stmt = conn.prepareStatement(city);//prepared statements to prevent sql injection
		PreparedStatement pstmt = conn.prepareStatement(code);
		stmt.setString(1, location);//sent variable in preparedstaments 
		pstmt.setString(1, location);
		ResultSet rs2 = stmt.executeQuery();//execute query for city
		ResultSet rs1 = pstmt.executeQuery();//execute query for lcode
		if (rs1.isBeforeFirst()) {
			return 1;//1 if lcode
		}
		if (rs2.isBeforeFirst())	{
			return 2;//2 if city
		}
		return 0; //0 if no match
			
	}
	
	private String[] ListSearch(int i, String location) throws SQLException {
		List<String> count = new ArrayList<>();//used to count number of lines
		List<String> email = new ArrayList<>();//array of returned emails
		String lcodesql = "select rid, email, rdate, pickup, dropoff, amount from requests where pickup = ?";//init sql query
		String citysql = "select rid, email, rdate, pickup, dropoff, amount from requests, locations where city = ?";
		
		if (i == 1) {//if user entered lcode
			PreparedStatement pstmt = conn.prepareStatement(lcodesql);//prepared statement init
			pstmt.setString(1, location);//set statement variable
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {//while there are things in result set
				String rid = Integer.toString(rs.getInt("rid"));
				Date date = rs.getDate("rdate");
				String Email = rs.getString("email");
				String pickup = rs.getString("pickup");
				String dropoff = rs.getString("dropoff");
				String amt = Integer.toString(rs.getInt("amount"));
				count.add(rid + "\t" + Email + "\t" + date.toString() + "\t" + pickup + "\t" + dropoff + "\t" + amt + "\n");//add to arraylist
				email.add(rs.getString("email").toLowerCase());//add to email list
			}
			
		}else if (i == 2) {
			PreparedStatement pstmt = conn.prepareStatement(citysql);//see prev comments
				pstmt.setString(1, location);
				ResultSet rs = pstmt.executeQuery();
				while(rs.next()) {
					String rid = Integer.toString(rs.getInt("rid"));
					Date date = rs.getDate("rdate");
					String pickup = rs.getString("pickup");
					String dropoff = rs.getString("dropoff");
					String amt = Integer.toString(rs.getInt("amount"));
					count.add(rid + "\t" + rs.getString("email") + "\t" + date.toString() + "\t" + pickup + "\t" + dropoff + "\t" + amt + "\n");
					email.add(rs.getString("email").toLowerCase());
				}
			}
		//edit strings to print as specified in spec
		int s = count.size();
		String List[] = new String[s];
		String emailList[] = new String[s];
		for (int j = 0; j < s; j ++) {
			List[j] = count.get(j);
			emailList[j] = email.get(j);
		}
		if (s <= 5) {
			printList(List, List.length);
		}else {
			String all[][] = new String[s/5][];
			for (int j = 0; j < s/5; j++) {
				all[j] = UpdateList(List, j);
			}
			printList(all[0], 5);//print first page
			int k = 1;
			System.out.println("Please input 'n' for next page or anything else to continue.");
			String input = scanner.next().toLowerCase();
			while (input.equals("n") && k < s/5) {//print next page
				printList(all[k], all[k].length);
				System.out.println("Please type n for next page or anything else to continue.");
				input = scanner.next().toLowerCase();
				k++;
			}
		}
		return emailList;
	}
	
	private String[] UpdateList(String list[], int mod) {//edit list if more than 5 rows, store in 2D array
		String newString[] = new String[5];
		mod = mod*5;
		for (int i = mod; i < mod + 5; i++) {
			newString[i] = list[i];
		}
		return newString;
	}
	
	private void printList(String list[], int l) {//print list
		for (int i = 0; i < l; i++) {
			System.out.println(list[i]);
		}
	}
	
	private void delete() throws SQLException {//delete function
		int ridList[] = listAll();//list all requests
		this.size = ridList.length;//size of all requests
		System.out.println("Select one ID of request you wish to delete, or exit to return");
		String id = scanner.next().toLowerCase();
		
		switch(id) {
		case "exit"://exit case
			state = State.MAIN;
			break;
		default:
			int ID = Integer.parseInt(id);
			for (int i = 0; i < ridList.length; i++) {//find rid to delete
				if (ID == ridList[i]) {
					this.id = ID;
					state = State.NUKE;
					break;
				}
			}
			System.out.println("Invalid id, please try again.");//if invalid rid, return to delete menu
			state = State.DELETE;
		}
	}
	
	private int[] listAll() throws SQLException {
		List<Integer> count = new ArrayList<>();//find number of rows returned
		String sql = "select rid, rdate, pickup, dropoff, amount from requests where requests.email = ?";//init query
		PreparedStatement pstmt = conn.prepareStatement(sql);//prepared statement, prevent injection
			pstmt.setString(1, Uemail);//set statement conditions
			ResultSet rs = pstmt.executeQuery();//get results
			System.out.println("ID" + "\t" + "date" + "\t" + "pickup"+ "\t" +"dropoff"+ "\t" + "amount");
			while(rs.next()) {//while there are results
				count.add(rs.getInt("rid"));//make a list of rid
				System.out.println(rs.getInt("rid") + "\t" +
								rs.getDate("rdate") + "\t" +
								rs.getString("pickup") + "\t" +
								rs.getString("dropoff") + "\t" +
								rs.getInt("amount"));	
				}//print row
		
		int ridList[] = new int[count.size()];//init array
		for (int i = 0; i < count.size(); i++) {
			ridList[i] = count.get(i);
		}
		return ridList;//return array of rid
	}
	
	private void DeleteRow() throws SQLException{//delete a row
		
		String sql = "delete from requests where rid = ? and email = ?";//see prev comments 
		
		PreparedStatement pstmt = conn.prepareStatement(sql);//see prev comment for prepared statements
			pstmt.setInt(1, id);//given rid
			pstmt.setString(2, Uemail);//user email
			pstmt.executeUpdate();
			
		if (size == 1) {
			mode();
		}
		System.out.println("Would you like to delete another request? enter yes or no.");
		
		switch(scanner.next().toLowerCase()) {//ask user to delete another row, set machine state accordingly
		case "yes":
			state = State.DELETE;
			break;
		case "no":
			state = State.MAIN;
			break;
		default:
			System.out.println("invalid input, returning to menu");
			state = State.MAIN;
		}

	}
	
}
