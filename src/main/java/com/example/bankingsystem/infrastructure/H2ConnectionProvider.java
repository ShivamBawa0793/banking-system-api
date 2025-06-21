package com.example.bankingsystem.infrastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class H2ConnectionProvider {

    //Database connection parameters
    private String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:mem:bankingsystem;DB_CLOSE_DELAY=-1";
    private static  String USER = "shivam";
    private static String PASS = "";

    private Connection connection;

    public H2ConnectionProvider(){
        //Connect to DB using the DB Connection parameters
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to database....");
            this.connection = DriverManager.getConnection(DB_URL,USER,PASS);
            System.out.println("Connection to H2 database established.");

        //Create Table if didn't exist
            createTable();

        } catch (SQLException se) {
            System.err.println("SQL Exception during H2 connection or table creation:");
            se.printStackTrace();
            throw new RuntimeException("Failed to establish database connection or create tables.", se);

        } catch (ClassNotFoundException e) {
            System.err.println("H2 JDBC Driver not found:");
            e.printStackTrace();
            throw new RuntimeException("H2 JDBC Driver not found.", e);

        }
    }

    private void createTable() throws SQLException{
        try(Statement stmt = connection.createStatement()){
            String sql = "CREATE TABLE IF NOT EXISTS ACCOUNTS " +
                    "(id VARCHAR(255) not NULL, " +
                    " creation_timestamp BIGINT, " +
                    " balance INT DEFAULT 0, " +
                    " total_outgoing INT DEFAULT 0, " +
                    " PRIMARY KEY ( id ))";
            stmt.execute(sql);
            System.out.println("ACCOUNTS table created or already exists.");
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection(){
        if(connection !=null){
            try {
                connection.close();
                System.out.println("H2 database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing H2 database connection:");
                e.printStackTrace();
            }

        }
    }
}
