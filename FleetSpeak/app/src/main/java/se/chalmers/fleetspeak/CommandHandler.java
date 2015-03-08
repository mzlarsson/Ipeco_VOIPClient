package se.chalmers.fleetspeak;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import se.chalmers.fleetspeak.util.Command;

/**
 * Created by Nieo on 08/10/14.
 * For handling commands coming from the server.
 * Need to be added as Messenger to socketService to work.
 * Owns the RoomHandler
 */
public class CommandHandler extends Handler {

    private  RoomHandler roomHandler;


    public CommandHandler(RoomHandler roomHandler){
        super();
        this.roomHandler = roomHandler;
    }

     /**
     * Handles incomming messages form SocketService and changes the model
     * @param msg
     */

    public void handleMessage(Message msg) {
        Command command = (Command) msg.obj;
        String sCommand = command.getCommand();
        Log.i("Commandhandler", "Got the command " + sCommand);

        //TODO way to send info to activity

        //TODO implement  spec
        if(sCommand.equals("setID")){
          //TODO where to save id??
        }else if(sCommand.equals("connection failed")){
        }else if(sCommand.equals("setName")){
            User u = roomHandler.getUser((Integer) command.getKey());
            u.setName((String)command.getValue());
        }else if(sCommand.equals("userDisconnected")){
            roomHandler.removeUser((Integer)command.getKey());
        }else if(sCommand.equals("setRtpPort")){
            //Not used
        }else if(sCommand.equals("moveUser")){
            roomHandler.moveUser((Integer)command.getKey(),(Integer)command.getValue());
        }else if(sCommand.equals("newUser")){
            roomHandler.addUser(new User((Integer) command.getKey()));
        }else if(sCommand.equals("addUser")){
            String[] u = ((String) command.getKey()).split(",");
            String[] r = ((String) command.getValue()).split(",");
            roomHandler.addUser(new User(u[0],Integer.parseInt(u[1])), new Room(r[0],Integer.parseInt(r[1])));
        }else if(sCommand.equals("createAndMove")){
            Log.i(this.getClass().toString(), "Create and move user");
            String[] s = ((String) command.getValue()).split(",");
            roomHandler.moveUser(roomHandler.getUser((Integer) command.getKey()), new Room(s[0], Integer.parseInt(s[1])));
        }else if(sCommand.equals("Disconnected")){
        }else{
        }

        Log.d(this.getClass().toString(), roomHandler.toString());


    }






}
