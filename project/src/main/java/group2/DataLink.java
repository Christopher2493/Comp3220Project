package group2;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataLink {
    
    String url = "jdbc:sqlite:./Data"; //'Data' file at source.

    public void CreateTable(){ //only for use in creating tables, will not be accessible by the GUI

        try (Connection conn = DriverManager.getConnection(url)) {

String doctors = "CREATE TABLE IF NOT EXISTS doctors (" //we make tables here and then execute them
                + "	DocId INTEGER PRIMARY KEY UNIQUE,"
                + "	DocName VARCHAR(255) NOT NULL"
                + ");";
String schedules = "CREATE TABLE IF NOT EXISTS schedules ("
                + "	DocId INTEGER PRIMARY KEY,"
                + "	MondayTime VARCHAR(255),"
                + "	TuesdayTime VARCHAR(255),"
                + "	WednesdayTime VARCHAR(255),"
                + "	ThursdayTime VARCHAR(255),"
                + "	FridayTime VARCHAR(255),"
                + "	SaturdayTime VARCHAR(255),"
                + "	SundayTime VARCHAR(255),"
                + " FOREIGN KEY (DocId)"
                + " REFERENCES doctors (DocId)"
                + " ON DELETE CASCADE"
                + " ON UPDATE NO ACTION"
                + ");";
String patients = "CREATE TABLE IF NOT EXISTS patients ("
                + "	PatientId INTEGER PRIMARY KEY UNIQUE,"
                + "	PatientName VARCHAR(255) NOT NULL"
                + ");";
String appointments = "CREATE TABLE IF NOT EXISTS appointments ("
                + "	PatientId INTEGER PRIMARY KEY,"
                + "	DocId INTEGER NOT NULL,"
                + "	Date VARCHAR(255) NOT NULL,"
                + "	Time VARCHAR(255) NOT NULL,"
                + " FOREIGN KEY (DocId)"
                + " REFERENCES doctors (DocId)"
                + " ON DELETE CASCADE"
                + " ON UPDATE NO ACTION"
                + " FOREIGN KEY (PatientId)"
                + " REFERENCES patients (PatientId)"
                + " ON DELETE CASCADE"
                + " ON UPDATE NO ACTION"
                + ");";


var stmt = conn.createStatement();
stmt.execute(doctors);
stmt.execute(schedules);
stmt.execute(patients);
stmt.execute(appointments);
System.out.println("CreateTable Executed.");

            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }



    public void DeleteTable(){ //only for use in wiping the tables, will not be accessible by the GUI

        try (Connection conn = DriverManager.getConnection(url)) {
            
 // our deletions go here
var stmt = conn.createStatement();
stmt.execute("DROP TABLE doctors;");
stmt.execute("DROP TABLE schedules;");
stmt.execute("DROP TABLE patients;");
stmt.execute("DROP TABLE appointments;");
System.out.println("DeleteTable Executed.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
public void ShowTables(){ //only for use in showing all tables, will not be accessible by the GUI

        var sql = "SELECT * FROM doctors";
        System.out.println("\ndoctors Table:");
        try (var conn = DriverManager.getConnection(url);
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf("%-10s%-20s\n",
                        rs.getInt("DocID"),
                        rs.getString("DocName")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        sql = "SELECT * FROM schedules";
        System.out.println("\nschedules Table:");
        try (var conn = DriverManager.getConnection(url);
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery(sql)) {
                
            while (rs.next()) {
                System.out.printf("%-10s%-20s%-20s%-20s%-20s%-20s%-20s%-20s\n",
                        rs.getInt("DocID"),
                        rs.getString("MondayTime"),
                        rs.getString("TuesdayTime"),
                        rs.getString("WednesdayTime"),
                        rs.getString("ThursdayTime"),
                        rs.getString("FridayTime"),
                        rs.getString("SaturdayTime"),
                        rs.getString("SundayTime")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
          sql = "SELECT * FROM patients";
        System.out.println("\npatients Table:");
        try (var conn = DriverManager.getConnection(url);
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf("%-10s%-20s\n",
                        rs.getInt("PatientID"),
                        rs.getString("PatientName")
                        
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
         sql = "SELECT * FROM appointments";
        System.out.println("\nappointments Table:");
        try (var conn = DriverManager.getConnection(url);
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf("%-10s%-10s%-20s%-20s\n",
                        rs.getInt("DocID"),
                        rs.getInt("PatientID"),
                        rs.getString("Date"),
                        rs.getString("Time")
                    
                );
            }System.out.println("ShowTables Executed.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
}



public void InsertData(String Database,String Values[]){ //Call with Database name and Values to insert, does one insert per call.

        try (Connection conn = DriverManager.getConnection(url)) {
            String Command = null;
            int Count=0;
            if(Database.equals("doctors")){ //doctor insert script template.
                //String sql = "INSERT INTO warehouses(name,capacity) VALUES(?,?)"; Example from Sqlite
                Command = "INSERT INTO doctors(DocID,DocName) VALUES(";
                Count = 2; // number of vars, DocID + DocName = 2
            }else if (Database.equals("schedules")){
                
                Command = "INSERT INTO schedules(DocID,MondayTime,TuesdayTime,WednesdayTime,ThursdayTime,FridayTime,SaturdayTime,SundayTime) VALUES(";
                Count = 8; // number of vars, DocID + 7 days = 8
            }else if (Database.equals("patients")){
                
                Command = "INSERT INTO patients(PatientID,PatientName) VALUES(";
                Count = 2; // number of vars, Id + name = 2
            }else if (Database.equals("appointments")){
                
                Command = "INSERT INTO appointments(PatientID,DocID,Date,Time) VALUES(";
                Count = 4; // number of vars, Id + id + date + time = 4
            }
            if(Command!=null && Count!=0){
                for(int i = 0; i<Count; i++)
                {
                    Command = Command + Values[i];
                    if(i+1!=Count){
                        Command = Command + ",";
                    }else{Command=Command + ")";}

                }
                System.out.println(Command); // prints out the insert, debug.
            var stmt = conn.createStatement();
                stmt.execute(Command);
            }
            
    } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

}



public Boolean SelectFind(String Database, String Argument){ //returns a true or false regarding whether or not the argument is found

//String Command = "SELECT DocID FROM doctors WHERE DocID = 171"; example
String Command = "SELECT * FROM "+Database+" WHERE " + Argument +";";
Boolean Value = true;
String Results = "";
        try (var conn = DriverManager.getConnection(url);
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery(Command)) {

            while (rs.next()) {
                    
            Results = Results + (rs.getInt(1));
            

            }System.out.println("SelectFind Executed.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
if (Results.equals("")){
Value = false;
}
return Value;
}


public String GetData(String Database,String Selection[], String Argument){ //returns the found data formatted as a string with variable names and results one row per line
    String Variables = String.join(",", Selection);
String Command = "SELECT "+Variables+ " FROM "+Database+" WHERE " + Argument +";";
String Results = "";

        try (var conn = DriverManager.getConnection(url);
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery(Command)) {

            while (rs.next()) {
                for(int i = 0; i<Selection.length; i++){
                    Results = Results + Selection[i]+": "+ (rs.getString(i+1))+" ";
                }
                Results=Results+"\n";
                

            }System.out.println("GetData Executed.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Results;
}

public String GetInfo(String Database,String Selection[], String Argument){ //returns the found info with limited formatting
    String Variables = String.join(",", Selection);
String Command = "SELECT "+Variables+ " FROM "+Database+" WHERE " + Argument +";";
String Results = "";

        try (var conn = DriverManager.getConnection(url);
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery(Command)) {

            while (rs.next()) {
                for(int i = 0; i<Selection.length; i++){
                    Results = Results + (rs.getString(i+1))+" ";

                }
                
            }System.out.println("GetInfo Executed.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Results;
}
public void DeleteData(String Database, String Argument){ //deletes the matching values from the corresponding table

//String Command = "DELETE FROM doctors WHERE DocID = 171"; example

    try (Connection conn = DriverManager.getConnection(url)) {

        String Command = "DELETE FROM "+Database+" WHERE " + Argument +";"; 

var stmt = conn.createStatement();
stmt.execute(Command);
System.out.println("DeleteData Executed.");

            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

}

public void ModifyData(String Database, String SetArgument,String WhereArgument){ //modifies the matching values from the corresponding table

//String Command = "UPDATE doctors SET DocID = 121 WHERE DocID = 171"; example

    try (Connection conn = DriverManager.getConnection(url)) {

        String Command = "Update "+Database+" SET " + SetArgument + " WHERE " + WhereArgument + ";";

var stmt = conn.createStatement();
stmt.execute(Command);
System.out.println("ModifyData Executed.");

            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

}


  // uncomment if you wish to run this file to do debug/developer things such as table creation/deletion/showing, ensure commented if pushing to git.
    public static void main(String[] args) {
        DataLink Run = new DataLink();
        Run.CreateTable();
        String[] Values = {"171", " 'AAAAAAA' "};
        Run.InsertData("doctors",Values);
        String[] Values2 = {"172", " 'BBBBBBB' "};
        Run.InsertData("doctors",Values2);
        String[] ValuesP = {"1082", " '1111111' "};
        Run.InsertData("patients",ValuesP);
        String[] ValuesS = {"'171'","'5-9'" ,"'5-9'" ,"'5-9'" ,"'5-9'" ,"'5-9'" ," 'Away' " ," 'Away' "};
        Run.InsertData("schedules",ValuesS);
        String[] ValuesA = {"1082","171" ," 'November 4th' " ," '17:30' "};
        Run.InsertData("appointments",ValuesA);
        System.out.println("Find DocID 173 query result: "+ Run.SelectFind("doctors","DocID = 173"));
        System.out.println("Find DocID 172 query result: "+ Run.SelectFind("doctors","DocID = 172"));
        String[] Values3 = {"DocID","DocName"};
        
        String Results = Run.GetData("doctors",Values3,"DocID < 200");
        System.out.println(Results);
        
        Results = Run.GetInfo("doctors",Values3,"DocID < 200");
        System.out.println(Results);


        Run.ShowTables();
        Run.DeleteData("doctors","DocID = 172");
        Run.ModifyData("doctors","DocID = 121","DocID = 171");
        Run.ShowTables();
        Run.DeleteTable();
        
    }


}