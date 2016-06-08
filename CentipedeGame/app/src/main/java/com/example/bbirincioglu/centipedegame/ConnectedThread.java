package com.example.bbirincioglu.centipedegame;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.Message;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * The class which communicates (reads from socket, and writes into socket) with the other device using bluetoothSocket, input and output streams.
 */
public class ConnectedThread extends Thread {
    private Activity activity;
    private GamePlayActivity.MessageHandler messageHandler;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public ConnectedThread(Activity activity, BluetoothSocket bluetoothSocket, GamePlayActivity.MessageHandler messageHandler) {
        setActivity(activity);
        setMessageHandler(messageHandler); //MessageHandler which recevies messages from this thread and sends them to main thread.
        setBluetoothSocket(bluetoothSocket);
        InputStream temp1 = null;
        OutputStream temp2 = null;

        try {
            temp1 = bluetoothSocket.getInputStream(); //Get input stream from socket for reading.
            temp2 = bluetoothSocket.getOutputStream(); //Get output stream from socket for writing.
        } catch (Exception e) {
            e.printStackTrace();
        }

        inputStream = temp1;
        outputStream = temp2;
    }

    //This method constantly checkes whether anything is written to socket, and read them if any. The messages that are read by the input stream
    // are of type bytes. This message has to be sent to main thread in order for it to be displayed in GUI or for other processes. Thus, we use
    //message handler to send message from this thread to main thread.
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;

        while (true) {
            try {
                bytes = getInputStream().read(buffer);
                Message message = Message.obtain();
                message.arg1 = bytes;
                message.obj = buffer;
                getMessageHandler().sendMessage(message);
            } catch (Exception e) {
                System.out.println("CONNECTED THREAD READ EXCEPTION: then break happened.");
                e.printStackTrace();
                break;
            }
        }
    }

    //Write in to socket using output stream. If message to be written is of type String, just convert it into byte array. If it is of type HashMap (I used
    //this data structure for sending Game Settings from Hosted Player's phone to Client Player phone), use object output stream to convert it into byte array.
    public void write(Object message) {
        System.out.println("IT IS IN THE WRITE.");
        byte[] messageAsByteArray = null;

        if (message instanceof String) {
            String messageAsString = (String) message;
            messageAsByteArray = messageAsString.getBytes();
        } else if (message instanceof HashMap) {
            HashMap<String, String> messageAsGameSettings = (HashMap<String, String>) message;

            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(messageAsGameSettings);
                objectOutputStream.close();
                messageAsByteArray = byteArrayOutputStream.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            getOutputStream().write(messageAsByteArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelConnection() {
        try {
            getBluetoothSocket().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public GamePlayActivity.MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(GamePlayActivity.MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
}
