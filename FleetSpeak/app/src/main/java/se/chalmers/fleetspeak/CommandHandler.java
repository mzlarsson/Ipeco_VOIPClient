package se.chalmers.fleetspeak;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;

/**
 * Created by Nieo on 08/10/14.
 */
public class CommandHandler extends Handler {

    public void handleMessage(Message msg) {
        Command c = (Command) msg.obj;
        String s = c.getCommand();
        Log.i("Commandhandler", "Got the message " + s);
        //TODO
    }


}
