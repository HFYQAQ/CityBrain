package cn.edu.neu.citybrain.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static int requestCnt;
    public static int requestFailedCnt;

    static {
        try {
            Class.forName(DBConstants.MYSQL_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DBConstants.JDBC_URL, DBConstants.JDBC_USER, DBConstants.JDBC_PWD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        requestCnt++;
        if (conn == null) {
            requestFailedCnt++;
        }
        return conn;
    }
}
