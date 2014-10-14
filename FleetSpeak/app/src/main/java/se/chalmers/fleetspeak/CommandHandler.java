package se.chalmers.fleetspeak;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import se.chalmers.fleetspeak.util.Command;

/**
 * Created by Nieo on 08/10/14.
 */
public class CommandHandler extends Handler {

    private RoomHandler roomHandler;


    public CommandHandler(){
        super();
        roomHandler = new RoomHandler();
    }

    public void handleMessage(Message msg) {
        Command c = (Command) msg.obj;
        String s = c.getCommand();
        Log.i("Commandhandler", "Got the message " + s);
        //TODO
    }


}
