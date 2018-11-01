import java.sql.Connection;
import java.util.Scanner;
import java.sql.PreparedStatement;
import java.sql.SQLException;
//probably need to import more shit >.<
public class Menu_PostRq {
	private String usr;
		
		Menu_PostRq (String usr, Scanner scanner, Connection conn) {
			this.usr = usr;
			JDBC_Connection.getMsg(usr, conn);
		}
		
		private void post_rq (Scanner scanner) {
			System.out.println("date\n");
			String date = Helpers.safeString(scanner.next());
			System.out.println("pickup location\n");
			String pickup = Helpers.safeString(scanner.next());
			System.out.println("dropoff location\n");
			String dropoff = Helpers.safeString(scanner.next());
			System.out.println("price\n");
			int price = Helpers.safeString(scanner.nextInt());
			
			Insert(date, pickup, dropoff, price);
		}
		
		private void Insert (String date, String pickup, String dropoff, int price){
				//TODO get rid, email
				String sql = "INSERT INTO requests(rid, email, rdate, pickup, dropoff, amount) VALUES(?,?,?,?,?,?)";
				try (Connection conn = JDBC_Connection.connect();
						PreparedStatement pstmt = conn.prepareStatement(sql)){
					pstmt.setString(3, date);
					pstmt.setString(4, pickup);
					pstmt.setString(5, dropoff);
					pstmt.setInt(6, price);
					pstmt.executeUpdate();
				}catch (SQLException e) {
					System.out.println(e.getMessage());
				}
		}
}
