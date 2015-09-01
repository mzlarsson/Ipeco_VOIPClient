package se.chalmers.fleetspeak.util;

import android.os.Handler;

import javax.security.auth.callback.CallbackHandler;

import se.chalmers.fleetspeak.Model;

/**
 * Created by David Gustafsson on 2015-08-31.
 */
public class ModelFactory {
    private static Model model;
    public static Model getModel(Handler handler){
        if(model == null){
            model = new Model(handler);
        }else{
            model.setNewHandler(handler);
        }
        return model;
    }
}
