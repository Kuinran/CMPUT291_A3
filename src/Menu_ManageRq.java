import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class Menu_ManageRq {
	private String usr;
	Menu_ManageRq(String usr, Scanner scanner, Connection conn) {
		this.usr = usr;
		mode(scanner, usr, conn);
	}
	
	private void mode (Scanner scanner, String usr, Connection conn) {
		System.out.println("Enter 'Search' or 'Delete' to continue");
		String input = scanner.next().toLowerCase();
		if (input.equals("search")) {
			search(scanner, conn);
		} else if (input.equals("delete")) {
			delete(usr, scanner, conn);
		} else {
			System.out.println("Invalid input");
			new Menu_ManageRq(usr, scanner, conn);
		}
	}
	
	private void search(Scanner scanner, Connection conn) {
		
		
	}
	
	private void delete(String usr, Scanner scanner, Connection conn) {
		listAll(usr);
		System.out.println("Select one ID of request you wish to delete.");
		int id = scanner.nextInt();
		
		String sql = "delete from requests where rid = ?";
		
		try(//Connection conn = JDBC_Connection.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setInt(1, id);
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
	
	private void listAll(String usr) {
		String sql = "select rid, rdate, pickup, dropoff, amount from requests where requests.email = ?";
		try (Connection conn = JDBC_Connection.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
				pstmt.setString(1, usr);
				ResultSet rs = pstmt.executeQuery();
				System.out.println("ID");
				while(rs.next()) {
					System.out.println(rs.getInt("rid") + "\t" +
									rs.getString("rdate") + "\t" +
									rs.getString("pickup") + "\t" +
									rs.getString("dropoff") + "\t" +
									rs.getInt("amount"));	
				}
			
		}  catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
}
