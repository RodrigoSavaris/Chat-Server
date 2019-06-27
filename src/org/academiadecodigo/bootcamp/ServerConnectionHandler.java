package org.academiadecodigo.bootcamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnectionHandler implements Runnable{

    private Socket clientSocket;
    private String receivedMessage;
    private Server server;

    public ServerConnectionHandler(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {
        while (true) {
            receiveMessage();
            System.out.println(receivedMessage);
            try {
                sendMessageToAll();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void receiveMessage() {

        BufferedReader bReader = null;
        try {
            bReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


            receivedMessage = bReader.readLine();
            System.out.println(receivedMessage);
            char[] testing = receivedMessage.toCharArray();
            for ( int i = 0; i<testing.length;i++) {
                System.out.println(testing[i]);
            }


            if (receivedMessage == "Exit") {
                System.out.println("the command can work here");
            }

            receivedMessage+="\n";

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public OutputStream getOutputStream() throws IOException {
        return clientSocket.getOutputStream();
    }

    public void sendMessageToAll() throws IOException {

        synchronized (server.getClientList()) {

            for (ServerConnectionHandler c : server.getClientList()) {

                c.getOutputStream().write(receivedMessage.getBytes());

            }

        }

    }
}
