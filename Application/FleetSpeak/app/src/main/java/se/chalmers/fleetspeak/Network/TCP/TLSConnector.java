package se.chalmers.fleetspeak.Network.TCP;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;

import se.chalmers.fleetspeak.util.MessageValues;

/**
 * Created by Nieo on 12/08/15.
 */
public class TLSConnector{

    private String LOGTAG = "TLSConnector";

    private Socket socket;
    private Handler responseHandler;
    private Messenger writeMessenger;

    private SocketWriter socketWriter;
    private SocketReader socketReader;

    public TLSConnector(Handler handler){
        responseHandler = handler;


    }

    //TODO change to username, password and use a static IP,port
    public void connect(String ip,  int port){
        new SocketCreator().execute(ip, ""+port);
    }

    public void sendMessage(String message){
        try {
            Log.d(LOGTAG, "sending message " + message);
            writeMessenger.send(Message.obtain(null,1,message));
        } catch (RemoteException e) {
            Log.e(LOGTAG, "failed to send message: " + e.getMessage());
        }
    }
    public void disconnect(){
        new SocketDestroyer().execute();
    }

    private class SocketCreator extends AsyncTask<String,  Void, Socket>{

        @Override
        protected Socket doInBackground(String... strings) {
            Socket sslSocket = null;
            if(strings.length == 2){
                Log.i(LOGTAG, "trying to connect to " + strings[0] + strings[1]);
                sslSocket =  SocketFactory.getSSLSocket(strings[0], Integer.parseInt(strings[1]));
                Log.i(LOGTAG, "socket created");
            }
            return sslSocket;
        }

        @Override
        protected void onPostExecute(Socket sslSocket){
            if(sslSocket != null){

                socket = sslSocket;
                try {
                    socketReader = new SocketReader(sslSocket.getInputStream(), new Messenger(responseHandler));
                    socketWriter = new SocketWriter(sslSocket.getOutputStream(), responseHandler);
                    synchronized (socketWriter){
                        socketWriter.wait();
                    }
                    writeMessenger = new Messenger(socketWriter.getWriterHandler());
                    responseHandler.sendEmptyMessage(MessageValues.CONNECTED);
                } catch (IOException e) {

                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }else{
                Log.i(LOGTAG, "Failed to connect");
                responseHandler.sendEmptyMessage(MessageValues.CONNECTIONFAILED);
            }
        }
    }
    private class SocketDestroyer extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(LOGTAG, "Stopping reader");
            socketReader.stop();
            try {
                writeMessenger.send(Message.obtain(null,0));
                Log.d(LOGTAG, "Stopping writer");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                Log.d(LOGTAG, "trying to close socket");
                socket.close();
                Log.d(LOGTAG, "Closed socket");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public String getIP(){
        if(socket != null){
            return socket.getInetAddress().getHostAddress();
        }
        return null;
    }


}
