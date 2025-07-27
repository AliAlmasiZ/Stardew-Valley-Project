package com.ap.stardew.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static com.ap.stardew.GameServer.PORT;

public class ListenerThread extends Thread{

    private final ServerSocket serverSocket;

    public ListenerThread(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    private void handleConnection(Socket socket) {
        if(socket == null) return;
        try {
            new ClientConnectionThread(socket).start();
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException ignored) {}
            System.err.println("error in connection client :" + socket.getInetAddress().getAddress() + "\n"
                + e.getMessage());
        }

    }


    @Override
    public void run() {
        while (!ServerApp.isEnded()) {
            try {
                System.out.println("Server is listening on port " + PORT);
                var socket = serverSocket.accept();
                System.out.println("New client connected : " + socket.getInetAddress().getHostAddress());
                handleConnection(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            serverSocket.close();
        } catch (Exception e) {
            System.err.println("error in closing serverSocket:\n" + e.getMessage());
        }
    }
}
