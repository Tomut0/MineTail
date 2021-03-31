package ru.minat0.minetail.managers;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.minat0.minetail.MineTail;
import ru.minat0.minetail.data.Mage;
import ru.minat0.minetail.utils.ErrorsUtil;
import ru.minat0.minetail.utils.StringBuilderUtils;

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
    private final MineTail plugin = MineTail.getInstance();

    final String dbName = "MineTail";
    private Connection connection;

    private final Set<Mage> mages = new HashSet<>();

    public void setup() {
        try {
            Statement statement = getConnection().createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS minetail_players "
                    + "(uuid varchar(36) NOT NULL,"
                    + "name varchar(16) NOT NULL,"
                    + "magicLevel int NOT NULL,"
                    + "rank varchar(255) NULL,"
                    + "magicClass varchar(16) NULL,"
                    + "manaBarColor varchar(16) NOT NULL,"
                    + "manaBarAppearTime varchar(16) NOT NULL,"
                    + "Spells varchar(255) NOT NULL,"
                    + "PRIMARY KEY ( uuid ));"
            );
        } catch (SQLException ex) {
            ErrorsUtil.error("Error while creating the tables: " + ex.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            initialize();
        }

        return connection;
    }

    private void initialize() throws SQLException {
        HikariDataSource ds = new HikariDataSource();
        FileConfiguration config = MineTail.getConfiguration().getConfig();

        String dbType = config.getString("DataSource.backend", "SQLITE");

        assert dbType != null;
        if (!dbType.equalsIgnoreCase("mysql") && !dbType.equalsIgnoreCase("sqlite")) {
            ErrorsUtil.warning("Error while getting the type of DB, please check \"DataSource.backend\" in config.yml! Using SQLite instead...");
            dbType = "SQLITE";
        }

        if (dbType.equalsIgnoreCase("MYSQL")) {
            String database = config.getString("DataSource.mySQLDatabase");
            String hostname = config.getString("DataSource.mySQLHost");
            String port = config.getString("DataSource.mySQLPort");

            ds.setUsername(config.getString("DataSource.mySQLUsername"));
            ds.setPassword(config.getString("DataSource.mySQLPassword"));
            ds.setMaximumPoolSize(20);
            ds.setDriverClassName("org.mariadb.jdbc.Driver");
            ds.setJdbcUrl("jdbc:mariadb://" + hostname + ":" + port + "/" + database);
            ds.addDataSourceProperty("cachePrepStmts", "true");
            ds.addDataSourceProperty("prepStmtCacheSize", "250");
            ds.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            ds.setIdleTimeout(1800000);

            try {
                connection = ds.getConnection();
            } catch (SQLException ex) {
                ErrorsUtil.error("Error while trying to establish database connection: " + ex.getMessage());
            }
        } else {
            File sqlFile = new File(plugin.getDataFolder(), dbName + ".db");

            if (!sqlFile.exists()) {
                try {
                    sqlFile.createNewFile();
                } catch (IOException ex) {
                    ErrorsUtil.error("File write error: " + dbName + ".db");
                }
            }

            try {
                ds.setDriverClassName("org.sqlite.JDBC");
                ds.setJdbcUrl("jdbc:sqlite:" + sqlFile);
                connection = ds.getConnection();
            } catch (SQLException ex) {
                ErrorsUtil.error("SQLite driver not found!");
            }
        }
    }

    public void update(@NotNull Mage mage) {
        String uuid = mage.getUniqueId().toString();

        String update = "UPDATE minetail_players SET name=?, magicLevel=?, rank=?, magicClass=?, manaBarColor=?, manaBarAppearTime=?, Spells=? WHERE uuid=?;";
        try (PreparedStatement statement = getConnection().prepareStatement(update)) {
            statement.setString(1, mage.getName());
            statement.setInt(2, mage.getMagicLevel());
            statement.setString(3, mage.getRank());
            statement.setString(4, mage.getMagicClass());
            statement.setString(5, mage.getManaBarColor());
            statement.setString(6, mage.getManaBarAppearTime());
            statement.setString(7, StringBuilderUtils.serialize(mage.getSpells()));
            statement.setString(8, uuid);

            statement.execute();
        } catch (SQLException ex) {
            ErrorsUtil.error("An error occurred while trying to update the players data!" + ex.getMessage());
        }
    }


    public void update(@NotNull Set<Mage> mageSet) {
        String update = "UPDATE minetail_players SET name=?, magicLevel=?, rank=?, magicClass=?, manaBarColor=?, manaBarAppearTime=?, Spells=? WHERE uuid=?;";
        ErrorsUtil.debug("Mages: " + mageSet.size(), false);
        for (Mage mage : mageSet) {
            if (mage.changed) {
                ErrorsUtil.debug("Updating player: " + mage.getName(), true);
                String uuid = mage.getUniqueId().toString();
                try (PreparedStatement statement = getConnection().prepareStatement(update)) {
                    statement.setString(1, mage.getName());
                    statement.setInt(2, mage.getMagicLevel());
                    statement.setString(3, mage.getRank());
                    statement.setString(4, mage.getMagicClass());
                    statement.setString(5, mage.getManaBarColor());
                    statement.setString(6, mage.getManaBarAppearTime());
                    statement.setString(7, StringBuilderUtils.serialize(mage.getSpells()));
                    statement.setString(8, uuid);
                    statement.execute();
                } catch (SQLException ex) {
                    ErrorsUtil.error("An error occurred while trying to update the set of players data! " + ex.getMessage());
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
            ErrorsUtil.error("An error occurred while trying to delete the players data! " + ex.getMessage());
        }
    }

    public void insert(@NotNull Mage mage) {
        String uuid = mage.getUniqueId().toString();

        String insert = "INSERT INTO minetail_players (uuid, name, magicLevel, rank, magicClass, manaBarColor, manaBarAppearTime, Spells) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
        try (PreparedStatement statement = getConnection().prepareStatement(insert)) {
            statement.setString(1, uuid);
            statement.setString(2, mage.getName());
            statement.setInt(3, mage.getMagicLevel());
            statement.setString(4, mage.getRank());
            statement.setString(5, mage.getMagicClass());
            statement.setString(6, mage.getManaBarColor());
            statement.setString(7, mage.getManaBarAppearTime());
            statement.setString(8, StringBuilderUtils.serialize(mage.getSpells()));
            statement.execute();
            mages.add(mage);
        } catch (SQLException ex) {
            ErrorsUtil.error("An error occurred while trying to insert the players data! " + ex.getMessage());
        }
    }

    private void loopThroughMages() {
        String sql = "SELECT * FROM minetail_players;";
        try (Statement statement = getConnection().createStatement()) {
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                Integer magicLevel = rs.getInt("magicLevel");
                String rank = rs.getString("rank");
                String magicClass = rs.getString("magicClass");
                String manaBarColor = rs.getString("manaBarColor");
                String manaBarAppearTime = rs.getString("manaBarAppearTime");
                String[] Spells = StringBuilderUtils.unserialize(rs.getString("Spells"));

                Mage mage = new Mage(uuid, magicLevel, rank, magicClass, manaBarColor, manaBarAppearTime, Spells);
                mages.add(mage);
            }
        } catch (SQLException ex) {
            ErrorsUtil.error("An error occurred while trying to load the players data! " + ex.getMessage());
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

    public void close(PreparedStatement ps, ResultSet rs) {
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            ErrorsUtil.error("Failed to close SQL connection: " + ex.getMessage());
        }
    }

    public void close(PreparedStatement ps) {
        try {
            if (ps != null)
                ps.close();
        } catch (SQLException ex) {
            ErrorsUtil.error("Failed to close SQL connection: " + ex.getMessage());
        }
    }

    public Set<Mage> getMages() {
        return mages;
    }

    public void loadDataToMemory() {
        loopThroughMages();
    }

}
