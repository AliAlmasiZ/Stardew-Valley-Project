package com.ap.stardew;

import com.ap.stardew.app.GameThread;
import com.ap.stardew.app.ServerApp;
import com.ap.stardew.models.player.Player;
import com.ap.stardew.utils.JSONUtils;
import com.esotericsoftware.kryonet.Server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import static com.ap.stardew.models.ConnectionThread.TCP_PORT;
import static com.ap.stardew.models.ConnectionThread.UDP_PORT;


public class GameServer {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Working dir (user.dir): " + System.getProperty("user.dir"));
        try {
            System.out.println("starting server...");

            //this is for using socket
//            ServerApp.setListenerThread(new ListenerThread(PORT));
//            ServerApp.startListening();

            //for start kryoNet server
            ServerApp.startServer(TCP_PORT, UDP_PORT);
            ServerApp.initializeServerListener(); // add message handling and client connections listener

            System.out.println("Server started successfully on tcp : " + TCP_PORT + " udp : " + UDP_PORT);

        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
            return;
        }


        while (!ServerApp.isEnded()) {
            if(scanner.hasNextLine()) {
                String input = scanner.nextLine().trim().toLowerCase();
                if (input.equals("exit")) {
                    ServerApp.endAll();
                }
                else if (Pattern.compile("show\\s+all\\s+games").matcher(input).matches()) {
                    List<GameThread> gameThreads = ServerApp.getGameThreads();
                    for (int i = 0; i < gameThreads.size(); i++) {
                        GameThread gameThread = gameThreads.get(i);
                        String alive = gameThread.isAlive() ? "Alive!" : "Finished!";
                        System.out.println("game" + i + " " +  alive + " Players :");
                        for (Player player : gameThread.getGame().getPlayers()) {
                            System.out.println(player.getUsername());
                        }
                        System.out.println("---------------");
                    }
                }
                else {
                    System.out.println("Invalid input");
                }
            }
        }
        System.out.println("server stoped");
        scanner.close();
    }
}
