package ru.xopek.mobrush.handler.database;

import java.io.File;
import java.sql.*;

public class SQLite {
    private static final String databaseName = "players";

    private final String url;

    /**
     * Создание базы данных, для хранения игроков
     * и сохранения их прогресса в мини-игре
     */

    public SQLite(File file) {
        url = "jdbc:sqlite:" + file.getAbsolutePath();

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + databaseName + " (" +
                    "uuid STRING PRIMARY KEY, " +
                    "name TEXT NOT NULL, " +
                    "money INTEGER NOT NULL," +
                    "xp INTEGER NOT NULL" +
                    ")");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void savePlayer(RushPlayer player) {
        String query = "INSERT INTO " + databaseName + " (uuid, name, money, xp) " +
                "VALUES (?, ?, ?, ?) ON CONFLICT(uuid) DO UPDATE SET " +
                "name = ?, money = ?, xp = ?";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, player.getUuid().toString());
            statement.setString(2, player.getName());
            statement.setInt(3, player.getMoney());
            statement.setInt(4, player.getXp());

            statement.setString(5, player.getName());
            statement.setInt(6, player.getMoney());
            statement.setInt(7, player.getXp());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public RushPlayer getPlayer(String uuid) {
        String query = "SELECT * FROM " + databaseName + " WHERE uuid = ?";
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                int money = resultSet.getInt("money");
                int xp = resultSet.getInt("xp");
                return new RushPlayer(uuid, name, money, xp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
