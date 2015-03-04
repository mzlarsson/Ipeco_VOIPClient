package se.chalmers.fleetspeak;

import android.os.Message;

/**
 * Created by Nieo on 10/10/14.
 * Help class for messages
 * How to use
 * try {
 *      serviceConnection.send(ServerHandler.[command]());
 *  } catch (RemoteException e) {
 *  }

 */
public class ServerHandler {


    private ServerHandler(){}


    public static Message connect(String ip, int port) {
        return Message.obtain(null,SocketService.CONNECT,port,0,ip);

    }

    public static Message disconnect() {
        return Message.obtain(null,SocketService.DISCONNECT,null);
    }


    public static Message setName(String name) {
        return  Message.obtain(null, SocketService.SETNAME, 0,0,name);
    }


    public static Message move(int roomID) {
        return Message.obtain(null,SocketService.MOVE,roomID);
    }

    public static Message moveNewRoom(String name){
        return Message.obtain(null,SocketService.MOVENEWROOM,name);
    }

    public static Message setSoundPort(int remoteUserid, int port) {
        return Message.obtain(null,SocketService.SETSOUNDPORT, port, 0, remoteUserid);
    }


}
