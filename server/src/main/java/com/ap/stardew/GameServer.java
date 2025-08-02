package com.ap.stardew;

import com.ap.stardew.app.ListenerThread;
import com.ap.stardew.app.ServerApp;
import com.ap.stardew.utils.JSONUtils;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.Scanner;

import static com.ap.stardew.models.ConnectionThread.TCP_PORT;
import static com.ap.stardew.models.ConnectionThread.UDP_PORT;


public class GameServer {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
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
