/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

/**
 *
 * @author gsm
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

/**
 * A multithreaded chat room server.  When a client connects the
 * server requests a screen name by sending the client the
 * text "SUBMITNAME", and keeps requesting a name until
 * a unique one is received.  After a client submits a unique
 * name, the server acknowledges with "NAMEACCEPTED".  Then
 * all messages from that client will be broadcast to all other
 * clients that have submitted a unique screen name.  The
 * broadcast messages are prefixed with "MESSAGE ".
 *
 * Because this is just a teaching example to illustrate a simple
 * chat server, there are a few features that have been left out.
 * Two are very useful and belong in production code:
 *
 *     1. The protocol should be enhanced so that the client can
 *        send clean disconnect messages to the server.
 *
 *     2. The server should do some logging.
 */
public class ChatServer {

    /**
     * The port that the server listens on.
     */
    private static int PORT;

    /**
     * The application server, which just listens on a port and
     * spawns handler threads.
     * @param port
     * @param serverClient
     * @throws java.io.IOException
     */
    public ChatServer(int port, ChatClient hostClient) throws IOException{
        System.out.println("The chat server is running.");
        
        setPort(port);
        ServerSocket listener = new ServerSocket(PORT);
        
        try {
            hostClient.start();
            new Handler(listener.accept()).start();
        } finally {
            listener.close();
        }
    }
    
    private void setPort(int port){
        this.PORT = port;
    }
    
    public int getPort(){
        return this.PORT;
    }
}