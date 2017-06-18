/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClient extends Thread{
    
    protected BufferedReader in;
    protected PrintWriter out;
    private JTextField messageField;
    private JTextArea chatBox;
    
    private DataBase db;
    private String serverAddress;
    private int port;
    private String userName = null;
    private int Id;
    
    public ChatClient(String address, int port, JTextField messageField, JTextArea chatBox){
        
        db = DataBase.getDataBase();
        
        setServerAddress(address);
        setPort(port);
        
        this.messageField = messageField;
        this.chatBox = chatBox;
        
        //insert chat history to message box
        for(String message : db.getChatHistory()){
            this.chatBox.append(message + "\n");
        }
        
        messageField.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                out.println(messageField.getText());
                messageField.setText("");
            }
            
        });
    }
    
    private void setServerAddress(String address){
        this.serverAddress = address;
    }
    
    public String getServerAddress(){
        return this.serverAddress;
    }
    
    private void setPort(int port){
        this.port = port;
    }
    
    private void setUserName(String userName){
        this.userName = userName;
    }
    
    public String getUserName(){
        return this.userName;
    }
    
    private String inputUserName() {
        return JOptionPane.showInputDialog(
            null,
            "Choose a screen name:",
            "Screen name selection",
            JOptionPane.PLAIN_MESSAGE);
    }

    
    @Override
    public void run(){
        try{
            Socket socket = new Socket(serverAddress, port);
            
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            
            while(true){
                String line = in.readLine();
                if(line.startsWith("SUBMITNAME")){
                    while(this.userName == null)
                        setUserName(inputUserName()); //this will loop until valid username is entered
                    
                    //go here if valid username is finally entered
                    //Add user to database
                    if(db.addUser(this.userName)){
                        this.Id = db.getSessionId();
                        JOptionPane.showMessageDialog(null, "Succesfully added user!", "New user success", JOptionPane.PLAIN_MESSAGE);
                        out.println(this.userName);
                    }
                } else if (line.startsWith("NAMEACCEPTED")){
                    messageField.setEditable(true);
                } else if(line.startsWith("MESSAGE")){
                    String message = line.substring(8);
                    chatBox.append(message + "\n");
                    db.logMessage(this.Id, message.substring(message.lastIndexOf(':') + 1));
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
