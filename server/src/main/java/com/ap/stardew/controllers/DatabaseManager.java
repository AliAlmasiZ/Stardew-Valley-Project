package com.ap.stardew.controllers;

import com.ap.stardew.models.Account;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.dto.SavedGameDetails;
import com.ap.stardew.models.enums.Gender;
import com.ap.stardew.models.enums.SecurityQuestions;
import com.ap.stardew.utils.JSONUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:game.db";

    public static void saveAccount(Account account) {
        //TODO put this in a better place
        try {
            createTableIfNotExists();
        } catch (IOException e) {
            System.err.println("Error in reading schema");
            e.printStackTrace();
        }

        String sql = "INSERT INTO users ( " +
            "    username, " +
            "    nickname, " +
            "    email, " +
            "    gender, " +
            "    securityAnswers, " +
            "    maximumMoneyEarned, " +
            "    password " +
            ") VALUES (?, ?, ?, ?, ?, ?, ?) " +
            "ON CONFLICT(username) DO UPDATE SET " +
            "    nickname           = excluded.nickname, " +
            "    email              = excluded.email, " +
            "    gender             = excluded.gender, " +
            "    securityAnswers    = excluded.securityAnswers, " +
            "    maximumMoneyEarned = excluded.maximumMoneyEarned, " +
            "    password           = excluded.password;";

        try(var conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false);

            try(var pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, account.getUsername());
                pstmt.setString(2, account.getNickname());
                pstmt.setString(3, account.getEmail());
                pstmt.setString(4, account.getGender().name());
                pstmt.setString(5, JSONUtils.toJson(account.getSecurityAnswers()));
                pstmt.setLong(6, account.getMaximumMoneyEarned());
                pstmt.setString(7, account.getPassword());

                pstmt.executeUpdate();
                System.out.println("user \"" + account.getUsername() + "\" saved successfully");
            } catch (SQLException e) {
                System.err.println("Error in save user " + account.getUsername());
                e.printStackTrace();
                conn.rollback();
            }

            conn.commit();
        } catch (SQLException e) {
            System.err.println("Error in save user " + account.getUsername());
            e.printStackTrace();
        }
    }

    public static void saveGame(String savePath, Game game, List<String> usernames) throws SQLException {
        String insertGameSQL = "INSERT INTO games (save_path, game_date) VALUES (?, ?)";
        String insertRelationSQL = "INSERT INTO user_game (game_id, username, farm, gold) VALUES (?, ?, ?, ?)";

        try (var conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false);
            int gameId;

            // 1. Insert into games table
            try (var stmt = conn.prepareStatement(insertGameSQL, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, savePath);
                stmt.setString(2, game.getDate().toString());
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        gameId = rs.getInt(1);
                    } else {
                        conn.rollback();
                        throw new SQLException("Failed to get generated game_id.");
                    }
                }
            }

            // 2. Insert user relations
            try (var stmt = conn.prepareStatement(insertRelationSQL)) {
                for (String username : usernames) {
                    stmt.setInt(1, gameId);
                    stmt.setString(2, username);
                    stmt.setString(3, game.getPlayerByUsername(username).getOwnedRegions().get(0).getName());
                    stmt.setInt(4, (int) game.getPlayerByUsername(username).getWallet().getBalance());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            conn.commit();
        }
    }

    public static List<SavedGameDetails> findGamesByUser(String username) throws SQLException {
        String sqlGames = """
        SELECT g.game_date, ug.farm, ug.gold, g.game_id
        FROM games g
        JOIN user_game ug ON g.game_id = ug.game_id
        WHERE ug.username = ?
    """;

        String sqlPlayers = """
        SELECT username
        FROM user_game
        WHERE game_id = ?
    """;

        List<SavedGameDetails> games = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmtGames = conn.prepareStatement(sqlGames);
             PreparedStatement stmtPlayers = conn.prepareStatement(sqlPlayers)) {

            // Get all games this user is in
            stmtGames.setString(1, username);
            try (ResultSet rsGames = stmtGames.executeQuery()) {
                while (rsGames.next()) {
                    int gameId = rsGames.getInt("game_id");
                    String gameDate = rsGames.getString("game_date");
                    String farm = rsGames.getString("farm");
                    int gold = rsGames.getInt("gold");

                    // Get all players for this game
                    List<String> players = new ArrayList<>();
                    stmtPlayers.setInt(1, gameId);
                    try (ResultSet rsPlayers = stmtPlayers.executeQuery()) {
                        while (rsPlayers.next()) {
                            players.add(rsPlayers.getString("username"));
                        }
                    }

                    // Fill DTO
                    SavedGameDetails details = new SavedGameDetails(gameDate, players, gold, farm, gameId);

                    games.add(details);
                }
            }
        }

        return games;
    }

    public static Account loadAccount(String username) {
        var sql = """
        SELECT
            username,
            nickname,
            email,
            gender,
            securityAnswers,
            maximumMoneyEarned,
            password
        FROM users
        WHERE username = ?
        """;
        try(var conn = DriverManager.getConnection(URL)) {
            var pstmt = conn.prepareStatement(sql);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in load account " + username);
            e.printStackTrace();
            return null;
        }
    }

    public static List<Account> findAllAccounts() {
        String sql = """
        SELECT
          username,
          nickname,
          email,
          gender,
          securityAnswers,
          maximumMoneyEarned,
          password
        FROM users
        """;

        List<Account> accounts = new ArrayList<>();
        try (var conn = DriverManager.getConnection(URL)){
            var pstmt = conn.prepareStatement(sql);

            var rs = pstmt.executeQuery();

            while (rs.next()) {
                accounts.add(mapRowToUser(rs));
            }

            return accounts;
        } catch (SQLException e) {
            System.err.println("Failed to load all accounts");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static Account mapRowToUser(ResultSet rs) throws SQLException {
        Account a = new Account();
        a.setUsername(rs.getString("username"));
        a.setNickname(rs.getString("nickname"));
        a.setEmail(rs.getString("email"));
        a.setGender(Gender.valueOf(rs.getString("gender")));
        a.setPasswordHashed(rs.getString("password"));
        a.setSecurityAnswers(JSONUtils.fromJson(rs.getString("securityAnswers"), Map.class));

        return a;
    }



    public static void createTableIfNotExists() throws IOException {
        var classLoader = DatabaseManager.class.getClassLoader();



        try (var conn = DriverManager.getConnection(URL);
             var stmt = conn.createStatement();
             var inputStream = classLoader.getResourceAsStream("schema.sql")) {

            if (inputStream == null) {
            throw new IOException("Cannot find resource file 'schema.sql'");
            }

            String sql = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            for (String statement : sql.split(";")) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to create 'users' table");
            e.printStackTrace();
        }
    }


}
