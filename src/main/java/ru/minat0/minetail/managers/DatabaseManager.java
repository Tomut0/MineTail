package ru.minat0.minetail.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import ru.minat0.minetail.MineTail;
import ru.minat0.minetail.data.Mage;
import ru.minat0.minetail.utils.ErrorsUtil;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Collections;
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
        FileConfiguration config = MineTail.getConfiguration().getConfig();

        String dbType = config.getString("DataSource.backend", "SQLITE");

        assert dbType != null;
        if (!dbType.equalsIgnoreCase("mysql") && !dbType.equalsIgnoreCase("sqlite")) {
            ErrorsUtil.warning("Error while getting the type of DB, please check \"DataSource.backend\" in config.yml! Using SQLite instead...");
            dbType = "SQLITE";
        }

        if (dbType.equalsIgnoreCase("MYSQL")) {

            String hostname = config.getString("DataSource.mySQLHost");
            String port = config.getString("DataSource.mySQLPort");
            String database = config.getString("DataSource.mySQLDatabase");
            String username = config.getString("DataSource.mySQLUsername");
            String password = config.getString("DataSource.mySQLPassword");

            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + "/" + database +
                        "?useSSL=false", username, password);
            } catch (ClassNotFoundException ex) {
                ErrorsUtil.error("MySQL driver not found!");
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
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + sqlFile);
            } catch (ClassNotFoundException | SQLException ex) {
                ErrorsUtil.error("SQLite driver not found!");
            }
        }
    }

    public void update(@NotNull Mage mage) {
        String uuid = mage.getUniqueId().toString();

        String update = "UPDATE minetail_players SET name=?, magicLevel=?, rank=?, magicClass=?, manaBarColor=? WHERE uuid=?;";
        try (PreparedStatement statement = getConnection().prepareStatement(update)) {
            statement.setString(1, mage.getName());
            statement.setInt(2, mage.getMagicLevel());
            statement.setString(3, mage.getRank());
            statement.setString(4, mage.getMagicClass());
            statement.setString(5, mage.getManaBarColor());
            statement.setString(6, uuid);

            statement.execute();
        } catch (SQLException ex) {
            ErrorsUtil.error("An error occurred while trying to update the players data!" + ex.getMessage());
        }
    }

    public void insert(@NotNull Mage mage) {
        String uuid = mage.getUniqueId().toString();

        String insert = "INSERT INTO minetail_players (uuid, name, magicLevel, rank, magicClass, manaBarColor) VALUES (?, ?, ?, ?, ?, ?);";
        try (PreparedStatement statement = getConnection().prepareStatement(insert)) {
            statement.setString(1, uuid);
            statement.setString(2, mage.getName());
            statement.setInt(3, mage.getMagicLevel());
            statement.setString(4, mage.getRank());
            statement.setString(5, mage.getMagicClass());
            statement.setString(6, mage.getManaBarColor());
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

                Mage mage = new Mage(Bukkit.getOfflinePlayer(uuid), magicLevel, rank, magicClass, manaBarColor);
                mages.add(mage);
            }
        } catch (SQLException ex) {
            ErrorsUtil.error("An error occurred while trying to load the players data! " + ex.getMessage());
        }
    }

    public Mage getMage(@NotNull UUID uuid) {
        for (Mage mage : mages) {
            if (mage.toPlayer().getUniqueId().equals(uuid)) {
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
