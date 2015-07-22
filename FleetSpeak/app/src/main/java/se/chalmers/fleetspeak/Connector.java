package se.chalmers.fleetspeak;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.MessageValues;

/**
 * Created by Nieo on 07/03/15.
 * Handles connection with the server. Got 2 threads that handle outgoing and incomming traffic.
 */
public class Connector {



    private SocketWriter socketWriter;
    private SocketReader socketListener;
    private Thread outThread;
    private Thread inThread;

    private Socket socket;
    private Messenger threadMessenger;

    private Messenger messenger;


    private Handler handler = null;

    /**
     * @param h Handler to recieve objects from server
     */
    public Connector(Handler h){
        socketWriter = new SocketWriter();
        outThread = new Thread(socketWriter);
        outThread.start();
        messenger = new Messenger(h);
        socketListener = new SocketReader();
        inThread = new Thread(socketListener);
    }

    /**
     * Connect to server with ip:port
     * @param callbackHandler Handler for callback message
     * @param ip
     * @param port
     */
    public void connect(Handler callbackHandler, String ip, int port){
        if(outThread.getState() == Thread.State.TERMINATED){
            outThread = new Thread(socketWriter);
            outThread.start();
        }
        sendMessage(callbackHandler, MessageValues.CONNECT, ip, port);
    }

    /**
     * Disconnects from the server
     * @param callbackHandler Handler for callback message
     */
    public void disconnect(Handler callbackHandler){
        sendMessage(callbackHandler, MessageValues.DISCONNECT, null, 0);
    }

    /**
     * Sets username to name
     * @param callbackHandler Handler for callback message
     * @param name
     */
    public void setName(Handler callbackHandler, String name){
        sendMessage(callbackHandler, MessageValues.SETNAME, name, 0);
    }

    /**
     * move user to a room
     * @param callbackHandler Handler for callback message
     * @param roomid id of room to move to
     */
    public void move(Handler callbackHandler, int roomid){
        sendMessage(callbackHandler, MessageValues.MOVE, roomid, 0);
    }

    /**
     * make a new room and move there
     * @param callbackHandler Handler for callback message
     * @param roomname name of new room
     */
    public void moveNewRoom(Handler callbackHandler, String roomname){
        sendMessage(callbackHandler, MessageValues.MOVENEWROOM, roomname, 0);
    }

    /**
     * sends a message to the socketwriter
     * @param callbackHandler handler for callbackamessage
     * @param command command to send
     * @param key command key
     * @param value command value
     */
    private void sendMessage(Handler callbackHandler, int command, Object key, int value){
        try{
            Message m = Message.obtain(null, command, value, 0, key);
            m.replyTo = new Messenger(callbackHandler);
            threadMessenger.send(m);
        }catch(RemoteException e){
            Log.d("Connector", "Failed to send message");
        }
    }

    /**
     * Starts the listenerthread
     */
    private void startSocketListener(){
        if(inThread.isAlive()){
          closeSocket();

        }
        if(Thread.State.TERMINATED == inThread.getState())
            inThread = new Thread(socketListener);
        Log.d("Threadstate", inThread.getState() + "");
        if(!inThread.isAlive())
            inThread.start();


    }


    /**
     * closes the socket
     */
    private void closeSocket(){
        try {
            socketListener.close();
            socketWriter.close();
            if(socket != null)
                socket.close();
        } catch (IOException e) {

        }
    }

    /**
     *Handles incomming messages and sends commands to the server
     */
    private class SocketWriter implements Runnable{


        ObjectOutputStream objectOutputStream;

        @Override
        public void run() {
            Looper.prepare();

            handler = new Handler() {
                public void handleMessage(Message msg) {
                if (msg != null) {
                    Log.i("Connector", "Command received. id: " + msg.toString());

                    switch (msg.what) {
                        case MessageValues.CONNECT:
                            if(socket != null){
                                closeSocket();
                            }
                            boolean connected = false;
                            Log.i("Connector", "Connecting...");
                            try{
                                socket = new Socket();
                                socket.connect(new InetSocketAddress((String)msg.obj, msg.arg1), 10000);
                                startSocketListener();
                                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                                connected = true;
                            }catch(IOException e){
                                Log.d("Connector", "IOException connection failed");
                            }

                            try {
                                if(connected) {
                                    Log.i("Connector", "Connected to" + msg.obj + ":" + msg.arg1);
                                    msg.replyTo.send(Message.obtain(null, MessageValues.CONNECTED));
                                    messenger.send(Message.obtain(null, MessageValues.CONNECTED, new Command("connected", msg.obj, null)));
                                }else{
                                    Log.i("Connector", "Failed to connect");
                                    msg.replyTo.send(Message.obtain(null, MessageValues.CONNECTIONFAILED));
                                    messenger.send(Message.obtain(null, MessageValues.CONNECTIONFAILED, new Command("connectionfailed", msg.obj, null)));
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            break;
                        case MessageValues.DISCONNECT:
                            sendCommand("disconnect", null ,null);
                            closeSocket();
                            try {
                                new Messenger(msg.getTarget()).send(Message.obtain(null, MessageValues.DISCONNECTED));
                            } catch (RemoteException e) {
                                Log.d("Connector", "failed to close socket");
                            }
                            break;
                        case MessageValues.SETNAME:
                            sendCommand("setname", msg.obj, null);
                            break;
                        case MessageValues.MOVENEWROOM:
                            sendCommand("movenewroom", msg.obj, null);
                            break;
                        case MessageValues.MOVE:
                            sendCommand("move", msg.obj, null);
                            break;
                        case MessageValues.SETSOUNDPORT:
                            sendCommand("setsoundport", msg.obj, msg.arg1);
                            break;
                        case MessageValues.AUTHENTICATIONDETAILS:
                            sendCommand("authenticationdetails", msg.obj, null);
                            break;
                        default:
                            break;
                    }
                }
                }
            };
            threadMessenger = new Messenger(handler);
            Looper.loop();
        }

        void sendCommand(String command, Object key, Object value){
            try {
                objectOutputStream.writeObject(new Command(command, key, value));
            } catch (IOException e) {
                Log.d("Connector", "IOException while trying to send command");
            } catch (NullPointerException e){
                Log.d("Connector", "No stream failed to send command");
            }
        }
        void close(){
            try {
                if(objectOutputStream != null)
                    objectOutputStream.close();
            } catch (IOException e) {
                Log.d("Connector", "Error while closing stream");
            }
        }
    }

    /**
     *Reads objects from the server and sends them to a Handler
     */

    private class SocketReader implements Runnable{
        ObjectInputStream objectInputStream;
        public SocketReader(){}
        @Override
        public void run() {
            try {
                objectInputStream = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            while(objectInputStream != null){
                try {
                    Object o;
                    if((o = objectInputStream.readObject()) != null){
                        Message msg = Message.obtain(handler, 0, o);
                        msg.replyTo = threadMessenger;
                        messenger.send(msg);

                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("SocketListener", e.getClass()+": "+e.getMessage());
                    closeSocket();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        void close(){
            if(objectInputStream != null) {
                try {

                    objectInputStream.close();
                    objectInputStream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



}
