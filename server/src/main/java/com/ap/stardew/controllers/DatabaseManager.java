package com.ap.stardew.controllers;

import com.ap.stardew.models.Account;
import com.ap.stardew.models.Game;
import com.ap.stardew.models.dto.SavedGameDetails;
import com.ap.stardew.models.enums.Gender;
import com.ap.stardew.models.enums.SecurityQuestions;
import com.ap.stardew.utils.JSONUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:game.db";

    static {
        //TODO put this in a better place
        try {
            createTableIfNotExists();
        } catch (IOException e) {
            System.err.println("Error in reading schema");
            e.printStackTrace();
        }
    }

    public static void saveAccount(Account account) {


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

    public static void saveAudioFile(String username, String fileName, byte[] data) throws IOException {
        String sql = """
            INSERT OR REPLACE INTO audio_files (username, file_name, audio_data) VALUES (?, ?, ?)
            """;

        if(data.length > 10 * 1024 * 1024) {
            throw new IOException("File too large");
        }
        if(!userExists(username)) {
            throw new IllegalArgumentException("Invalid user");
        }
        try (var conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false);



            try (var pstmt = conn.prepareStatement(sql)){
                pstmt.setString(1, username);
                pstmt.setString(2, fileName);
                pstmt.setBytes(3, data);
                pstmt.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean userExists(String username) {
        try (var pstmt = DriverManager.getConnection(URL).prepareStatement(
            "SELECT 1 FROM users WHERE username = ?"
        )){
            pstmt.setString(1, username);
            var rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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

    /**
     * find the games which the player has played
     * @param username
     * @return
     * @throws SQLException
     */
    public static List<SavedGameDetails> findGamesByUser(String username) throws SQLException {
        String sqlGames = """
        SELECT g.game_id
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

                    SavedGameDetails details = getSavedGameDetails(gameId);

                    games.add(details);
                }
            }
        }

        return games;
    }

    /**
     * get the details of a specific game
     * @param gameId
     * @return
     * @throws SQLException
     */
    public static SavedGameDetails getSavedGameDetails(int gameId) throws SQLException {
        String sqlGame = """
        SELECT g.game_id, g.game_date, ug.username, ug.farm, ug.gold
        FROM games g
        JOIN user_game ug ON g.game_id = ug.game_id
        WHERE g.game_id = ?
    """;

        SavedGameDetails details = new SavedGameDetails();
        details.players = new ArrayList<>();
        details.farms = new HashMap<>();
        details.gold = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(sqlGame)) {

            stmt.setInt(1, gameId);

            try (ResultSet rs = stmt.executeQuery()) {
                boolean firstRow = true;
                while (rs.next()) {
                    String username = rs.getString("username");

                    if (firstRow) {
                        details.gameId = rs.getInt("game_id");
                        details.inGameDate = rs.getString("game_date");
                        firstRow = false;
                    }

                    // Add player
                    details.players.add(username);

                    // Add farm and gold for this player
                    details.farms.put(username, rs.getString("farm"));
                    details.gold.put(username, rs.getInt("gold"));
                }
            }
        }

        return details;
    }

    /**
     * get the save path of a specific game
     * @param gameId
     * @return
     * @throws SQLException
     */
    public static String getSavedGamePath(int gameId) throws SQLException {
        String sql = "SELECT save_path FROM games WHERE game_id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("save_path");
                } else {
                    return null; // or throw exception if game not found
                }
            }
        }
    }

    public static boolean deleteGame(int gameId) throws SQLException {
        String sql = "DELETE FROM games WHERE game_id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameId);
            int affected = stmt.executeUpdate();
            return affected > 0;
        }
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

    public static List<String> allUserAudioFiles(String username) {
        try (var conn = DriverManager.getConnection(URL)) {
            try (var pstmt = conn.prepareStatement(
                "SELECT a.file_name, u.username " +
                    "FROM audio_files a " +
                    "JOIN users u ON a.username = u.username " +
                    "WHERE a.username = ?")) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                List<String> files = new ArrayList<>();
                while (rs.next()) {
                    files.add(rs.getString("username") + ": " + rs.getString("file_name"));
                }
                return files;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();

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
