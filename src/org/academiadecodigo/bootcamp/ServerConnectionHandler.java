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
    private boolean serverLive;

    public ServerConnectionHandler(Socket clientSocket, Server server) {
        serverLive = true;
        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {
        while (serverLive) {
            receiveMessage();
        }
    }

    public void receiveMessage() {

        BufferedReader bReader = null;
        try {
            bReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


            receivedMessage = bReader.readLine();
            System.out.println(receivedMessage);


            switch (receivedMessage.toUpperCase()) {

                case "EXIT":
                    exitCommand();
                    break;

                case "HELP":
                    this.getOutputStream().write(allCommands().getBytes());
                    break;

                    default:
                        receivedMessage+="\n";
                        sendMessageToAll();
                        break;
            }


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

                if (c.getOutputStream() != this.getOutputStream()) {

                    c.getOutputStream().write(receivedMessage.getBytes());
                }

            }

        }

    }

    public void exitCommand() throws IOException {
        server.getClientList().remove(this);
        this.getOutputStream().close();
        this.clientSocket.close();
        serverLive = false;
    }

    public String allCommands() {
        return "Currently there are two commands: \n" +
                "Exit will let you exit the chat\n" +
                "Help will show you all the commands\n";
    }
}
