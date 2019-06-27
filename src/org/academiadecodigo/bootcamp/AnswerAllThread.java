package org.academiadecodigo.bootcamp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

public class AnswerAllThread implements Runnable{




    public void respond(OutputStream clientStream, byte[] receivedMessage) {

        try {
            OutputStream outputStream = clientStream;

            byte[] messageData = receivedMessage;

            outputStream.write(messageData);
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    @Override
    public void run() {

    }
}
