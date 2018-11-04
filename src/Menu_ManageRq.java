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
	Menu_ManageRq(String usr, Scanner scanner, Connection conn) throws SQLException {
		this.usr = usr;
		mode(scanner, usr, conn);
	}
	
	private void mode (Scanner scanner, String usr, Connection conn) throws SQLException {
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
	
	private void search(Scanner scanner, Connection conn) throws SQLException {
		System.out.println("Enter a city or location code of the pickup location");
		String location = scanner.next().toLowerCase();
		int i = parseUlocation(location);
		if (i == 0) {
			System.out.println("Invalid input, please try again");
			search(scanner, conn);
		}
		ListSearch(i, location, scanner, conn);
		//message user
	}
	
	private int parseUlocation(String location)throws SQLException {//check if input is lcode or city
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
	
	private void ListSearch(int i, String location, Scanner scanner, Connection conn) throws SQLException {
		List<String> count = new ArrayList<>();
		String lcodesql = "select rid, rdate, pickup, dropoff, amount from requests where pickup = ?";
		String citysql = "select rid, rdate, pickup, dropoff, amount from requests, location where location.lcode = pickup and city = ?";
		
		if (i == 1) {
			PreparedStatement pstmt = conn.prepareStatement(lcodesql);
			pstmt.setString(1, location);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				String rid = Integer.toString(rs.getInt("rid"));
				String date = rs.getString("rdate");
				String pickup = rs.getString("pickup");
				String dropoff = rs.getString("dropoff");
				String amt = Integer.toString(rs.getInt("amount"));
				count.add(rid + "\t" + date + "\t" + pickup + "\t" + dropoff + "\t" + amt + "\n");
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
					count.add(rid + "\t" + date + "\t" + pickup + "\t" + dropoff + "\t" + amt + "\n");
				}
			}
		int s = count.size();
		String List[] = new String[s];
		for (int j = 0; j < count.size(); j ++) {
			List[j] = count.get(j);
		}
		if (s >= 5) {
			printList(List, List.length);
		}else {
			String all[][] = new String[s%5][];
			for (int j = 0; j < s%5; j++) {
				all[j] = UpdateList(List, j);
			}
			printList(all[0], 5);
			int k = 1;
			System.out.println("Please input 'n' for next page or anything else to continue.");
			String input = scanner.next().toLowerCase();
			while (input.equals("n") && k < s%5) {
				printList(all[k], all[k].length);
				System.out.println("Please type n for next page or anything else to continue.");
				input = scanner.next().toLowerCase();
				k++;
			}
		}
	}
	
	private String[] UpdateList(String list[], int mod) {
		String newString[] = new String[5];
		for (int i = mod; i < mod + 5; i ++) {
			newString[i] = list[i];
		}
		return newString;
	}
	
	private void printList(String list[], int l) {
		for (int i = 0; i < l; i++) {
			System.out.println(list[i]);
		}
	}
	
	private void delete(String usr, Scanner scanner, Connection conn) throws SQLException {
		
		int ridList[] = listAll(usr);
		System.out.println("Select one ID of request you wish to delete.");
		int id = scanner.nextInt();
		for (int i = 0; i < ridList.length; i++) {
			if (id == ridList[i]) {
				DeleteRow(id, scanner, conn);
			}
		}
		System.out.println("Invalid rid, please try again.");
		delete(usr, scanner, conn);

	}
	
	private int[] listAll(String usr){
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
		int ridList[] = new int[count.size()];
		for (int i = 0; i < count.size(); i++) {
			ridList[i] = count.get(i);
		}
		return ridList;
	}
	
	private void DeleteRow(int id, Scanner scanner, Connection conn) throws SQLException {
		
		String sql = "delete from requests where rid = ? and email = ?";
		
		try(PreparedStatement pstmt = conn.prepareStatement(sql)){
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
	
}
