package com.ap.stardew.controllers;

import com.ap.stardew.models.Account;
import com.ap.stardew.models.enums.Gender;
import com.ap.stardew.models.enums.SecurityQuestions;
import com.ap.stardew.utils.JSONUtils;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:game.db";

    public static void saveAccount(Account account) {
        //TODO put this in a better place
        createTableIfNotExists();

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
    public static void createTableIfNotExists() {
        String sql = """
    CREATE TABLE IF NOT EXISTS users (
        username TEXT PRIMARY KEY,
        nickname TEXT,
        email TEXT,
        gender TEXT,
        securityAnswers TEXT,
        maximumMoneyEarned INTEGER,
        password TEXT
    );
    """;

        try (var conn = DriverManager.getConnection(URL);
             var stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Failed to create 'users' table");
            e.printStackTrace();
        }
    }
}
