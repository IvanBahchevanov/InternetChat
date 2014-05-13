
package internetchat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Ivan Bahchevanov
 */


public class ServGui extends JFrame implements ActionListener {
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

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource().equals(btnStart)) {
            if ( server == null) {
                try {      
                    
                    int port = Integer.parseInt(txtPortField.getText());  
                    server = new Server(port, this);
                    new ServerThread().start();
                    
                } catch (Exception e) {
                    writeToEventArea("enter port number...");
                }
            }
            
        }
        
        if (ev.getSource().equals(btnStop)) {
            
        }
    }
    
    public void writeToEventArea(String msg) {
        String event = msg + "\n" ;
        txtEventsArea.append(event);
    }
    
    public void writeToChatArea(String msg) {
        String event = msg + "\n" ;
        txtChatArea.append(event);
    }
    
    
    class ServerThread extends Thread {
         public void run() {
             server.startServer();
         }
    }
}

