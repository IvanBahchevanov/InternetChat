
package internetchatclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;;
import java.util.Scanner;

/**
 *
 * @author Ivan Bahchevanov
 */


public class Client  {
    private InternetChatClient icc;
    private int port;
    private String host;
    private String clientName;
    private ObjectInputStream mInput;
    private ObjectOutputStream mOutput;
    private Socket socket ;
    private SimpleDateFormat date;
    
    public Client(String host,int port, String name, InternetChatClient icc) {
        this.host = host;
        this.port = port;
        this.icc = icc;
        this.clientName = name;
        date = new SimpleDateFormat();
    }
    
    public Client(String host,int port, InternetChatClient icc) {
        this.host = host;
        this.port = port;
        this.icc = icc;
        this.clientName = "guest";
        date = new SimpleDateFormat();
    }
    
    protected boolean startClient() {
        
        try {
            
            socket = new Socket(host, port);
            
        } catch (Exception e) {
            icc.printMessage("!!! can't connect to server...!!!");  
            return false;
        }
        
        String msg = "<>Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        icc.printMessage(msg);
        
        try {
            mInput = new ObjectInputStream(socket.getInputStream());
            mOutput = new ObjectOutputStream(socket.getOutputStream());
            
        } catch (Exception e) {
            icc.printMessage("!!!error creating the IO streams...!!!");
            return false;
        }
        
        new ServerListener().start();
        
        try {
            mOutput.writeObject(clientName);
        } catch (Exception e) {
            icc.printMessage("!!! can't login... !!!");
            return false;
        }
        return true;
        
    }
    
//    private void writeEvent(String message) {
//      //  String msg = date.format(new Date()) + " " + message;
//        System.out.println(message);
//    }
    
    protected void sendMessage(String msg) {
        try {
            mOutput.writeObject(msg);
        } catch (Exception e) {
            icc.printMessage("!!! error sending message to server !!!");
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
                    break;
                }
            }
        }        
    }
    
//    public static void main(String[] args) {
//        Scanner scan = new Scanner(System.in);
//    //    String h = scan.nextLine();
//        int p = 65000;
//        String user = "guest";
//        Client client = new Client("localhost", p, user);
//        client.startClient();
//        
//        while (true) {            
//            String msg = scan.nextLine();            
//            client.sendMessage(msg);  
//            if (msg.equalsIgnoreCase("out")) {
//                break;
//            }
//        }        
//    }
}
   