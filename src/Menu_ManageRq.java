import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Menu_ManageRq {
	private String usr;
	Menu_ManageRq(String usr, Scanner scanner, Connection conn) {
		this.usr = usr;
		mode(scanner, usr, conn);
	}
	
	private void mode (Scanner scanner, String usr, Connection conn) {
		System.out.println("Enter 'Search', 'Delete', or 'exit'");
		String input = scanner.next().toLowerCase();
		if (input.equals("search")) {
			search(scanner, conn);
		} else if (input.equals("delete")) {
			delete(usr, scanner, conn);
		} else if (input.equals("exit")) {
			new Menu_Main(usr, scanner, conn);
		} else {
			System.out.println("Invalid input");
			new Menu_ManageRq(usr, scanner, conn);
		}
	}
	
	private void search(Scanner scanner, Connection conn) {
		System.out.println("Enter a city or location code");
		String location = scanner.next().toLowerCase();
		int i = parseUlocation(location);
		
	}
	
	private int parseUlocation(String location) {//check if input is lcode or city
		List<String> loccode = new ArrayList<>();
		List<String> loccity = new ArrayList<>();
		String code = "select lcode from locations";
		String city = "select city from locations";
		
		try (Connection conn = JDBC_Connection.connect();
				Statement stmt = conn.createStatement();
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
	
	private void delete(String usr, Scanner scanner, Connection conn) {
		//consider edge cases
		listAll(usr);
		System.out.println("Select one ID of request you wish to delete.");
		int id = scanner.nextInt();
		
		String sql = "delete from requests where rid = ? and email = ?";
		
		try(//Connection conn = JDBC_Connection.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setInt(1, id);
			pstmt.setString(2, usr);
			pstmt.executeUpdate();
			
		} catch (SQLException e){
			System.out.println(e.getMessage());	
		}
		System.out.println("Would you like to delete another request? enter yes or no.");
		String del = scanner.next().toLowerCase();
		while (!del.equals("yes") || !del.equals("no")) {
			System.out.println("please enter yes or no.");
			del = scanner.next().toLowerCase();
		}
		if (del.equals("yes")) {
			delete(usr, scanner, conn);
		} else if (del.equals("no")) {
			mode(scanner, usr, conn);
		}
	}
	
	private int listAll(String usr) {
		List<Integer> count = new ArrayList<>();
		String sql = "select rid, rdate, pickup, dropoff, amount from requests where requests.email = ?";
		try (Connection conn = JDBC_Connection.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
				pstmt.setString(1, usr);
				ResultSet rs = pstmt.executeQuery();
				System.out.println("ID");
				while(rs.next()) {
					count.add(rs.getInt("rid"));
					System.out.println(rs.getInt("rid") + "\t" +
									rs.getString("rdate") + "\t" +
									rs.getString("pickup") + "\t" +
									rs.getString("dropoff") + "\t" +
									rs.getInt("amount"));	
				}
			
		}  catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		int i = count.get(count.size()-1);
		return i;
	}
	
	
}
