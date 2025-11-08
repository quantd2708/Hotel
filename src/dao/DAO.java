package dao;
import java.sql.Connection;
import java.sql.DriverManager;

public class DAO {
    public static Connection con;
    
    public DAO(){
        if(con == null){
            String dbUrl = 
                    "jdbc:mysql://localhost:3306/hotel?autoReconnect=true&useSSL=false"; // Sửa port 3307 thành 3306 (default)
            String dbClass = "com.mysql.cj.jdbc.Driver"; // Sửa driver Class

            try {
                Class.forName(dbClass);
                con = DriverManager.getConnection (dbUrl, "root", "0967365261"); // Sửa password
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
