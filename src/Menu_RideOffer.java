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
		System.out.println("Enter src location code:\n");
		String uplocation = scanner.next();
		String src = validate_location(scanner,usr,conn,uplocation);
		System.out.println(src);
		System.out.println("Enter dst location code:\n");
		String offlocation = scanner.next();
		String dst = validate_location(scanner,usr,conn,offlocation);
		System.out.println(dst);
		int rno = GenRNO(conn);
		System.out.println("The rno is: " + rno);
		enrouteprocess(usr,scanner,conn,rno);
		System.out.println("Do you wish to enter you car number?\n1- yes\n0- no");
		String a = scanner.next();
		int respons = Integer.parseInt(a);
		int cno = 0;
		if(respons==1) {
			System.out.println("enter your cno:");
			while(true) {
			cno = Integer.parseInt(scanner.next());
			cno = validate_cno(scanner,usr,conn,cno);
			if (cno!=-1) {break;}
			}	
		}
		
		//String finalsql = String.format("insert into rides values(%d, %d, %s, %d, %s, %s, %s, %s, %s, %d)", 
				//rno,price,rdate,seats,lugDesc,src,dst,usr,cno);
		try {
			String finalsql = "insert into cars values (1,'Nissan','path',2006,4,'m@gmail.com')";
			PreparedStatement statement = conn.prepareStatement(finalsql);
		statement.executeUpdate();
			
			//Connection conn2 = JDBC_Connection.connect();
			//Statement statement = conn2.createStatement();
			//statement.executeUpdate("insert into cars values (1,'Nissan','path',2006,4,'m@gmail.com')");
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
	return "failed";
	}
	
	public int validate_cno(Scanner scanner, String usr, Connection conn, int cno) {
		String sql = String.format("select cno from cars, members where members.email = cars.owner and cars.cno = %d",cno);
		try{
			Statement stmt = conn.createStatement();
			ResultSet rs1 = stmt.executeQuery(sql);
			if(rs1.next()) {
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
