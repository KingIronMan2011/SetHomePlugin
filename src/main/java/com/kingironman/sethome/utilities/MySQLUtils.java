package com.kingironman.sethome.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLUtils {
    private Connection connection;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    public Connection getConnection() {
        return connection;
    }

    public MySQLUtils(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true";
            connection = DriverManager.getConnection(url, username, password);
        }
    }

    public void setupTables() throws SQLException {
        connect();
        String sql = "CREATE TABLE IF NOT EXISTS homes ("
                + "uuid VARCHAR(36) NOT NULL,"
                + "home_name VARCHAR(64) NOT NULL,"
                + "world VARCHAR(64) NOT NULL,"
                + "x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT,"
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