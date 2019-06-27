package org.academiadecodigo.bootcamp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private int myPort = 9070;
    private boolean serverLive;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private LinkedList<ServerConnectionHandler> clientList;

    public Server() {
        clientList = new LinkedList<>();
        serverLive = true;
    }

    public void start() {

        ExecutorService fixedPool = Executors.newFixedThreadPool(100);
        try {
            serverSocket = new ServerSocket(myPort);

            while (serverLive) {
                clientSocket = serverSocket.accept();

                System.out.println("new client connected");
                synchronized (clientList) {
                    ServerConnectionHandler thisConnectionHandler = new ServerConnectionHandler(clientSocket, this);
                    fixedPool.submit(thisConnectionHandler);
                    clientList.add(thisConnectionHandler);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public LinkedList<ServerConnectionHandler> getClientList() {
        return clientList;
    }

}
