import models.Account;
import models.App;
import models.enums.Gender;
import models.enums.Menu;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import views.LoginMenu;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProfileMenuTest {
    private Account account1 = new Account(Gender.MALE, "test@gmail.com", "AliAlm", "TestPass#403","AliAlmasi");
    private Account account2 = new Account(Gender.MALE, "test@gmail.com", "AliAlm", "TestPass#403","AliAlmasi2");
    private LoginMenu menu;
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private ByteArrayOutputStream out;
    private Scanner scanner;

    @BeforeEach
    void setup() {
        App.setLoggedInAccount(account1);
        App.addAccount(account1);
        App.addAccount(account2);
        App.setCurrentMenu(Menu.PROFILE_MENU);
        menu = new LoginMenu();
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    private void setIn(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }
    private void checkStartExpected(String startExpected) {
        scanner = new Scanner(System.in);
        App.getCurrentMenu().checker(scanner);
        String output = out.toString().trim();
        assertTrue(output.startsWith(startExpected), "your code output:\n" + output + "\nexpected:\n" + startExpected + "\n");
    }

    private void checkExpected(String expected) {
        scanner = new Scanner(System.in);
        App.getCurrentMenu().checker(scanner);
        String output = out.toString().trim();
        assertEquals(expected, output, "your code output:\n" + output + "\nexpected:\n" + expected + "\n");
    }

    @Test
    void userInfoTest() {
        setIn("  user    info    ");
        checkExpected("Username : AliAlmasi\n" +
                "Nickname : AliAlm\n" +
                "Maximum Money Earned : 0\n" +
                "Played games : 0");
    }


    @ParameterizedTest
    @CsvSource({
            "change email -e test@gmail.com , your email must be different with old one",
            "   change    email   -e   test@gamil.co.m , \"m\" is too short",
            "change email -e  tes..t@gamil.com , email should not contain \"..\"",
                "change email -e test@gma@l.com , \"test@gma\" contains invalid characters"
    })
    void changeEmail(String input, String expected) {
        setIn(input);
        checkExpected(expected);
    }


    @ParameterizedTest
    @CsvSource({
            "change password   -p  TestPass#403   -o    TestPass#403, your password must be different with old one",
            "change  password  -p   asdasd   -o   TestPass#403, password should have at least 8 characters",
            "change   password -p CorrectP@ss123 -o Testpass#403, your password is incorrect",
            "change   password -p CorrectP@ss123 -o TestPass#403, your password changed successfully",
            "change password   -p  TestPass#403   -o    TestPass#403, your password must be different with old one"

    })
    void changePass(String input, String expected) {
        setIn(input);
        checkExpected(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "change username -u AliAlmasi2, this username is already taken",
            "change username -u AliAlmasi, your username must be different with old one",
            "  change       username    -u      AliAl3      ,your user name changed to AliAl3 successfully"
    })
    void changeUsername(String input, String expected) {
        setIn(input);
        checkExpected(expected);
    }



    @Test
    void changeNickname() {
        setIn("change   nickname  -u   AliAlm");
        checkExpected("your nickname must be different with old one");
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        setIn("   change   nickname    -u  Ali   ");
        checkExpected("your nickname changed to \"Ali\" successfully");
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        setIn("    user info");
        checkExpected("Username : AliAlmasi\n" +
                "Nickname : Ali\n" +
                "Maximum Money Earned : 0\n" +
                "Played games : 0");
    }

    void showCurrentMenu() {
        setIn("show     current     menu");
        checkExpected("LoginMenu");
    }

    @Test
    void testScannerBlocking() {
        setIn("hello\n");
        Scanner s = new Scanner(System.in);
        String line = s.nextLine(); // works if input ends with \n
        System.out.println("Read: " + line);
    }

}
