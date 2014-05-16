
package internetchatclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Represents a chat client.
 * @author Ivan Bahchevanov
 */

public class Client  {
    private InternetChatClient icc;
    private int port;
    private String serverHost;
    private String clientName;
    private ObjectInputStream mInput;
    private ObjectOutputStream mOutput;
    private Socket socket ;    
    
    public Client(String host,int port,String name, InternetChatClient icc) {
        this.serverHost = host;
        this.port = port;
        this.icc = icc;
        this.clientName = name;
    }
    /**
     * 
     * @return      
     */
    protected boolean startClient() {
        
        try {            
            socket = new Socket(serverHost, port);
            
        } catch (Exception e) {
            
            icc.printMessage("!!! can't connect to server...!!!"); 
            return false;
        }
        
        String msg =  socket.getInetAddress() + " : " +  
                socket.getPort() + " <> Connection accepted " ;
        
        icc.printMessage(msg);
        
        try {
            mInput = new ObjectInputStream(socket.getInputStream());
            mOutput = new ObjectOutputStream(socket.getOutputStream());
            
        } catch (Exception e) {
            
            icc.printMessage("!!!error creating the IO streams...!!!");  
            return  false;
        }
        
        new ServerListener().start();
        
        try {
            mOutput.writeObject(clientName);
            
        } catch (Exception e) {
            
            icc.printMessage("!!! can't login... !!!");
            return  false;
        } 
        return true;
    }
    
    protected void sendMessage(String msg) {
        try {
            mOutput.writeObject(msg);
            
        } catch (Exception e) {
            
            icc.printMessage("!!! error sending message to server !!!");
            icc.enableControls();
        }
    }
    
    class ServerListener extends Thread {   
        
        @Override
        public void run() {
            while (true) {                
                try {                    
                    String msg = (String) mInput.readObject();
                    icc.printMessage(msg);
                  
                } catch (IOException | ClassNotFoundException e) {
                    
                    icc.printMessage("!!! error receiving from server...!!!");
                    icc.enableControls();
                    icc.client = null;
                    break;
                }
            }
        }        
    }  
}
   