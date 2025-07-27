package com.ap.stardew.views.old;

import com.badlogic.gdx.Game;
import com.ap.stardew.models.App;

import java.util.Scanner;

public class AppView extends Game {
    private static AppView instance;

    private final Scanner scanner;
    //false is the typical terminal mode
    private String previusMessage = "";


    public static AppView getInstance(){
        if(instance == null) instance = new AppView();
        return instance;
    }

    @Override
    public void create() {
        //todo
    }

    public AppView() {
        scanner = new Scanner(System.in);
    }

    public void run() {
        while (!App.shouldTerminate) {
            App.getCurrentMenu().checker(scanner);
        }
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
