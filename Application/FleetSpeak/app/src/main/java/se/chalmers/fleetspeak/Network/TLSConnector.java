package se.chalmers.fleetspeak.Network;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;

import se.chalmers.fleetspeak.util.Command;

/**
 * Created by Nieo on 12/08/15.
 */
public class TLSConnector{

    private String LOGTAG = "TLSConnector";

    private SSLSocket socket;
    private Messenger responseMessenger;
    private Messenger writeMessenger;

    private SocketWriter socketWriter;
    private SocketReader socketReader;
    public void setNewHandler(Handler handler){
        responseMessenger = new Messenger(handler);
    }

    public TLSConnector(Handler handler){
        responseMessenger = new Messenger(handler);


    }

    public void setResponseMessenger(Handler handler){
        responseMessenger = new Messenger(handler);
    }

    public void connect(String ip,  int port){
        new SocketCreator().execute(ip, ""+port);
    }

    public void sendMessage(Object message){

        try {
            Log.d(LOGTAG, "sening message");
            writeMessenger.send(Message.obtain(null,1,message));
        } catch (RemoteException e) {
            Log.e(LOGTAG, "failed to send message: " + e.getMessage());
        }
    }
    public void disconnect(){

    }

    private class SocketCreator extends AsyncTask<String,  Void, SSLSocket>{

        @Override
        protected SSLSocket doInBackground(String... strings) {
            SSLSocket sslSocket = null;
            if(strings.length == 2){
                try {
                    sslSocket = (SSLSocket) SocketFactory.getSocketFactory().createSocket(strings[0], Integer.parseInt(strings[1]));

                    Log.i(LOGTAG, sslSocket.getSession().getProtocol());

                    Log.i(LOGTAG, "socket created");
                }catch(SSLException e){
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sslSocket;
        }

        @Override
        protected void onPostExecute(SSLSocket sslSocket){
            if(sslSocket != null){

                socket = sslSocket;
                try {
                    socketReader = new SocketReader(sslSocket.getInputStream(), responseMessenger);
                    socketWriter = new SocketWriter(sslSocket.getOutputStream());
                    synchronized (socketWriter){
                        socketWriter.wait();
                    }
                    writeMessenger = new Messenger(socketWriter.getHandler());
                    responseMessenger.send(Message.obtain(null,0,new Command("connected",sslSocket.getInetAddress().toString(),null)));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        }
    }



}
