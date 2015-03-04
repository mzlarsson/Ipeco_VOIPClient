package se.chalmers.fleetspeak;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import se.chalmers.fleetspeak.activities.StartActivity;
import se.chalmers.fleetspeak.util.Command;

/**
 * Created by Nieo on 08/10/14.
 * For handling commands coming from the server.
 * Need to be added as Messenger to socketService to work.
 * Owns the RoomHandler
 */
public class CommandHandler extends Handler {

    private static CommandHandler commandHandler = new CommandHandler();
    private static RoomHandler roomHandler;

    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(new StartActivity());


    //FIXME Not sure where to store id
    private int id;


    private CommandHandler(){
        super();
        roomHandler = new RoomHandler();
    }

    public static CommandHandler getInstance(){
        return commandHandler;
    }

    /**
     * Handles incomming messages form SocketService and informs GUI components
     * @param msg
     */

    public void handleMessage(Message msg) {
        Command command = (Command) msg.obj;
        String sCommand = command.getCommand();
        Log.i("Commandhandler", "Got the command " + sCommand);

        String aCommand = "dataUpdate";

        if(sCommand.equalsIgnoreCase("setID")){
            id =  (Integer)command.getKey();
        }
        else if(sCommand.equalsIgnoreCase("connection failed")){
            aCommand = "connection failed";
        }
        else if(sCommand.equalsIgnoreCase("changedUsername")){
            User u = roomHandler.getUser((Integer) command.getKey());
            u.setName((String)command.getValue());
        }
        else if(sCommand.equalsIgnoreCase("removedUser")){
            roomHandler.removeUser((Integer)command.getKey());
        }
        else if(sCommand.equalsIgnoreCase("movedUser")){
            roomHandler.moveUser((Integer)command.getKey(),(Integer)command.getValue());
        }
        else if(sCommand.equalsIgnoreCase("addedUser")){
            roomHandler.addUser(new User((Integer)command.getKey()), (Integer) command.getValue());
        }
        else if(sCommand.equalsIgnoreCase("createdroom")){
            roomHandler.addRoom((Integer) command.getKey(), (String) command.getValue());
        }
        else if(sCommand.equalsIgnoreCase("removedroom")){
            roomHandler.removeRoom((Integer) command.getKey());
        }
        else if(sCommand.equalsIgnoreCase("Disconnected")){
            roomHandler = new RoomHandler();
            aCommand = "Disconnected";
        }
        else if(sCommand.equalsIgnoreCase("requestsoundport")) {
            //TODO communtication with soundControler
            //TODO should retrun a port for the server to use
        }
        else if(sCommand.equalsIgnoreCase("useSoundPort")){
            //TODO port to send rtp packets to
        }
        else if(sCommand.equalsIgnoreCase("closeSoundPort")){
            //TODO close a port
        }
        else{
            aCommand = "unknown command";
        }

        Intent intent = new Intent("update");
        intent.putExtra("message", aCommand);
        localBroadcastManager.sendBroadcast(intent);

    }

    public static User[] getUsers(int roomID){
        return roomHandler.getUsers(roomID);
    }

    public static Room[] getRooms(){
         return roomHandler.getRooms();
    }






}
