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
            e.printStackTrace();
        }
    }

    /**
     * Starts the listenerthread
     */
    private void startSocketListener(){
        inThread.start();
    }


    /**
     * closes the socket
     */
    private void closeSocket(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket = null;
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
                            Log.i("Connector", "Connecting...");
                            try{
                                socket = new Socket((String)msg.obj, msg.arg1);
                                startSocketListener();
                                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                            }catch(IOException e){
                                Log.i("Connector", "IOException" );
                                e.printStackTrace();
                            }
                            Log.i("Connector", "Connected to" + msg.obj + ":" + msg.arg1);
                            try {
                                msg.replyTo.send(Message.obtain(null, MessageValues.CONNECTED));
                                messenger.send(Message.obtain(null, MessageValues.CONNECTED, new Command("connected", msg.obj, null)));
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            break;
                        case MessageValues.DISCONNECT:
                            sendCommand("closeSocket", null ,null);
                            closeSocket();
                            try {
                                new Messenger(msg.getTarget()).send(Message.obtain(null, MessageValues.DISCONNECTED));
                            } catch (RemoteException e) {
                                e.printStackTrace();
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
                e.printStackTrace();
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
            while(socket != null){
                try {
                    Object o;
                    if((o = objectInputStream.readObject()) != null){
                        messenger.send(Message.obtain(handler, 0, o));
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.d("SocketListener", "EOF exception");
                    closeSocket();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }



}
