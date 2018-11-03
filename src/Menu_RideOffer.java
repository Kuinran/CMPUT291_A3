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


public class Menu_RideOffer {
//lots of the code is similar to post ride request so credits to jarrett >.<
	private String usr;
	
	public Menu_RideOffer(String usr, Scanner scanner, Connection conn) {
		this.usr = usr;
		RideOffer(scanner,usr,conn);
	}
	private void RideOffer(Scanner scanner, String usr, Connection conn){
		System.out.println("date (YYYY-MM-DD)\n");
		String date = scanner.next();
		System.out.println("number of seats\n");
		String s = scanner.next();
		int numseats = Integer.parseInt(s);
		System.out.println("price\n");
		String p = scanner.next();
		int price = Integer.parseInt(p);
		System.out.println("Luggage Description\n");  //eg: small bag
		String luggage = scanner.next();
		System.out.println("src location code\n");
		String pickup = scanner.next();
		validate_location(scanner,usr,conn,pickup);
		System.out.println("dst location code\n");
		String dropoff = scanner.next();
		
	}
public void validate_location(Scanner scanner, String usr, Connection conn, String location) {
	//first chunk of code here checks if the lcode is a valid lcode
	String checklcode = String.format("select lcode from locations where lcode = '%s';", location);
	//These string are just for testing. Remove later
	//String s1 = "insert into locations values ('ab4','Calgary','Alberta','111 Edmonton Tr');";
	//String s2 = "insert into locations values ('ab5','Calgary','Alberta','Airport');";
	//String s3 = "insert into locations values ('ab6','Red Deer','Alberta','City Hall');";
	
	try{
		Statement stmt = conn.createStatement();
		//ResultSet rs1 = stmt.executeQuery(checklcode);
	}
	catch(SQLException e) {
		System.out.println(e.getMessage());
	}
}


}
