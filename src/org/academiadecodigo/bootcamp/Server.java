package org.academiadecodigo.bootcamp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private int myPort = 9070;
    private boolean serverLive;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    public Server() {

        serverLive = true;
        ExecutorService fixedPool = Executors.newFixedThreadPool(100);

        try {
            serverSocket = new ServerSocket(myPort);

            while (serverLive) {

                clientSocket = serverSocket.accept();
                System.out.println("new client connected");
                ServerConnectionHandler thisConnectionHandler = new ServerConnectionHandler(clientSocket);
                fixedPool.submit(thisConnectionHandler);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
