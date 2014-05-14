
package internetchatclient;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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

public class InternetChatClient extends JFrame implements ActionListener{
    
    private Client client;
    private JTextArea txtMsgArea;
    private JTextField txtServer;
    private JTextField txtPort;
    private JTextField txtMsgField;
    private JTextField txtName;
    private JButton btnEnter;
    private JButton btnSubmit;
    
    public InternetChatClient() {
        super("Internet Chat : Client");
        initGui();
    }
    
    public void printMessage(String msg) {
        String message = msg + "\n";
        txtMsgArea.append(message);
        txtMsgArea.setCaretPosition(txtMsgArea.getText().length() - 1);
    }

    public static void main(String[] args) {
        new InternetChatClient();
    }
    
    private void initGui() {        
        JPanel north = new JPanel();
        txtServer = new JTextField(15);
        txtServer.setText("127.0.0.1");
        north.add(new JLabel("Server:"));
        north.add(txtServer);
        txtPort = new JTextField(5);
        txtPort.setText("65000");
        north.add(new JLabel("Port No:"));
        north.add(txtPort);
        north.add(new JLabel("User Name:"));
        txtName = new JTextField(5);
        txtName.setText("Guest");
        north.add(txtName);
        btnEnter = new JButton("Enter Chat");
        btnEnter.addActionListener(this);
        north.add(btnEnter);
        
        JPanel south = new JPanel();
        txtMsgField = new JTextField(45);
        txtMsgField.addKeyListener(new keyListener());
        south.add(txtMsgField);
        btnSubmit = new JButton("Submit");
        btnSubmit.addActionListener(this);
        south.add(btnSubmit);
        
        JPanel center = new JPanel(new GridLayout(2, 1));
        txtMsgArea = new JTextArea(30, 40);
        txtMsgArea.setEditable(false);
        center.add(txtMsgArea);
        center.add(new JScrollPane(txtMsgArea));
        center.add(south);
        
        add(north, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
       // add(south, BorderLayout.SOUTH);
        
        addKeyListener(new keyListener());
        setPreferredSize(new Dimension(600, 400));
        setFocusable(true);
        setVisible(true);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        
        if (ev.getSource().equals(btnEnter)) {
            if (client == null) {
                int port;
                String host;
                String name;
                try {
                    host = txtServer.getText();
                    port = Integer.parseInt(txtPort.getText());
                    name = txtName.getText();
                } catch (Exception e) {
                    return;
                }
                txtServer.setEditable(false);
                txtPort.setEditable(false);
                txtName.setEditable(false);
                btnEnter.setEnabled(false);
                client = new Client(host, port, name, this);
                client.startClient();
            }
            
        }
        
        if (ev.getSource().equals(btnSubmit)) {
            if (client != null) {
                String msg;
                try {
                    msg = txtMsgField.getText().trim();
                    txtMsgField.setText("");
                } catch (Exception e) {
                    return;
                }
                if (! msg.equals("")) {
                    client.sendMessage(msg);
                }                
            }
        }
    }
    
    class keyListener implements KeyListener {
        
        @Override
        public void keyPressed(KeyEvent ev) {
            if (ev.getKeyCode() == KeyEvent.VK_ENTER) {
                if (client != null ) {
                    String msg;
                    try {
                        msg = txtMsgField.getText().trim();
                        txtMsgField.setText("");
                    } catch (Exception e) {
                        return;
                    }
                    if (! msg.equals("")) {
                        client.sendMessage(msg);
                    }
                }
                else {
                    int port;
                    String host;
                    String name;
                    try {
                        host = txtServer.getText();
                        port = Integer.parseInt(txtPort.getText());
                        name = txtName.getText();
                    } catch (Exception e) {
                        return;
                    }
                    txtServer.setEditable(false);
                    txtPort.setEditable(false);
                    txtName.setEditable(false);
                    client = new Client(host, port, name, InternetChatClient.this);
                    client.startClient();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent ev) {}
        
        @Override
        public void keyTyped(KeyEvent ev) {}    
    }

}


