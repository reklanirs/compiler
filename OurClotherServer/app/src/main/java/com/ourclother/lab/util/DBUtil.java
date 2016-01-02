package com.ourclother.lab.util;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
/**
 * Created by Administrator on 2015/2/11.
 */
public class DBUtil {
    private static Connection conn;
    private static PreparedStatement pstmt;

    private static String driverClass = "";
    private static String driverUrl = "";
    private static String username = "";
    private static String password = "";

    /**
     *获得MySql数据库连接
     * @return Connection conn
     */
    public static Connection getConnForMySql() {
    new DBUtil().init();

    try {
        Class.forName(driverClass);
        conn = DriverManager.getConnection(driverUrl, username, password);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return conn;
}

    private void init() {
        Properties pro = new Properties();
        try {
            pro.load(this.getClass().getClassLoader().getResourceAsStream("paramsConfig.properties"));
            driverClass = pro.getProperty("driverClass");
            driverUrl = pro.getProperty("dbUrl");
            username = pro.getProperty("username");
            password = pro.getProperty("password");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获得当前连接的预准备的Statement
     * 其中SQL不带参数[问号]
     * @param conn
     * @param sql
     * @return PreparedStatement pstmt
     */
    public static PreparedStatement getPreparedStatemnt(Connection conn, String sql) {
        try {
            pstmt = conn.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pstmt;
    }

    /**
     * 获得当前连接的预处理的Statement
     * 其中 SQL 带条件的查询 ，问号的参数按顺序一次存入 数组 params
     * @param conn
     * @param sql
     * @param params
     * @return PreparedStatement pstmt
     */
    public static PreparedStatement getPreparedStatemnt(Connection conn , String sql, String params[]) {
        try {
            pstmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++){
                pstmt.setString(i + 1, params[i]);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pstmt;
    }

    /**
     * 获得当前连接的预处理的Statement
     * 其中 SQL 带条件的查询 ，问号的参数按顺序一次存入 数组 params
     * @param conn
     * @param sql
     * @param params
     * @return PreparedStatement pstmt
     */
    public static PreparedStatement getPreparedStatemnt(Connection conn , String sql,
                                                        Object params[]) {
        try {
            pstmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++){
                pstmt.setObject(i + 1, params[i]);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pstmt;
    }

    /**
     * 重载方法 关闭 连接
     * @param conn
     */
    public static void CloseResources(Connection conn) {
        try {
            if (conn != null && !conn.isClosed())
                conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重载方法 关闭 称述对象
     * @param stmt
     */
    public static void CloseResources(Statement stmt) {
        try {
            if (stmt != null)
                stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重载方法 关闭 结果集
     * @param rs
     */
    public static void CloseResources(ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重载方法 关闭 结果集,称述对象
     * @param rs
     */
    public static void CloseResources(ResultSet rs , Statement stmt) {
        CloseResources(rs);
        CloseResources(stmt);
    }

    /**
     * 重载方法，关闭 连接和陈述对象
     * @param conn
     * @param stmt
     */
    public static void CloseResources(Connection conn, Statement stmt) {
        CloseResources(stmt);
        CloseResources(conn);
    }

    /**
     * 重载方法，关闭 连接和结果集
     * @param conn
     * @param rs
     */
    public static void CloseResources(Connection conn, ResultSet rs) {
        CloseResources(rs);
        CloseResources(conn);
    }

    /**
     * 重载方法，关闭 连接、陈述对象和结果集
     * @param conn
     * @param stmt
     * @param rs
     */
    public static void CloseResources(Connection conn, Statement stmt,
                                      ResultSet rs) {
        CloseResources(rs);
        CloseResources(conn ,stmt);
    }
}
