import java.sql.*;

public class JdbcEmployee {
	public static void main(String[] args) throws Exception {
		var out = System.out;

		out.println("Hello ...");

		// Driver Register
		Class.forName("oracle.jdbc.driver.OracleDriver");

		// Create a database connection by using user name and password
		String url = "jdbc:oracle:thin:@localhost:1521:orcl";
		Connection conn = DriverManager.getConnection(url, "john", "a");

		Statement stmt = conn.createStatement();

		// drop table using statement
		int upNo = -1;

		try {
			upNo = stmt.executeUpdate("DROP TABLE EMPLOYEE");
		} catch (Exception e) {
			upNo = -1;
		}

		if (upNo == 0) {
			out.println("\nEmployee tabled is dropped.");
		}

		// creatte table using statement
		String sql = """
					CREATE TABLE employee (
					  id NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,
					  first_name VARCHAR2(200) NOT NULL ,
					  last_name VARCHAR2(200) ,
					  email VARCHAR2(200) NOT NULL ,
					  age INT ,
					  phone_no VARCHAR2(200) ,
					  up_dt DATE DEFAULT sysdate NOT NULL
					)
				""";

		upNo = stmt.executeUpdate(sql);

		if (upNo == 0) {
			out.println("Employee tabled is created.");
		}

		// insert a record using statement
		sql = "INSERT INTO employee(first_name, email, age) VALUES( 'john', 'john@google.com', 18 )";
		upNo = stmt.executeUpdate(sql);

		if (upNo >= 1) {
			out.println("Employee record is inserted.");
		} else {
			out.println("Employee tabled is not inserted.");
		}

		// insert record using prepared statement
		sql = "INSERT INTO employee(first_name, email, age) VALUES( ?, ?, ? )";

		PreparedStatement pst = conn.prepareStatement(sql);

		Object[][] records = { { "brown", "brown@gmail.com", 20 }, { "jane", "jane@gmail.com", 22 } };

		for (var record : records) {
			var idx = 1; // index starts from one.
			for (var c : record) {
				if (c instanceof String) {
					pst.setString(idx++, "" + c);
				} else if (c instanceof Integer) {
					pst.setInt(idx, (int) c);
				}
			}
			upNo = pst.executeUpdate();

			if (upNo >= 1) {
				out.println("Employee record is inserted. ");
			} else {
				out.println("Employee tabled is not inserted.");
			}
		}

		// query using prepared statement
		sql = "SELECT first_name, email, age FROM employee where 2 = ? ";
		pst = conn.prepareStatement(sql);

		var idx = 1; // index 1
		pst.setInt(idx++, 2);

		ResultSet rs = pst.executeQuery();

		out.println("\nQuery Result ...");

		while (rs.next()) {
			idx = 1; // index 1

			String firstName = rs.getString(idx++);
			String email = rs.getString(idx++);
			Integer age = rs.getInt("age");

			out.println(String.format("first_name = %s, email = %s, age = %s", firstName, email, age));
		}

		// clear resources
		rs.close();
		pst.close();
		stmt.close();
		conn.close();

		out.println("\nGood bye!");
	}
}