
package internetchat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Ivan Bahchevanov
 */
public class InternetChatServer {
    public static void main(String[] args) {
        new ServGui();
    }
}

/**
 * this represents the GUI for the server.
 * @author _user
 */
class ServGui extends JFrame implements ActionListener {
    
    private Server server;
    private JTextArea txtEventsArea;
    private JTextArea txtChatArea;
    private JTextField txtPortField;
    private JButton btnStart;
    private JButton btnStop;

    public ServGui() {
        super("Internet Chat : Server");
        JPanel north = new JPanel();
        north.add(new JLabel("Port No:"));
        txtPortField = new JTextField(5);
        txtPortField.addKeyListener(new KeyAdapt());
        north.add(txtPortField);
        btnStart = new JButton("Start");
        btnStart.addActionListener(this);
        north.add(btnStart);
        btnStop = new JButton("Stop");
        btnStop.addActionListener(this);
        north.add(btnStop);
        
        JPanel center = new JPanel(new GridLayout(1, 3));
        txtEventsArea = new JTextArea(60, 60);
        txtEventsArea.setEditable(false);
        center.add(txtEventsArea);
        center.add(new JScrollPane(txtEventsArea));
        txtChatArea = new JTextArea(60, 60);
        txtChatArea.setEditable(false);
        center.add(txtChatArea);
        center.add(new JScrollPane(txtChatArea));        
        
        add(north, BorderLayout.NORTH);
        add(center,BorderLayout.CENTER);  
        
        setFocusable(true);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(600, 400));
        setAlwaysOnTop(true);
        pack();
    }  
    /**
     * this logs every event in the event are
     * @param msg 
     */
    public void writeToEventArea(String msg) {
        String event = msg + "\n" ;
        txtEventsArea.append(event);
        txtEventsArea.setCaretPosition(txtEventsArea.getText().length() - 1);
    }
    
    /**
     * every chat msg is printed in the chat area
     * @param msg 
     */
    public void writeToChatArea(String msg) {
        String event = msg + "\n" ;
        txtChatArea.append(event);
        txtChatArea.setCaretPosition(txtChatArea.getText().length() - 1);
    }
    /**
     * starts and stops the server
     * @param ev 
     */
    @Override
    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource().equals(btnStop)) {
            if (server != null) {   
                txtPortField.setEnabled(true);
                server.stopServer();
                server = null;                
            }   
            return;
        }
        if(server == null) {
            int port;
            try {      
                 port = Integer.parseInt(txtPortField.getText().trim());             

            } catch (Exception e) {  
                return;
            }
            txtPortField.setEnabled(false);
            server = new Server(port, this);
            new ServerThread().start();
        }
    }
    /**
     * this thread runs the server .
     */
    class ServerThread extends Thread {
         public void run() {
             server.startServer();
             
             // the server is stopped now
             server = null;
         }
    }
    /**
     * keylistener 
     */
    class KeyAdapt implements  KeyListener {

        @Override
        public void  keyPressed (KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER ) {
                if ( server == null) {                    
                    int port;
                    try {                              
                         port = Integer.parseInt(txtPortField.getText().trim());    
                    } catch (Exception ex) {  
                        return;
                    }
                    txtPortField.setEnabled(false);
                    server = new Server(port, ServGui.this);
                    new ServerThread().start();
                }           
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {}        

        @Override
        public void keyReleased(KeyEvent e) {}
    }
}




