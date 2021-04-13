package ru.minat0.minetail.core.managers;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.minat0.minetail.core.Mage;
import ru.minat0.minetail.core.utils.Logger;
import ru.minat0.minetail.core.utils.StringBuilder;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author Minat0_
 * I'd seen example from RoinujNosde DatabaseManager's class.
 * https://github.com/RoinujNosde/TitansBattle/blob/master/src/main/java/me/roinujnosde/titansbattle/managers/DatabaseManager.java
 */
public class DatabaseManager {
    private final Plugin plugin;
    private final FileConfiguration config;
    private final HikariDataSource dataSource;

    private final Set<Mage> mages = new HashSet<>();

    public DatabaseManager(Plugin plugin, FileConfiguration config, DBType type) {
        this.plugin = plugin;
        this.config = config;
        this.dataSource = getDataSource(type);
        setup();
    }

    public enum DBType {
        SQLITE, MYSQL
    }

    public void setup() {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS minetail_players "
                    + "(uuid varchar(36) NOT NULL,"
                    + "name varchar(16) NOT NULL,"
                    + "magicLevel int NOT NULL,"
                    + "experience int DEFAULT 0 NOT NULL,"
                    + "magicRank varchar(255) NULL,"
                    + "magicClass varchar(16) NULL,"
                    + "manaBarColor varchar(16) NOT NULL,"
                    + "manaBarAppearTime varchar(16) NOT NULL,"
                    + "Spells varchar(255) NOT NULL,"
                    + "PRIMARY KEY ( uuid ));"
            );
        } catch (SQLException ex) {
            Logger.error("Error while creating the tables: " + ex.getMessage());
        }
    }

    private HikariDataSource getDataSource(DBType type) {
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
                dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
                dataSource.setJdbcUrl("jdbc:mariadb://" + hostname + ":" + port + "/" + database);
                return dataSource;
            default:
                Logger.warning("Unable to get type of database connection, please check \"DataSource.backend\" in config.yml! Using SQLite instead...");
                return getDataSource(DBType.SQLITE);
        }
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

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

    public void update(@NotNull Mage mage) {
        String uuid = mage.getUniqueId().toString();

        String update = "UPDATE minetail_players SET name=?, magicLevel=?, magicRank=?, magicClass=?, manaBarColor=?, manaBarAppearTime=?, Spells=? WHERE uuid=?;";
        try (PreparedStatement statement = getConnection().prepareStatement(update)) {
            statement.setString(1, mage.getName());
            statement.setInt(2, mage.getMagicLevel());
            statement.setString(3, mage.getRank());
            statement.setString(4, mage.getMagicClass());
            statement.setString(5, mage.getManaBarColor());
            statement.setString(6, mage.getManaBarAppearTime());
            statement.setString(7, StringBuilder.serialize(mage.getSpells()));
            statement.setString(8, uuid);

            statement.execute();
        } catch (SQLException ex) {
            Logger.error("An error occurred while trying to update the players data!" + ex.getMessage());
        }
    }


    public void update(@NotNull Set<Mage> mageSet) {
        String update = "UPDATE minetail_players SET name=?, magicLevel=?, magicRank=?, magicClass=?, manaBarColor=?, manaBarAppearTime=?, Spells=? WHERE uuid=?;";
        Logger.debug("Mages: " + mageSet.size(), false);
        for (Mage mage : mageSet) {
            if (mage.changed) {
                Logger.debug("Updating player: " + mage.getName(), true);
                String uuid = mage.getUniqueId().toString();
                try (PreparedStatement statement = getConnection().prepareStatement(update)) {
                    statement.setString(1, mage.getName());
                    statement.setInt(2, mage.getMagicLevel());
                    statement.setString(3, mage.getRank());
                    statement.setString(4, mage.getMagicClass());
                    statement.setString(5, mage.getManaBarColor());
                    statement.setString(6, mage.getManaBarAppearTime());
                    statement.setString(7, StringBuilder.serialize(mage.getSpells()));
                    statement.setString(8, uuid);
                    statement.execute();
                } catch (SQLException ex) {
                    Logger.error("An error occurred while trying to update the set of players data! " + ex.getMessage());
                }
            }
        }
    }

    public void delete(@NotNull Mage mage) {
        String uuid = mage.getUniqueId().toString();

        String delete = "DELETE FROM minetail_players WHERE uuid=?";
        try (PreparedStatement statement = getConnection().prepareStatement(delete)) {
            statement.setString(1, uuid);
            statement.execute();
        } catch (SQLException ex) {
            Logger.error("An error occurred while trying to delete the players data! " + ex.getMessage());
        }
    }

    public void insert(@NotNull Mage mage) {
        String uuid = mage.getUniqueId().toString();

        String insert = "INSERT INTO minetail_players (uuid, name, magicLevel, magicRank, magicClass, manaBarColor, manaBarAppearTime, Spells) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
        try (PreparedStatement statement = getConnection().prepareStatement(insert)) {
            statement.setString(1, uuid);
            statement.setString(2, mage.getName());
            statement.setInt(3, mage.getMagicLevel());
            statement.setString(4, mage.getRank());
            statement.setString(5, mage.getMagicClass());
            statement.setString(6, mage.getManaBarColor());
            statement.setString(7, mage.getManaBarAppearTime());
            statement.setString(8, StringBuilder.serialize(mage.getSpells()));
            statement.execute();
            mages.add(mage);
        } catch (SQLException ex) {
            Logger.error("An error occurred while trying to insert the players data! " + ex.getMessage());
        }
    }

    private void loopThroughMages() {
        String sql = "SELECT * FROM minetail_players;";
        try (Statement statement = getConnection().createStatement()) {
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                Integer magicLevel = rs.getInt("magicLevel");
                String rank = rs.getString("magicRank");
                String magicClass = rs.getString("magicClass");
                String manaBarColor = rs.getString("manaBarColor");
                String manaBarAppearTime = rs.getString("manaBarAppearTime");
                String[] Spells = StringBuilder.unserialize(rs.getString("Spells"));

                Mage mage = new Mage(uuid, magicLevel, rank, magicClass, manaBarColor, manaBarAppearTime, Spells);
                mages.add(mage);
            }
        } catch (SQLException ex) {
            Logger.error("An error occurred while trying to load the players data! " + ex.getMessage());
        }
    }

    @Nullable
    public Mage getMage(@NotNull UUID uuid) {
        for (Mage mage : mages) {
            if (mage.getOfflinePlayer().getUniqueId().equals(uuid)) {
                return mage;
            }
        }

        return null;
    }

    public Set<Mage> getMages() {
        return mages;
    }

    public void loadDataToMemory() {
        loopThroughMages();
    }

}
