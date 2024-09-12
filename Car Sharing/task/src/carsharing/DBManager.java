package carsharing;

import java.sql.*;

public class DBManager {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";
    static String DB_URL = "jdbc:h2:./src/carsharing/db/{database}";

    //  Database credentials
    static final String USER = "sa";
    static final String PASS = "";

    private Connection connection;

    public DBManager(String databaseFileName) throws SQLException, ClassNotFoundException {
        DB_URL = DB_URL.replace("{database}", databaseFileName);
        Class.forName(JDBC_DRIVER);
        this.connection = DriverManager.getConnection(DB_URL);
        this.connection.setAutoCommit(true);
    }

    public void executeSql(String sql) throws SQLException {
        Statement stmt = this.connection.createStatement();
        stmt.execute(sql);
    }

    public ResultSet getResultSet(String sql) throws SQLException {
        Statement stmt = this.connection.createStatement();
        return stmt.executeQuery(sql);
    }

    public void insert(String sql) throws SQLException {
        executeSql(sql);
    }

    public void executeUpdate(String sql) throws SQLException {
        Statement statement = this.connection.createStatement();
        statement.executeUpdate(sql);
    }

    public void createTable(String sql){
        try {
            executeSql("DROP TABLE IF EXISTS COMPANY");
            executeUpdate(sql);
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    // Add similar methods for read, update and delete.
    // Make sure to handle exceptions accordingly in real practice.
}
