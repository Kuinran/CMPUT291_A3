import java.util.Scanner;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.io.Console;

public class Menu_Main {
	private String usr;
	
	Menu_Main (String usr, Scanner scanner, Connection conn) {
		this.usr = usr;
		JDBC_Connection.getMsg(usr, conn);
	}
}
