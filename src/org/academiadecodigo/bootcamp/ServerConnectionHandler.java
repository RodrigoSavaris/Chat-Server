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
    private boolean isAdmnistrator;
    private BufferedReader bReader;

    public ServerConnectionHandler(Socket clientSocket, Server server) {
        serverLive = true;
        this.clientSocket = clientSocket;
        this.server = server;
        isAdmnistrator = false;
    }

    @Override
    public void run() {
        while (serverLive) {
            receiveMessage();
        }
    }

    public void receiveMessage() {

        bReader = null;
        try {
            bReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String nicknameBeingSet = null;
            String nicknameBeingWhispered = null;
            String messageToWhisper = "";


            receivedMessage = bReader.readLine();
            System.out.println(receivedMessage);

            switch (receivedMessage.split(" ")[0].toUpperCase()) {

                case "/EXIT":
                    exitCommand();
                    break;

                case "/HELP":
                    this.getOutputStream().write(allCommands().getBytes());
                    break;

                case "/NICKNAME":
                    nicknameBeingSet = receivedMessage.split(" ")[1];
                    changeNickname(nicknameBeingSet);
                    break;

                case "/WHISPER":
                    nicknameBeingWhispered = receivedMessage.split(" ")[1];
                    for ( int i=2;i<receivedMessage.split(" ").length;i++) {
                        messageToWhisper += receivedMessage.split(" ")[i]+" ";
                    }
                    sendMessageToSomeone(nicknameBeingWhispered,messageToWhisper);
                    break;

                case "/LIST":
                    listUsers();
                    break;

                case "/ADM":
                    if (receivedMessage.split(" ")[1].toUpperCase().equals("SENHA")) {
                        isAdmnistrator = true;
                        this.getOutputStream().write(("You now are an administrator\n").getBytes());
                    }
                    break;

                case "/KICK":
                    if (isAdmnistrator) {
                        kick(receivedMessage.split(" ")[1]);
                    } else {
                        this.getOutputStream().write("You are not an administrator\n".getBytes());
                    }
                    break;

                default:
                    receivedMessage = nickname + ": " + receivedMessage + "\n";
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

        synchronized (server.getClientList()) {

            serverLive = false;
            server.getClientList().remove(this);
            bReader.close();
            this.getOutputStream().close();
            this.clientSocket.close();

        }

    }

    public String allCommands() {

        return "The operational commands of the server are:\n" +
                "/Exit will let you exit the chat\n" +
                "/Help will show you all the commands\n" +
                "/Nickname NAME will change your nickname displayed to NAME\n" +
                "/Whisper NAME will send the message to that destination\n" +
                "/List will show all nicknames of the users connected\n" +
                "/Kick NAME will kick the user with such name from the channel. Only available to administrators\n";

    }

    public void changeNickname(String newNickname) throws IOException {

        nickname = newNickname;
        this.getOutputStream().write(("Your nickname has been changed to: "+nickname+"\n").getBytes());

    }

    public String getNickname() {
        return nickname;
    }

    public void sendMessageToSomeone(String toWho, String message) throws IOException {

        synchronized (server.getClientList()) {

            for (ServerConnectionHandler c : server.getClientList()) {

                if (c.getNickname().equals(toWho)) {
                    message = "(Whisper) " + nickname + ": " + message + "\n";
                    c.getOutputStream().write(message.getBytes());
                }

            }

        }

    }

    public void listUsers() throws IOException {

        synchronized (server.getClientList()) {
            String nicknamesOnline = "";

            for (ServerConnectionHandler c : server.getClientList()) {
                nicknamesOnline += c.getNickname()+" ";

            }

            this.getOutputStream().write((nicknamesOnline+"\n").getBytes());

        }
    }

    public void kick(String toWho) throws IOException {

        synchronized (server.getClientList()) {

            for (ServerConnectionHandler c : server.getClientList()) {

                if (c.getNickname().equals(toWho)) {

                    c.exitCommand();
                    c.getOutputStream().write(("You kicked the user "+toWho+"\n").getBytes());

                }

            }

        }
    }

}
