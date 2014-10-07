package se.chalmers.fleetspeak;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Nieo on 01/10/14.
 * For TCP connection to the server
 */
public class Connector{

    private Socket socket;
    private InputStream input;
    private ObjectInputStream ois;
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

        if(isConnected){
            output.println(command);
            Log.i("Connector.sendCommand", "Sent command: " + command);
        }
    }
    public void getData(String command){
        if(isConnected){
            sendCommand(command);
            new getDataListener().execute("");


        }
        Log.i("Connector.getData", "notConnected");

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
                    Log.i("Connector.connect", "Connection established to" + socket.toString());
                    isConnected = true;

                    output = new PrintWriter(socket.getOutputStream(), true);
                    Log.i("Connector.connect", "Outputsteam ready");

                    input = socket.getInputStream();
                    ois = new ObjectInputStream(input);
                    Log.i("Connector.connect", "InputStream ready");

                }catch(IOException e){
                    Log.i("Connector.connect", "Connection failed " + e.getMessage() );
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

    private class getDataListener extends AsyncTask<String,Void,Object>{
        @Override
        protected Object doInBackground(String... params){
            try {
                Object a ;
                a =  ois.readObject();

                if (a != null) {
                    return a;

                }
            }catch(IOException e){
                Log.i("Connector.getData", e.getMessage());
            }catch(ClassNotFoundException e){
                Log.i("Connector.getData", e.getMessage());
            }catch(NullPointerException e){
                Log.i("Connector.getData", e.getMessage());
            }
            return null;

        }
        @Override
        protected void onPostExecute(Object result){
            Log.i("Connector.getData", result.toString());
            //update the GUI here
        }
        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
