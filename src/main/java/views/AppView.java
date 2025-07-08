package views;

import models.App;

import java.util.Scanner;

public class AppView {
    private final Scanner scanner;
    //false is the typical terminal mode
    private boolean rawMode = false;
    private String previusMessage = "";

    public AppView() {
        scanner = new Scanner(System.in);
    }

    public void run() {
        while (!App.shouldTerminate) {
            App.getCurrentMenu().checker(scanner);
        }
    }

    public boolean isRawMode() {
        return rawMode;
    }

    public String inputWithPrompt(String prompt) {
        System.out.println(prompt);
        return scanner.nextLine();
    }

    public void log(Object o) {
        if(o != null){
            System.out.println(o.toString());
            previusMessage = o.toString();
        }
    }
    public void log() {
    }

    public void err(String string) {
        System.err.println(string);
    }

    public Scanner getScanner() {
        return scanner;
    }

    public String getPreviusMessage() {
        return previusMessage;
    }
}
