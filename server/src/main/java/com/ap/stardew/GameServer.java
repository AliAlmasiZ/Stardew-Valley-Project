package com.ap.stardew;

import com.ap.stardew.app.ListenerThread;
import com.ap.stardew.app.ServerApp;

import java.io.IOException;
import java.util.Scanner;

public class GameServer {
    private static final Scanner scanner = new Scanner(System.in);
    public static final int PORT = 3232;

    public static void main(String[] args) {

        try {
            System.out.println("starting server...");
            ServerApp.setListenerThread(new ListenerThread(PORT));
            ServerApp.startListening();
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
            return;
        }


        while (!ServerApp.isEnded()) {
            if(scanner.hasNextLine()) {
                String input = scanner.nextLine().trim();
                if (input.equals("exit")) {
                    ServerApp.endAll();
                }
            }
        }
        System.out.println("server stoped");
        scanner.close();
    }
}
