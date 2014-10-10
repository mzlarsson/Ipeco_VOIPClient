package se.chalmers.fleetspeak;

import android.os.Message;
import android.view.MotionEvent;

/**
 * Created by Nieo on 10/10/14.
 * Help class for messages
 * try {
 *  Message msg = Message.obtain(SERVERHANDLER.[command]());
 *  mService.send(msg);
 *  } catch (RemoteException e) {
 *  }

 */
public class ServerHandler {


    private ServerHandler(){}


    public static Message connect(String ip, int port) {
        return Message.obtain(null,SocketService.CONNECT,port,0,ip);

    }


    public static Message disconnect(int userID) {
        return Message.obtain(null,SocketService.DISCONNECT,userID);
    }


    public static Message setName(String name, int userID) {
        return  Message.obtain(null, SocketService.SETNAME, userID,0,name);
    }


    public static Message createRoom(String newRoom, int userID) {
        return Message.obtain(null,SocketService.CREATEROOM,userID,0,newRoom);
    }


    public static Message move(int roomID, int userID) {
        return Message.obtain(null,SocketService.MOVEUSER,userID,0,roomID);
    }


    public static Message getRooms() {
        return Message.obtain(null,SocketService.GETROOMS);
    }


    public static Message getUsers(int roomID) {
        return Message.obtain(null, SocketService.GETUSERSINROOM, roomID,0,null);
    }


    public static Message muteUser(int userID) {
        return Message.obtain(null,SocketService.MUTEUSER,userID);
    }
}
