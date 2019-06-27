package org.academiadecodigo.bootcamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnectionHandler implements Runnable{

    private Socket clientSocket;
    private String receivedMessage;

    public ServerConnectionHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        while (true) {
            receiveMessage();
            System.out.println(receivedMessage);
        }
    }

    public void receiveMessage() {

        BufferedReader bReader = null;
        try {
            bReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            receivedMessage = bReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
