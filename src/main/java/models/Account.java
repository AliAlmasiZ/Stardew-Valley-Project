package models;

import models.enums.Gender;
import models.enums.SecurityQuestions;
import records.Result;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Account implements Serializable {
    private static final Pattern validUsernameCharacters = Pattern.compile("^[a-zA-Z0-9-]*$");
    private static final Pattern emailGroupingPattern    = Pattern.compile("^(?<mail>\\S+)@(?<domain>\\S+)\\.(?<tld>\\S+)$");
    private static final Pattern validMailCharacters     = Pattern.compile("^[a-zA-Z0-9._-]+$");
    private static final Pattern validDomainCharacters   = Pattern.compile("^[a-zA-Z0-9.-]+$");
    private static final Pattern validPasswordCharacters = Pattern.compile("^[0-9A-Za-z?<>,\"';:/\\\\|\\[\\]{}+=()*&^%$@#!]+$");


    private String username;
    private String password;
    private String nickname;
    private String email;
    private final Gender gender;
    private Map<SecurityQuestions, String> securityAnswers = new HashMap<>();
    private Game activeGame = null;
    private final ArrayList<Game> playedGames = new ArrayList<>();

    public Account(Gender gender, String email, String nickname, String password, String username) {
        this.gender = gender;
        this.email = email;
        this.nickname = nickname;
        this.password = hashPassword(password);
        this.username = username;
    }

    public static Result isUsernameValid(String username) {
        if(username.isEmpty()){
            return new Result(false, "Username should not be empty");
        }
        if(!Account.validUsernameCharacters.matcher(username).matches()){
            return new Result(false, "Username contains invalid characters");
        }
        return new Result(true, "");
    }

    public static Result isPasswordValid(String password) {
        if(password.isEmpty()){
            return new Result(false, "password should not be empty");
        }
        if(password.length()<8){
            return new Result(false, "password should have at least 8 characters");
        }
        if(!validPasswordCharacters.matcher(password).matches()){
            return new Result(false, "password contains invalid characters");
        }
        if (!password.matches(".*[a-z].*")) {
            return new Result(false, "Password must contain at least one lowercase letter");
        }

        if (!password.matches(".*[A-Z].*")) {
            return new Result(false, "Password must contain at least one uppercase letter");
        }

        if (!password.matches(".*[0-9].*")) {
            return new Result(false, "Password must contain at least one digit");
        }

        if (!password.matches(".*[?<>,\"';:/\\\\|\\[\\]{}+=()*&^%@$#!]+.*")){
            return new Result(false, "Password must contain at least one special character");
        }
        return new Result(true, "");
    }

    private static String hashPassword(String password) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    public static Result isEmailValid(String email) {
        if(email.isEmpty()){
            return new Result(false, "email should not be empty");
        }

        Matcher emailGroups = Account.emailGroupingPattern.matcher(email);
        if(!emailGroups.matches()){
            return new Result(false, "email format is invalid : sample@domain.com");
        }

        String tld    = emailGroups.group("tld");
        String mail   = emailGroups.group("mail");
        String domain = emailGroups.group("domain");

        if(!validMailCharacters.matcher(mail).matches()){
            return new Result(false, "\"" + mail + "\" contains invalid characters");
        }
        if(!(Character.isLetterOrDigit(mail.charAt(0)) && Character.isLetterOrDigit(mail.charAt(mail.length()-1)))){
            return new Result(false, "\"" + mail + "\" should start and end with a letter or digit");
        }
        if(!(Character.isLetterOrDigit(domain.charAt(0)) && Character.isLetterOrDigit(tld.charAt(tld.length()-1)))){
            return new Result(false, "\"" + domain + "." + tld + "\" should start and end with a letter or digit");
        }
        if(!validDomainCharacters.matcher(domain+tld).matches()){
            return new Result(false, "\"" + domain + "." + tld + "\" contains invalid characters");
        }
        if(tld.length() < 2){
            return new Result(false, "\"" + tld + "\" is too short");
        }
        if(email.contains("..")){
            return new Result(false, "email should not contain \"..\"");
        }

        return new Result(true, "");
    }
    public static Result isNicknameValid(String nickname) { //check all username conditions
        return new Result(true, "");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isPasswordCorrect(String password) { /* !! don't use getPassword because of SHA-256 */
        return this.password.equals(hashPassword(password));
    }

    public void setPassword(String password) {
        this.password = hashPassword(password);
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }


    public Map<SecurityQuestions, String> getSecurityAnswers() {
        return securityAnswers;
    }

    public void setSecurityAnswers(Map<SecurityQuestions, String> securityAnswers) {
        this.securityAnswers = securityAnswers;
    }

    public long getMaximumMoneyEarned() {
        // TODO: implement later
        return 0;
    }

    public int gamesCount() {
        return playedGames.size();
    }

    public Game getActiveGame() {
        return activeGame;
    }

    public void setActiveGame(Game activeGame) {
        this.activeGame = activeGame;
    }
}
