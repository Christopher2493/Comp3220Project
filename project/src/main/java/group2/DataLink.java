package group2;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataLink {
    
    String url = "jdbc:sqlite:./Data"; //'Data' file at source.
    

    public void CreateTable(){ //only for use in creating tables for the GUI, will not be accessible by the GUI

        try (Connection conn = DriverManager.getConnection(url)) {
            

String doctors = "CREATE TABLE IF NOT EXISTS doctors (" //we make tables here and then execute them
                + "	DocId INTEGER PRIMARY KEY,"
                + "	DocName VARCHAR(255) NOT NULL"
                + ");";
var stmt = conn.createStatement();
stmt.execute(doctors);
System.out.println("CreateTable Executed.");

            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }



    public void DeleteTable(){ //only for use in wiping the tables, will not be accessible by the GUI


        try (Connection conn = DriverManager.getConnection(url)) {
            

String delete = "DROP TABLE doctors;"; // our deletions go here
var stmt = conn.createStatement();
stmt.execute(delete);
System.out.println("DeleteTable Executed.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }



public void ShowTables(){

    try (Connection conn = DriverManager.getConnection(url)) {


String ShowTables = "SELECT * from doctors";
var stmt = conn.createStatement();
stmt.execute(ShowTables);
System.out.println("ShowTables Executed.");
    } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
}



/*  
  // uncomment if you wish to run this file to do table things
    public static void main(String[] args) {
        DataLink Run = new DataLink();
        Run.CreateTable();
        Run.ShowTables();
        Run.DeleteTable();
        
    }*/


}