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
//probably need to import more shit >.<


public class Menu_PostRq{
	private String usr;
		Menu_PostRq(String usr, Scanner scanner, Connection conn) {
			this.usr = usr;
			post_rq(scanner, usr, conn);
		}
		
		private void post_rq (Scanner scanner, String usr, Connection conn){
			System.out.println("date (YYYY-MM-DD)\n");
			String date = scanner.next();
			System.out.println("pickup location code\n");
			String pickup = scanner.next();
			System.out.println("dropoff location code\n");
			String dropoff = scanner.next();
			System.out.println("price\n");
			String p = scanner.next();
			int price = Integer.parseInt(p);
			
			Insert(usr, date, pickup, dropoff, price);
			new Menu_Main(usr, scanner, conn);
		}
		
		private void Insert (String email, String date, String pickup, String dropoff, int price)	{
			//TODO check date format, and change pickup and dropoff to location codes			
			
			String sql = "INSERT INTO requests(rid, email, rdate, pickup, dropoff, amount) VALUES(?,?,?,?,?,?)";
			int rid = GenRID();
			try (Connection conn = JDBC_Connection.connect();
					PreparedStatement pstmt = conn.prepareStatement(sql)){
				pstmt.setInt(1, rid);
				pstmt.setString(2, email);
				DateFormat rdate = new SimpleDateFormat("yyyy-MM-dd");
				pstmt.setString(3, rdate.format(date));
				pstmt.setString(4, pickup);
				pstmt.setString(5, dropoff);
				pstmt.setInt(6, price);
				pstmt.executeUpdate();
			}catch (SQLException e){
				System.out.println(e.getMessage());
			}	
		}
		private int GenRID(){
			List<Integer> rid = new ArrayList<>();
			String sql = "SELECT rid FROM requests";
			
			try (Connection conn = JDBC_Connection.connect();
					Statement stmt  = conn.createStatement();
					ResultSet rs    = stmt.executeQuery(sql)){
				if(rs.next()) {
					while(rs.next()) {
						rid.add(rs.getInt("rid"));
					}
				}else {
					return 0;
				}
			}catch (SQLException e) {
				System.out.println(e.getMessage());
			}		
			int e = rid.get(rid.size()-1);
			return e+1;
		}
}
