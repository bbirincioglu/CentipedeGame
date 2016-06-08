package com.example.bbirincioglu.centipedegame;

/**
 * The interface implemented by ClientConnectionThread, and ServerConnectionThread
 */
public interface ConnectionThread {
    public static final int STATUS_INITIALIZED = 0;
    public static final int STATUS_WAITING_FOR_SOMEONE_TO_JOIN_GAME = 1;
    public static final int STATUS_SOMEONE_JOINED_GAME = 2;
    public static final int STATUS_CONNECTION_FAILED = 3;
    public static final int STATUS_CONNECTING = 4;
    public static final int STATUS_CONNECTED = 5;

    public void notifyObservers();
    public void addObserver(ConnectionThreadObserver observer);
    public void removeObserver(ConnectionThreadObserver observer);
    public void setCurrentStatus(int currentStatus);
    public int getCurrentStatus();

    public class StatusSender implements Runnable {
        private ConnectionThread connectionThread;
        private int status;

        public StatusSender(ConnectionThread connectionThread, int status) {
            this.connectionThread = connectionThread;
            this.status = status;
        }
        public void run() {
            connectionThread.setCurrentStatus(status);
        }
    }
}
