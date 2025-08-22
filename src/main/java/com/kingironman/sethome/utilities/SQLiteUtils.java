package com.kingironman.sethome.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteUtils {
    private static final String DB_URL = "jdbc:sqlite:SetHomePlugin.db";
    private Connection connection;

    public void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
    }

    public void setupTables() throws SQLException {
        connect();
        String sql = "CREATE TABLE IF NOT EXISTS homes ("
                + "uuid TEXT NOT NULL,"
                + "home_name TEXT NOT NULL,"
                + "world TEXT NOT NULL,"
                + "x REAL, y REAL, z REAL, yaw REAL, pitch REAL,"
                + "PRIMARY KEY (uuid, home_name)"
                + ");";
        Statement stmt = connection.createStatement();
        stmt.execute(sql);
        stmt.close();
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}