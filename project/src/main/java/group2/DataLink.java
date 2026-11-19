package group2;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataLink {
    
 public static void connect() {
        // connection string
        var url = "jdbc:sqlite:./Data";

        try (var conn = DriverManager.getConnection(url)) {
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    


}