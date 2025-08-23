package com.kingironman.sethome.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;

public class SQLiteUtils {
    private static final String DB_PATH = "plugins/SetHome/homes.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    private Connection connection;
    public Connection getConnection() {
        return connection;
    }

    public void connect() throws SQLException {
        // Ensure plugins/SetHome directory exists
        File dir = new File("plugins/SetHome");
        if (!dir.exists()) dir.mkdirs();
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