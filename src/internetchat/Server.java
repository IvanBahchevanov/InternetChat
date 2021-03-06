package internetchat;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class represents a chat server.
 * @author Ivan Bahchevanov
 */
public class Server {

    private int port;
    private int connectionID;
    private boolean running;
    private ArrayList<ClientThread> clientList;
    private SimpleDateFormat date;
    private ServGui servGui;

    public Server(int port, ServGui servGui) {
        running = true;
        this.port = port;
        this.servGui = servGui;
        clientList = new ArrayList<>();
        date = new SimpleDateFormat("HH:mm:ss");
    }

    /**
     * Starts the server, accepts new connections 
     * and starts a new thread for each of it.
     */    
    public void startServer() {

        try {
            // starts the sever
            ServerSocket serverSocket = new ServerSocket(port);
            servGui.writeToEventArea("server started on port: " + port);

            while (running) {

                // wait for new client to connect
                Socket socket = serverSocket.accept();
                if (!running) {
                    break;
                }
                
                String event = socket.getInetAddress() + " : " + socket.getPort()
                        + " connected" ;
                      
                servGui.writeToEventArea(event);

                // start new thread for each new client
                ClientThread ct = new ClientThread(socket);
                clientList.add(ct);
                ct.start();
            }
            servGui.writeToEventArea("server is stopped...");
            
            // running is now false, must close the server            
            try {

                serverSocket.close();

                for (int i = 0; i < clientList.size(); i++) {
                    try {
                        ClientThread ctd = clientList.get(i);
                        ctd.mInput.close();
                        ctd.mOutput.close();
                        ctd.socket.close();

                    } catch (Exception e) {
                    }
                }

            } catch (Exception e) {
                servGui.writeToEventArea(e.toString());
            }

        } catch (Exception e) {
            servGui.writeToEventArea("can't start the server...");
            servGui.writeToEventArea(e.toString());
        }
    }

    /**
     * stops the server and closes the connections.
     */
    public void stopServer() {
        running = false;
        try {
            new Socket("localhost", port);
        } catch (Exception e) {
        }
    }

    /**
     * sends message from a client to all other connected clients.
     * removes all clients who are not connected anymore
     * @param msg the message to broadcast.
     */
    private synchronized void sendToAllClients(String msg) {

        String message = date.format(new Date()) + " " + msg;

        for (int i = 0; i < clientList.size(); i++) {

            ClientThread ctd = clientList.get(i);

            if (!ctd.writeToClient(message)) {
                clientList.remove(i);
                servGui.writeToEventArea(ctd.clientName + " removed from list");
            } else {
                servGui.writeToChatArea(message);
            }
        }
    }

    /**
     * removes any unactive client 
     * @param ind the index of the client's thread in the clientList.
     */
    private synchronized void removeClient(int ind) {

        for (int i = 0; i < clientList.size(); i++) {
            try {
                ClientThread ct = clientList.get(i);
                if (ct.id == ind) {
                    clientList.remove(i);
                    servGui.writeToEventArea(clientList.get(i).clientName + " removed from list");
                    return;
                }
            } catch (Exception e) {
            }

        }
    }    
    
    /**
     * a thread to handle each client.
     */
    class ClientThread extends Thread {

        private Socket socket;
        private ObjectInputStream mInput;
        private ObjectOutputStream mOutput;
        private int id;
        private String clientName;
        String msg;

        public ClientThread(Socket aSocket) {
            this.socket = aSocket;
            id = ++connectionID;
            servGui.writeToEventArea("getting streams from client");
            try {

                mOutput = new ObjectOutputStream(socket.getOutputStream());
                mInput = new ObjectInputStream(socket.getInputStream());
                clientName = (String) mInput.readObject();
                servGui.writeToEventArea(clientName + " signed in...");

            } catch (IOException | ClassNotFoundException e) {
                servGui.writeToEventArea("can't create IO streams...");
            }
        }

        @Override
        public void run() {
            boolean keepRunning = true;

            while (keepRunning) {
                try {
                    msg = (String) mInput.readObject();
                    sendToAllClients("<" + clientName + "> " + "-> " + msg);

                } catch (IOException | ClassNotFoundException e) {
                    servGui.writeToEventArea("error reading message from " + clientName);
                    keepRunning = false;
                }
            }

            removeClient(id);
            closeMe();
        }

        public boolean writeToClient(String msg) {
            if (!socket.isConnected()) {
                closeMe();
                return false;
            }
            try {

                mOutput.writeObject(msg);

            } catch (Exception e) {
                servGui.writeToEventArea("error writing message to " + clientName);
            }
            return true;
        }

        private void closeMe() {
            try {
                mOutput.close();
                mInput.close();
                socket.close();
            } catch (Exception e) {
            }
        }
    }

}
