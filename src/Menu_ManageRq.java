import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Scanner;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Menu_ManageRq {
	private String usr;
	private enum State {MAIN, QUIT};
	private State state;
	Menu_ManageRq(String usr, Scanner scanner, Connection conn) {
		this.usr = usr;
		this.state = State.MAIN;
		mode(scanner, conn);
		

	}
	
	private void mode (Scanner scanner, Connection conn) {
		while(this.state != State.QUIT) { 
			System.out.println("Enter 'Search', 'Delete', or 'exit'");
			String input = scanner.next().toLowerCase();
			if (input.equals("search")) {
				try{
					search(scanner, conn);
				}  catch (SQLException e) {
					System.out.println(e.getMessage());	
				}
			} else if (input.equals("delete")) {
				delete(scanner, conn);
			} else if (input.equals("exit")) {
				new Menu_Main(usr, scanner, conn);
			} else {
				System.out.println("Invalid input");
				state = State.QUIT;
			}
		}
	}
	
	private void search(Scanner scanner, Connection conn) throws SQLException {
		System.out.println("Enter a city or location code of the pickup location");
		String location = scanner.next().toLowerCase();
		int i = parseUlocation(location, conn);
		if (i == 0) {
			System.out.println("Invalid input, please try again");
			search(scanner, conn);
		}
		String list[] = ListSearch(i, location, scanner, conn);
		//message user
		MessageUsr(scanner, conn, list);
		mode(scanner, conn);
	}
	
	private void MessageUsr(Scanner scanner, Connection conn, String Emails[]) {
		System.out.println("Would you like to send a message? enter yes or no.");
		String input = scanner.next().toLowerCase();
		if(input.equals("no")) {
			mode(scanner, conn);
		} else if (!input.equals("no") && !input.equals("yes")){
			System.out.println("invalid input, please enter yes or no.");
			MessageUsr(scanner, conn, Emails);
		}
		System.out.println("Please enter the email of the user you wish to message");
		String email = scanner.next().toLowerCase();
		for (int i = 0; i < Emails.length; i++) {
			if (email == Emails[i]) {
				sendMsg(email, conn, scanner);
			}
		}
		System.out.println("Invalid email, please try again");
		MessageUsr(scanner, conn, Emails);
	}
	
	
	private void sendMsg(String email, Connection conn, Scanner scanner) {
		System.out.println("Please enter a message, pressing 'enter' will end the message");
		String message = scanner.next();
		
		Calendar currenttime = Calendar.getInstance();
	    Date sqldate = new Date((currenttime.getTime()).getTime());
		
		String sql = "insert into inbox(email, msgTimestamp, sender, content, rno, seen) values(?,?,?,?,?,?)";
		try(PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, email);
			pstmt.setDate(2, sqldate);
			pstmt.setString(3, usr);
			pstmt.setString(4, message);
			pstmt.setInt(5, -1);
			pstmt.setString(6, "n");
		}catch (SQLException e){
			System.out.println(e.getMessage());
		}
	}
	
	private int parseUlocation(String location, Connection conn)throws SQLException {//check if input is lcode or city
		List<String> loccode = new ArrayList<>();
		List<String> loccity = new ArrayList<>();
		String code = "select lcode from locations";
		String city = "select city from locations";
		
		try (Statement stmt = conn.createStatement();
				ResultSet rs1 = stmt.executeQuery(code);
				ResultSet rs2 = stmt.executeQuery(city)){
			
			if (rs1.next() || rs2.next()) {
				while(rs1.next()) {
					loccode.add(rs1.getString("lcode").toLowerCase());
				}
				while (rs2.next()) {
					loccity.add(rs2.getString("city").toLowerCase());
				}
			} else {
				return 0;
			}
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());	
		}
		for (int i = 0; i < loccode.size();i++) {
			if (location == loccode.get(i)) {
				return 1;	//1 if lcode
			}
		}
		for (int i = 0; i< loccity.size(); i++) {
			if (location == loccity.get(i)) {
				return 2;	//2 if city
			}
		}
		return 0;	//0 if no match
	}
	
	private String[] ListSearch(int i, String location, Scanner scanner, Connection conn) throws SQLException {
		List<String> count = new ArrayList<>();
		List<String> email = new ArrayList<>();
		String lcodesql = "select rid, email, rdate, pickup, dropoff, amount from requests where pickup = ?";
		String citysql = "select rid, email, rdate, pickup, dropoff, amount from requests, location where city = ?";
		
		if (i == 1) {
			PreparedStatement pstmt = conn.prepareStatement(lcodesql);
			pstmt.setString(1, location);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				String rid = Integer.toString(rs.getInt("rid"));
				String date = rs.getString("rdate");
				String Email = rs.getString("email");
				String pickup = rs.getString("pickup");
				String dropoff = rs.getString("dropoff");
				String amt = Integer.toString(rs.getInt("amount"));
				count.add(rid + "\t" + Email + "\t" + date + "\t" + pickup + "\t" + dropoff + "\t" + amt + "\n");
				email.add(Email);
			}
			
		}else if (i == 2) {
			PreparedStatement pstmt = conn.prepareStatement(citysql);
				pstmt.setString(1, location);
				ResultSet rs = pstmt.executeQuery();
				while(rs.next()) {
					String rid = Integer.toString(rs.getInt("rid"));
					String date = rs.getString("rdate");
					String pickup = rs.getString("pickup");
					String dropoff = rs.getString("dropoff");
					String amt = Integer.toString(rs.getInt("amount"));
					count.add(rid + "\t" + rs.getString("email") + "\t" + date + "\t" + pickup + "\t" + dropoff + "\t" + amt + "\n");
					email.add(rs.getString("email"));
				}
			}
		int s = count.size();
		String List[] = new String[s];
		String emailList[] = new String[s];
		for (int j = 0; j < s; j ++) {
			List[j] = count.get(j);
			emailList[j] = email.get(j);
		}
		if (s >= 5) {
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
	
	private void delete(Scanner scanner, Connection conn) {
		//TODO fix more edge cases
		int ridList[] = listAll(usr, conn);
		int rListSize = ridList.length;
		System.out.println("Select one ID of request you wish to delete.");
		int id = scanner.nextInt();
		for (int i = 0; i < ridList.length; i++) {
			if (id == ridList[i]) {
				DeleteRow(id, scanner, conn, rListSize);
			}
		}
		System.out.println("Invalid rid, please try again.");
		delete(scanner, conn);

	}
	
	private int[] listAll(String usr, Connection conn){
		List<Integer> count = new ArrayList<>();
		String sql = "select rid, rdate, pickup, dropoff, amount from requests where requests.email = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)){
				pstmt.setString(1, usr);
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
			
		}  catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		int ridList[] = new int[count.size()];
		for (int i = 0; i < count.size(); i++) {
			ridList[i] = count.get(i);
		}
		return ridList;
	}
	
	private void DeleteRow(int id, Scanner scanner, Connection conn, int size){
		
		String sql = "delete from requests where rid = ? and email = ?";
		
		try(PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setInt(1, id);
			pstmt.setString(2, usr);
			pstmt.executeUpdate();
			
		} catch (SQLException e){
			System.out.println(e.getMessage());	
		}
		if (size == 1) {
			mode(scanner,conn);
		}
		System.out.println("Would you like to delete another request? enter yes or no.");
		String del = scanner.next().toLowerCase();
		while (!del.equals("yes") && !del.equals("no")) {
			System.out.println("please enter yes or no.");
			del = scanner.next().toLowerCase();
		}
		if (del.equals("yes")) {
			delete(scanner, conn);
		} else if (del.equals("no")) {
			mode(scanner,conn);
		}
	}
	
}
