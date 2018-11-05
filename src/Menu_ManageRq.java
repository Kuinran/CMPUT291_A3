import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Menu_ManageRq {
	private String Uemail;
	private Scanner scanner;
	private Connection conn;
	private String EmailIn, ListEmail[];
	private int id, size;
	private enum State {MAIN, SEARCH, DELETE, QUIT, MSG, NUKE, PRINT};
	private State state;
	Menu_ManageRq(String usr, Scanner scanner, Connection conn) {
		this.Uemail = usr;
		this.scanner = scanner.reset();
		this.conn = conn;
		this.mode();
		this.state = State.MAIN;
		mode();
	}

	void mode () {
		//scanner.nextLine();
		while(this.state != State.QUIT) { 
			//bug with search
			if (this.state == State.MAIN ) {
				Prompt();
			} else if (this.state == State.SEARCH) {
				try{
					search();
				}  catch (SQLException e) {
					System.out.println(e.getMessage());	
					state = State.MAIN;
					continue;
				}
				
			} else if (this.state == State.DELETE) {
				try {
					delete();
				} catch (SQLException e) {
					System.out.println(e.getMessage());
					state = State.MAIN;
					continue;
				}
				
			} else if (this.state == State.MSG) {
				sendMsg();
			} else if (this.state == State.NUKE) {
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
	
	void Prompt() {
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
	
	private void search() throws SQLException {
		System.out.println("Enter a city or location code of the pickup location, or exit to return");
		String location = scanner.next().toLowerCase();
		
		switch(location) {
		case "exit":
			state = State.MAIN;
			break;
		default:
			int i = parseUlocation(location);
			if (i == 0) {
				System.out.println("Invalid input, please try again");
				state = State.SEARCH;
				break;
			}
			this.ListEmail = ListSearch(i, location);
			msgPrompt();
		}
	}
	
	private void msgPrompt() {
		System.out.println("Would you like to send a message? enter yes or no.");
//		String input = scanner.next().toLowerCase();
		
		switch(scanner.next().toLowerCase()) {
		case "no":
			state = State.MAIN;
			break;
		case "yes":
			System.out.println("Please enter the email of the user you wish to message");
			String email = scanner.next().toLowerCase();
			for (int i = 0; i < ListEmail.length; i++) {
				if (email == ListEmail[i]) {
					this.EmailIn = email;
					state = State.MSG;
					break;
				}
			}
			System.out.println("invald email");
			state = State.SEARCH;
			break;
		default:
			System.out.println("invalid option");
			state = State.SEARCH;
		}
	}
	
	
	private void sendMsg() {
		System.out.println("Please enter a message, pressing 'enter' will end the message");
		String message = scanner.next();
		JDBC_Connection.sendMsg(Uemail, EmailIn, message, -1, conn);
		
	}
	
	private int parseUlocation(String location)throws SQLException {//check if input is lcode or city
		String code = "select * from locations where lcode = ?";
		String city = "select * from locations where city = ?";
		
		PreparedStatement stmt = conn.prepareStatement(city);
		PreparedStatement pstmt = conn.prepareStatement(code);
		stmt.setString(1, location);
		pstmt.setString(1, location);
		ResultSet rs2 = stmt.executeQuery();
		ResultSet rs1 = pstmt.executeQuery();
		if (rs1.isBeforeFirst()) {
			return 1;//1 if lcode
		}
		if (rs2.isBeforeFirst())	{
			return 2;//2 if city
		}
		return 0; //0 if no match
			
	}
	
	private String[] ListSearch(int i, String location) throws SQLException {
		List<String> count = new ArrayList<>();
		List<String> email = new ArrayList<>();
		String lcodesql = "select rid, email, rdate, pickup, dropoff, amount from requests where pickup = ?";
		String citysql = "select rid, email, rdate, pickup, dropoff, amount from requests, locations where city = ?";
		
		if (i == 1) {
			PreparedStatement pstmt = conn.prepareStatement(lcodesql);
			pstmt.setString(1, location);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				String rid = Integer.toString(rs.getInt("rid"));
				Date date = rs.getDate("rdate");
				String Email = rs.getString("email");
				String pickup = rs.getString("pickup");
				String dropoff = rs.getString("dropoff");
				String amt = Integer.toString(rs.getInt("amount"));
				count.add(rid + "\t" + Email + "\t" + date.toString() + "\t" + pickup + "\t" + dropoff + "\t" + amt + "\n");
				email.add(rs.getString("email").toLowerCase());
			}
			
		}else if (i == 2) {
			PreparedStatement pstmt = conn.prepareStatement(citysql);
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
			printList(all[0], 5);
			int k = 1;
			System.out.println("Please input 'n' for next page or anything else to continue.");
			String input = scanner.next().toLowerCase();
			while (input.equals("n") && k < s/5) {
				printList(all[k], all[k].length);
				System.out.println("Please type n for next page or anything else to continue.");
				input = scanner.next().toLowerCase();
				k++;
			}
		}
		return emailList;
	}
	
	private String[] UpdateList(String list[], int mod) {
		String newString[] = new String[5];
		mod = mod*5;
		for (int i = mod; i < mod + 5; i++) {
			newString[i] = list[i];
		}
		return newString;
	}
	
	private void printList(String list[], int l) {
		for (int i = 0; i < l; i++) {
			System.out.println(list[i]);
		}
	}
	
	private void delete() throws SQLException {
		//TODO fix more edge cases
		int ridList[] = listAll();
		this.size = ridList.length;
		System.out.println("Select one ID of request you wish to delete, or exit to return");
		String id = scanner.next().toLowerCase();
		
		switch(id) {
		case "exit":
			state = State.MAIN;
			break;
		default:
			int ID = Integer.parseInt(id);
			for (int i = 0; i < ridList.length; i++) {
				if (ID == ridList[i]) {
					this.id = ID;
					state = State.NUKE;
					break;
				}
			}
			System.out.println("Invalid rid, please try again.");
			state = State.DELETE;
		}
	}
	
	private int[] listAll() throws SQLException {
		List<Integer> count = new ArrayList<>();
		String sql = "select rid, rdate, pickup, dropoff, amount from requests where requests.email = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, Uemail);
			ResultSet rs = pstmt.executeQuery();
			System.out.println("ID");
			while(rs.next()) {
				count.add(rs.getInt("rid"));
				System.out.println(rs.getInt("rid") + "\t" +
								rs.getDate("rdate") + "\t" +
								rs.getString("pickup") + "\t" +
								rs.getString("dropoff") + "\t" +
								rs.getInt("amount"));	
				}
		
		int ridList[] = new int[count.size()];
		for (int i = 0; i < count.size(); i++) {
			ridList[i] = count.get(i);
		}
		return ridList;
	}
	
	private void DeleteRow() throws SQLException{
		
		String sql = "delete from requests where rid = ? and email = ?";
		
		PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.setString(2, Uemail);
			pstmt.executeUpdate();
			
		if (size == 1) {
			mode();
		}
		System.out.println("Would you like to delete another request? enter yes or no.");
		
		switch(scanner.next().toLowerCase()) {
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
