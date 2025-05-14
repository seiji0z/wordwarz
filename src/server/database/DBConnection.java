package server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    static Connection con;

    private DBConnection(){}

    public static void setCon() {
        try {
            String url = "jdbc:mysql://localhost:3306/wordwarz";
            String user = "root";
            String password = "root";
            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection(url, user, password);
            System.out.println("Database connection established!");
        } catch (SQLException e) {
            System.out.println("Error establishing database connection: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Error loading MySQL Connector/J driver: " + e.getMessage());
        }
    }

}