package se.chalmers.fleetspeak.TCP;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import se.chalmers.fleetspeak.util.Command;

/**
 * Created by Nieo on 07/03/15.
 * Handles data connection with the server.
 */
public class Connector  implements IConnector{



    private StreamWriter streamWriter;
    private Messenger writerMessenger;
    private StreamReader streamReader;
    private Socket mySocket;
    private Messenger responseMessenger;
    /**
     * @param handler Handler to recieve objects from server
     */
    public Connector(Handler handler){
        responseMessenger = new Messenger(handler);
    }

    /**
     *  Separate Thread for trying to create a socket
     *  If its successful it will start a reader and a writer
     *  Sends result to
     */
    private class socketCreator extends AsyncTask<String, Void, Socket> {

        @Override
        protected Socket doInBackground(String... strings) {
            Socket socket = null;
            if(strings.length >= 2) {
                try {
                    socket = new Socket(strings[0], Integer.parseInt(strings[1]));
                } catch (ConnectException e){
                    try {
                        responseMessenger.send(Message.obtain(null, 0, new Command("connectionfailed", null, null)));
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return socket;
        }

        @Override
        protected void onPostExecute(Socket socket) {
            if(socket != null) {
                mySocket =  socket;
                try {
                    streamReader = new StreamReader(socket.getInputStream(), responseMessenger);
                    streamWriter = new StreamWriter(socket.getOutputStream());
                    synchronized (streamWriter) {
                        streamWriter.wait();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    writerMessenger = new Messenger(streamWriter.getMyHandler());
                    responseMessenger.send(Message.obtain(null, 0, new Command("connected", socket.getInetAddress().toString(), null)));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    /**
     * For closing the socket in a separate thread
     *
     */

    private class socketCloser extends AsyncTask<Socket, Void, Void>{
        @Override
        protected Void doInBackground(Socket... sockets) {
            try {
                writerMessenger.send(Message.obtain(null, 1));
                streamReader.stop();
                sockets[0].close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    /**
     * Connect to server with ip:port
     * @param ip
     * @param port
     */
    public void connect(String ip, int port){
        new socketCreator().execute(ip, ""+ port);
    }

    /**
     * Closes the current connection
     */

    public void disconnect(){
        new socketCloser().execute(mySocket);
    }


    /**
     * sends a command to the connected server
     * @param command command to send
     */
    public void sendMessage(Object command){
        try{
            Message m = Message.obtain(null, 0,command);
            writerMessenger.send(m);
        }catch(RemoteException e){
            Log.d("Connector", "Failed to send message");
        }
    }
}
