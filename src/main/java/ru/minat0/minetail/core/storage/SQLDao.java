package ru.minat0.minetail.core.storage;

import java.sql.Connection;

public class SQLDao {
    private final Connection connection;

    public SQLDao(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }
}
