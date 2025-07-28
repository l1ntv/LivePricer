package ru.rsreu.lint.livepricer.dao;

import ru.rsreu.lint.livepricer.ResourceLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    static {
        try {
            Class.forName(ResourceLoader.getProperty("db.driver"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("JDBC драйвер не найден!", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(
                ResourceLoader.getProperty("db.url"),
                ResourceLoader.getProperty("db.user"),
                ResourceLoader.getProperty("db.password"));
        conn.setAutoCommit(true);
        return conn;
    }
}
