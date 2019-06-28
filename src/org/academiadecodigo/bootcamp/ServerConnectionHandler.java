package org.academiadecodigo.bootcamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnectionHandler implements Runnable {

    private Socket clientSocket;
    private String receivedMessage;
    private Server server;
    private boolean serverLive;
    private String nickname = "NewUser"+(int)(Math.random()*3500000+2);

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
            String nicknameBeingSet = null;
            String nicknameBeingWhispered = null;
            String messageToWhisper = "";


            receivedMessage = bReader.readLine();
            System.out.println(receivedMessage);


            //Using this block to manipulate multiparted commands

            switch (receivedMessage.split(" ")[0].toUpperCase()) {

                case "NICKNAME":
                    if (receivedMessage.split(" ").length == 2) {
                        nicknameBeingSet = receivedMessage.split(" ")[1];
                        receivedMessage = "nickname";
                    }
                    break;

                case "WHISPER":
                    nicknameBeingWhispered = receivedMessage.split(" ")[1];
                    for ( int i=2;i<receivedMessage.split(" ").length;i++) {
                        messageToWhisper += receivedMessage.split(" ")[i]+" ";
                    }
                    receivedMessage = "whisper";
                    break;

            }


            switch (receivedMessage.toUpperCase()) {

                case "EXIT":
                    exitCommand();
                    break;

                case "HELP":
                    this.getOutputStream().write(allCommands().getBytes());
                    break;

                case "NICKNAME":
                    changeNickname(nicknameBeingSet);
                    break;

                case "WHISPER":
                    sendMessageToSomeone(nicknameBeingWhispered,messageToWhisper);
                    break;

                default:
                    receivedMessage = nickname + ":" + receivedMessage + "\n";
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
                "Help will show you all the commands\n" +
                "Nickname NAME will change your nickname displayed to NAME\n" +
                "Whisper NAME will send the message to that destination\n";

    }

    public void changeNickname(String newNickname) {

        nickname = newNickname;

    }

    public String getNickname() {
        return nickname;
    }

    public void sendMessageToSomeone(String toWho, String message) throws IOException {

        synchronized (server.getClientList()) {

            for (ServerConnectionHandler c : server.getClientList()) {

                if (c.getNickname().equals(toWho)) {
                    message = nickname+":"+message+"\n";
                    c.getOutputStream().write(message.getBytes());
                }

            }

        }

    }


}
