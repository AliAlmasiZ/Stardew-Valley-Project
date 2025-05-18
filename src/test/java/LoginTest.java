import models.Account;
import models.App;
import models.enums.Gender;
import models.enums.Menu;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import views.LoginMenu;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTest {

    private Account account1 = new Account(Gender.MALE, "test@gmail.com", "AliAlm", "TestPass#403","AliAlmasi");
    private Account account2 = new Account(Gender.MALE, "test@gmail.com", "AliAlm", "TestPass#403","AliAlmasi2");
    private LoginMenu menu;
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private ByteArrayOutputStream out;
    private Scanner scanner;

    @BeforeEach
    void setup() {
        App.setLoggedInAccount(null);
        App.addAccount(account1);
        App.addAccount(account2);
        App.setCurrentMenu(Menu.LOGIN_MENU);
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
    void invalidUserName() {
        setIn("login -u     meow -p asfasfasf");
        checkExpected("username doesnt exist");
    }

    @Test
    void invalidPass() {
        setIn("login -u    AliAlmasi    -p    TestPass#40");
        checkExpected("incorrect password");
    }

    @Test
    void loginSuccessfully() {
        setIn("    login    -u   AliAlmasi    -p    TestPass#403      ");
        checkExpected("logged in successfully");
    }

    @ParameterizedTest
    @CsvSource({
            "forget password -i   AliAlmasi, invalid command",
            "forget password -u  liAlmasi, ",
            "forget password -u   AliAlmasi,",
            "forget password -u   AliAlmasi",
            "forget password -u   AliAlmasi",
            "forget password -u   AliAlmasi"
    })
    void forgetPassword(String input, String expected) {
        setIn(input);
        checkExpected(expected) ;
    }






}
