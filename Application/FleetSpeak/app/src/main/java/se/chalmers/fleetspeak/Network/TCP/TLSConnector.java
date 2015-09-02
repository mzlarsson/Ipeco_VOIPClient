package se.chalmers.fleetspeak.Network.TCP;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;

import se.chalmers.fleetspeak.util.MessageValues;

/**
 * Created by Nieo on 12/08/15.
 */
public class TLSConnector{

    private String LOGTAG = "TLSConnector";

    private SSLSocket socket;
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

    public void sendMessage(Object message){

        try {
            Log.d(LOGTAG, "sending message");
            writeMessenger.send(Message.obtain(null,1,message));
        } catch (RemoteException e) {
            Log.e(LOGTAG, "failed to send message: " + e.getMessage());
        }
    }
    public void disconnect(){
        new SocketDestroyer();
    }

    private class SocketCreator extends AsyncTask<String,  Void, SSLSocket>{

        @Override
        protected SSLSocket doInBackground(String... strings) {
            SSLSocket sslSocket = null;
            if(strings.length == 2){
                try {
                    Log.i(LOGTAG, "trying to connect to " + strings[0] + strings[1]);
                    sslSocket = (SSLSocket) SocketFactory.getSocketFactory().createSocket(strings[0], Integer.parseInt(strings[1]));

                    Log.i(LOGTAG, sslSocket.getSession().getProtocol());

                    Log.i(LOGTAG, "socket created");
                }catch(SSLException e){

                }
                catch (IOException e) {

                }
            }
            return sslSocket;
        }

        @Override
        protected void onPostExecute(SSLSocket sslSocket){
            if(sslSocket != null){

                socket = sslSocket;
                try {
                    socketReader = new SocketReader(sslSocket.getInputStream(), new Messenger(responseHandler));
                    socketWriter = new SocketWriter(sslSocket.getOutputStream());
                    synchronized (socketWriter){
                        socketWriter.wait();
                    }
                    writeMessenger = new Messenger(socketWriter.getHandler());
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
            socketReader.stop();
            try {
                writeMessenger.send(Message.obtain(null,0));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
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
