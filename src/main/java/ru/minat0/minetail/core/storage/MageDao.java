package ru.minat0.minetail.core.storage;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ru.minat0.minetail.core.Mage;
import ru.minat0.minetail.core.utils.Logger;

import javax.sql.DataSource;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

public class MageDao extends SQLDao implements Dao<Mage, UUID> {
    private final Set<Mage> mages = new HashSet<>();

    public MageDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<Mage> get(@NotNull UUID uuid) {
        return mages.stream().distinct().filter(mage -> mage.getUniqueId().equals(uuid)).findFirst();
    }

    @Override
    public Collection<Mage> getAll() {
        return mages;
    }

    @Override
    public void create(@NotNull Mage mage) {
        String uuid = mage.getUniqueId().toString();
        Gson gson = new Gson();

        String query = "INSERT INTO minetail_players (uuid, name, magicLevel, magicEXP, magicRank, magicClass, settings, Spells) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, uuid);
            statement.setString(2, mage.getName());
            statement.setInt(3, mage.getMagicLVL());
            statement.setInt(4, mage.getMagicEXP());
            statement.setString(5, mage.getMagicRank());
            statement.setString(6, mage.getMagicClass());
            statement.setString(7, gson.toJson(mage.getSettings()));
            statement.setString(8, String.join(",", mage.getSpells()));
            statement.execute();

            mages.add(mage);
        } catch (SQLException ex) {
            Logger.error("An error occurred while trying to insert the players data! " + ex.getMessage());
        }
    }

    @Override
    public void update(@NotNull UUID uuid) {
        if (get(uuid).isPresent()) {
            String query = "UPDATE minetail_players SET name=?, magicLevel=?, magicEXP=?, magicRank=?, magicClass=?, settings=?, Spells=? WHERE uuid=?;";
            Mage mage = get(uuid).get();
            Gson gson = new Gson();
            try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, mage.getName());
                statement.setInt(2, mage.getMagicLVL());
                statement.setInt(3, mage.getMagicEXP());
                statement.setString(4, mage.getMagicRank());
                statement.setString(5, mage.getMagicClass());
                statement.setString(6, gson.toJson(mage.getSettings()));
                statement.setString(7, String.join(",", mage.getSpells()));
                statement.setString(8, uuid.toString());

                statement.execute();
            } catch (SQLException ex) {
                Logger.error("An error occurred while trying to update the players data!" + ex.getMessage());
            }
        } else Logger.warning("Failed to update " + Bukkit.getOfflinePlayer(uuid).getName() + "!");
    }

    public void updateAll() {
        for (Mage mage : getAll()) {
            String query = "UPDATE minetail_players SET name=?, magicLevel=?, magicEXP=?, magicRank=?, magicClass=?, settings=?, Spells=? WHERE uuid=?;";
            Gson gson = new Gson();
            try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, mage.getName());
                statement.setInt(2, mage.getMagicLVL());
                statement.setInt(3, mage.getMagicEXP());
                statement.setString(4, mage.getMagicRank());
                statement.setString(5, mage.getMagicClass());
                statement.setString(6, gson.toJson(mage.getSettings()));
                statement.setString(7, String.join(",", mage.getSpells()));
                statement.setString(8, mage.getUniqueId().toString());

                statement.execute();
            } catch (SQLException ex) {
                Logger.error("An error occurred while trying to update the players data!" + ex.getMessage());
            }
        }
    }

    @Override
    public void delete(@NotNull UUID uuid) {
        String query = "DELETE FROM minetail_players WHERE uuid=?";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, uuid.toString());
            statement.execute();
        } catch (SQLException ex) {
            Logger.error("An error occurred while trying to delete the players data! " + ex.getMessage());
        }
    }

    public void loadMages() {
        String query = "SELECT * FROM minetail_players;";

        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                Integer magicLevel = rs.getInt("magicLevel");
                Integer magicEXP = rs.getInt("magicEXP");
                String magicRank = rs.getString("magicRank");
                String magicClass = rs.getString("magicClass");

                String settings = rs.getString("settings");
                Gson gson = new Gson();

                Type type = new TypeToken<Map<String, String>>() {
                }.getType();
                Map<String, String> settingMap = gson.fromJson(settings, type);

                List<String> spells = Arrays.asList(rs.getString("Spells").split(","));

                Mage mage = new Mage(uuid, magicClass, magicLevel, magicEXP, magicRank, settingMap, spells);
                mages.add(mage);
            }
        } catch (SQLException ex) {
            Logger.error("An error occurred while trying to load the players data! " + ex.getMessage());
        }
    }
}
