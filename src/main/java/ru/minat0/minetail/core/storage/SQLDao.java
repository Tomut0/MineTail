package ru.minat0.minetail.core.storage;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLDao {
    private final DataSource dataSource;

    public SQLDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
