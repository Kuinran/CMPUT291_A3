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
		System.out.println("Enter date (YYYY-MM-DD):\n");
		String rdate = scanner.next();
		System.out.println("Enter number of seats:\n");
		String s = scanner.next();
		int seats = Integer.parseInt(s);
		System.out.println("Enter price:\n");
		String p = scanner.next();
		int price = Integer.parseInt(p);
		System.out.println("Enter Luggage Description:\n");  //eg: small bag
		String lugDesc = scanner.next();
		String src;
		while(true) {
		System.out.println("Enter src location code:\n");
		String uplocation = scanner.next();
		src = validate_location(scanner,usr,conn,uplocation);
		System.out.println(src);
		if(src != "failed") {
			break;
		}
		System.out.println("invalid input try again");
		}
		String dst;
		while(true) {
		System.out.println("Enter dst location code:\n");
		String offlocation = scanner.next();
		dst = validate_location(scanner,usr,conn,offlocation);
		System.out.println(dst);
		if(dst !="failed") {
			break;
		}
			System.out.println("invalid input try again");
		}
		
		int rno = GenRNO(conn);
		System.out.println("The rno is: " + rno);
		enrouteprocess(usr,scanner,conn,rno);
		System.out.println("Do you wish to enter you car number?\n1- yes\n0- no");
		String a = scanner.next();
		int respons = Integer.parseInt(a);
		int cno = 0;
		if(respons==1) {
			System.out.println("usr: " + usr + "\nenter your cno: ");
			while(true) {
			String val = scanner.next();
			int test = Integer.parseInt(val);
			cno = validate_cno(scanner,usr,conn,test);
			if (cno!=-1) {
				break;
				}
			}	
		}
		
		try {
			//rides(rno, price, rdate, seats, lugDesc, src, dst, driver, cno)
			String finalsql = "insert into rides values (?,?,?,?,?,?,?,?,?)";
			PreparedStatement pstate = conn.prepareStatement(finalsql);
			if(respons == 1) {
			pstate.setInt(1,rno);
			pstate.setInt(2, price);
			pstate.setString(3,rdate);
			pstate.setInt(4, seats);
			pstate.setString(5, lugDesc);
			pstate.setString(6, src);
			pstate.setString(7, dst);
			pstate.setString(8,usr);
			pstate.setInt(9, cno);
			pstate.executeUpdate();
			}
			else {
				pstate.setInt(1,rno);
				pstate.setInt(2, price);
				pstate.setString(3,rdate);
				pstate.setInt(4, seats);
				pstate.setString(5, lugDesc);
				pstate.setString(6, src);
				pstate.setString(7, dst);
				pstate.setString(8,usr);
				pstate.setNull(9, '\0');
				pstate.executeUpdate();	
			}
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		}
	
	//TODO: add a case where user enters a code that is not a lcode key or a key substring found in city, prov, address(rare case)
	public String validate_location(Scanner scanner, String usr, Connection conn, String location) {
		List<String> lcodelist = new ArrayList<>();
	String checklcode = "select lcode from locations where lcode = ?";
	String finds = String.format("select * from locations where (city LIKE '%%%s%%') OR (prov LIKE '%%%s%%') OR (address LIKE '%%%s%%')",
			location, location, location);

	//These string are just for testing. Remove later
	//String s1 = "insert into locations values ('ab4','Calgary','Alberta','111 Edmonton Tr');";
	//String s2 = "insert into locations values ('ab5','Calgary','Alberta','Airport');";
	//String s3 = "insert into locations values ('ab6','Red Deer','Alberta','City Hall');";
	
	try{
		PreparedStatement st1 = conn.prepareStatement(checklcode);
		st1.setString(1,location);
		Statement stmt = conn.createStatement();
		ResultSet rs1 = st1.executeQuery();
		if(rs1.next()){
			System.out.println("Correct code");
			return location; //this means that indeed a correct lcode was inputted
		}
		System.out.println("incorrect code. Did you meen to select from");
		ResultSet rs2 = stmt.executeQuery(finds);
		int counter = 1;
		while(rs2.next()) {
		System.out.println(counter + "- " + rs2.getString(1) + " " + rs2.getString(2) + " " + rs2.getString(3) + " " + rs2.getString(4));
		lcodelist.add(rs2.getString(1));
		if (counter % 5 == 0) {
			System.out.println("press number to select a location, or press 0 to see more");
			String a = scanner.next();
			int answer = Integer.parseInt(a);
			if(answer == 0) {
				continue;
			}
			else {
				return lcodelist.get(answer-1);
			}
		}
		counter++;
		}
		System.out.println("press number to select a location");
		String a = scanner.next();
		int answer = Integer.parseInt(a);
		String f = lcodelist.get(answer-1);
		return f;
	}
	catch(SQLException e) {
		System.out.println(e.getMessage());
	}
	return "failed"; //this is returned if user entered neither a valid substring or a valid lcode
	}
	
	public int validate_cno(Scanner scanner, String usr, Connection conn, int cno) {
		
		String sql = "select cno from cars, members where ? = cars.owner and cars.cno = ?";
		try{
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, usr);
			stmt.setInt(2, cno);
			ResultSet rs1 = stmt.executeQuery();
			if(rs1.next()) {
				System.out.println("cno found");
				return cno;
			}
			else {
				System.out.println("Cno not found under user name. Please enter a valid cno:");
				return -1;
			}
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
		}
		return 1;
		}
	
	public int GenRNO(Connection conn){
		List<Integer> rno = new ArrayList<>();
		String sql = "SELECT rno FROM rides";
		
		try {
				Statement stmt  = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()) {
				while(rs.next()) {
					rno.add(rs.getInt("rno"));
				}
			}else {
				return 0;
			}
		}catch (SQLException e) {
			System.out.println(e.getMessage());
		}		
		int e = rno.get(rno.size()-1);
		return e+1;
	}
	public void enrouteprocess(String usr, Scanner scanner, Connection conn,int rno) {
		while(true) {
			System.out.println("Do you wish to enter a enroute location?\n1- yes\n0- no");
			String a = scanner.next();
			int respons = Integer.parseInt(a);
			if(respons == 0) {
				break;
			}
			else {
				System.out.println("Enter lcode of enroute location:");
				String en = scanner.next();
				String enroute = validate_location(scanner,usr,conn,en);
				System.out.println("inserting enroute values");
				String sql = "insert into enroute values (?,?)";
				try {
				PreparedStatement pst = conn.prepareStatement(sql);
				pst.setInt(1, rno);
				pst.setString(2, enroute);
	
				pst.executeUpdate();
				System.out.print("Success!");
				}
				catch(SQLException e) {
					System.out.println(e.getMessage());
				}
			
				}
		}
	}
}
