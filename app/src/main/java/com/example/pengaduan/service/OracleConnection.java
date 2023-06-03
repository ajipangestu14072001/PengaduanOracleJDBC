package com.example.pengaduan.service;

import com.example.pengaduan.utils.Constant;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleConnection {
    public static Connection getConnection() throws SQLException {
        String jdbcURL = Constant.BASE_URL_JDBC;
        String username = Constant.USERNAME;
        String password = Constant.PASSWORD;

        Connection connection = DriverManager.getConnection(jdbcURL, username, password);
        return connection;
    }
}
