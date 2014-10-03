package se.chalmers.fleetspeak;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Nieo on 01/10/14.
 * For TCP connection to the server
 */
public class Connector{

    private Socket socket;
    private InputStream input;
    private PrintWriter output;
    private final String ip;
    private final int port;
    private boolean isConnected = false;

    public Connector(String ip, int port){
        this.ip = ip;
        this.port = port;
    }
    /**
     * Sends a command to the server
     * @param command
     */
    public void sendCommand(String command){

        if(output != null){
            output.println(command);
            Log.i("Connector", "Sent command: " + command);
        }
    }

    /**
     * tries to etablish a connection to a server

     */
    public void connect(){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ip, port);
                    Log.i("Connector", "Connection established to" + socket.toString());
                    isConnected = true;

                    output = new PrintWriter(socket.getOutputStream(), true);
                    Log.i("Connector", "Outputsteam ready");

                    sendCommand("/mute");
                }catch(IOException e){
                    Log.i("Connector", "Connection failed " + e.getMessage() );
                }
            }
        });
        thread.start();

    }
    public void disconnect(){

    }


    public boolean isConnected() {
        return isConnected;
    }
}
