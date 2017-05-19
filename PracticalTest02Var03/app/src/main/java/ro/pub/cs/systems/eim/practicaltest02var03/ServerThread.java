package ro.pub.cs.systems.eim.practicaltest02var03;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by student on 19.05.2017.
 */

public class ServerThread extends Thread {

    private int port = 0;
    private ServerSocket serverSocket = null;

    private HashMap<String, String> data = null;

    public ServerThread() {}

    public ServerThread(int port) {
        this.port = port;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            Log.e(Constants.TAG, "An exception occurred" + e.getMessage());
            if (Constants.DEBUG) {
                e.printStackTrace();
            }
        }
        data = new HashMap<>();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public synchronized HashMap<String, String> getData() {
        return data;
    }

    public synchronized void setData(String word, String webInformation) {
        this.data.put(word, webInformation);
    }


    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.i(Constants.TAG, "[SERVER] Waiting for a connection...");
                Socket socket = serverSocket.accept();
                Log.i(Constants.TAG, "[SERVER] A new connection request was received from " + socket.getInetAddress() + socket.getLocalPort());
                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();
            }
        } catch (IOException e) {
            Log.e(Constants.TAG, "An exception occurred" + e.getMessage());
            if (Constants.DEBUG) {
                e.printStackTrace();
            }
        }
    }


    public void stopThread() {
        if (serverSocket != null) {
            interrupt();
        }
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            Log.e(Constants.TAG, "An exception occurred" + e.getMessage());
            if (Constants.DEBUG) {
                e.printStackTrace();
            }

        }
    }
}
