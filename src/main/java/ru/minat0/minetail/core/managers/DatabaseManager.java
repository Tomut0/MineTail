package ru.minat0.minetail.core.managers;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.minat0.minetail.core.Mage;
import ru.minat0.minetail.core.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Minat0_
 * I'd seen example from RoinujNosde DatabaseManager's class.
 * https://github.com/RoinujNosde/TitansBattle/blob/master/src/main/java/me/roinujnosde/titansbattle/managers/DatabaseManager.java
 */
public class DatabaseManager {
    private final JavaPlugin plugin;
    private final FileConfiguration config;

    private HikariDataSource dataSource;

    private final Set<Mage> mages = new HashSet<>();

    public DatabaseManager(JavaPlugin plugin, FileConfiguration config, DBType type) {
        this.plugin = plugin;
        this.config = config;
        this.dataSource = getDataSource(type);
        setup();
    }

    public enum DBType {
        SQLITE, MYSQL
    }

    private void setup() {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS minetail_players "
                    + "(uuid varchar(36) NOT NULL,"
                    + "name varchar(16) NOT NULL,"
                    + "magicLevel int NOT NULL,"
                    + "magicEXP int DEFAULT 0 NOT NULL,"
                    + "magicRank varchar(255) NULL,"
                    + "magicClass varchar(16) NOT NULL,"
                    + "settings JSON NOT NULL,"
                    + "Spells varchar(255) NOT NULL,"
                    + "PRIMARY KEY ( uuid ));"
            );
        } catch (SQLException ex) {
            Logger.error("Error while creating the tables: " + ex.getMessage());
        }
    }

    private HikariDataSource getDataSource(DBType type) {
        dataSource = new HikariDataSource();
        dataSource.setMaximumPoolSize(20);
        dataSource.addDataSourceProperty("cachePrepStmts", "true");
        dataSource.addDataSourceProperty("prepStmtCacheSize", "250");
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        switch (type) {
            case SQLITE:
                File SQLFile = createFileIfNotExist();
                dataSource.setDriverClassName("org.sqlite.JDBC");
                dataSource.setJdbcUrl("jdbc:sqlite:" + SQLFile);
                return dataSource;
            case MYSQL:
                String database = config.getString("DataSource.mySQLDatabase");
                String hostname = config.getString("DataSource.mySQLHost");
                String port = config.getString("DataSource.mySQLPort");
                String username = config.getString("DataSource.mySQLUsername");
                String password = config.getString("DataSource.mySQLPassword");
                dataSource.setUsername(username);
                dataSource.setPassword(password);
                dataSource.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database + "?useSSL=false&characterEncoding=utf-8&autoReconnect=true");
                return dataSource;
            default:
                Logger.warning("Unable to get type of database connection, please check \"DataSource.backend\" in config.yml! Using SQLite instead...");
                return getDataSource(DBType.SQLITE);
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    File createFileIfNotExist() {
        File sqlFile = new File(plugin.getDataFolder(), plugin.getDescription().getName() + ".db");

        if (!sqlFile.exists()) {
            try {
                sqlFile.createNewFile();
                return sqlFile;
            } catch (IOException ex) {
                Logger.error("File write error: " + plugin.getDescription().getName() + ".db");
            }
        }

        return sqlFile;
    }
}
