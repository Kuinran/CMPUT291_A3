import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
import java.sql.ResultSet;
//probably need to import more shit >.<


public class Menu_PostRq{
	private String usr;

		Menu_PostRq(String usr, Scanner scanner, Connection conn) {
			this.usr = usr;
			post_rq(scanner, conn);
		}
		
		private void post_rq (Scanner scanner, Connection conn){
			//ask for user inputs
			System.out.println("date (YYYY-MM-DD)\n");
			String date = scanner.next();
			System.out.println("pickup location code\n");
			String pickup = scanner.next();
			System.out.println("dropoff location code\n");
			String dropoff = scanner.next();
			System.out.println("price\n");
			String p = scanner.next();
			int price = Integer.parseInt(p);
			
			Insert(date, pickup, dropoff, price, conn);//insert function
		}
		
		private void Insert (String date, String pickup, String dropoff, int price, Connection conn)	{
			//TODO check date format, and change pickup and dropoff to location codes			
			
			String sql = "INSERT INTO requests(rid, email, rdate, pickup, dropoff, amount) VALUES(?,?,?,?,?,?)";//init query
			int rid = GenRID(conn);//generate new rid
			try (PreparedStatement pstmt = conn.prepareStatement(sql)){//init prepared statement; prevents injection
				pstmt.setInt(1, rid);//set rid
				pstmt.setString(2, usr);//set user email
				pstmt.setDate(3, java.sql.Date.valueOf(date));//set date
				pstmt.setString(4, pickup);//set pickup lcode
				pstmt.setString(5, dropoff);//set dropoff lcode
				pstmt.setInt(6, price);// set price
				pstmt.executeUpdate();//executes query; inserts to table
			}catch (SQLException e){
				System.out.println(e.getMessage());
			}	
		}
		private int GenRID(Connection conn){//generate new rid
			List<Integer> rids = new ArrayList<>();
			String sql = "SELECT rid FROM requests";//query to get all prev rid
			
			try (	Statement stmt  = conn.createStatement();
					ResultSet rs    = stmt.executeQuery(sql)){
				if(rs.isBeforeFirst()) {
					while(rs.next()) {
						rids.add(rs.getInt("rid"));
					}
				} else {
					return 0;
				}
			}catch (SQLException e) {
				System.out.println(e.getMessage());
			}
			int i = rids.get(rids.size()-1);//get last rid
			return i+1;//new rid is old rid + 1
		}
}
