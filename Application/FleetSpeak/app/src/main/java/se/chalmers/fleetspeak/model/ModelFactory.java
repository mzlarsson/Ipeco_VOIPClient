package se.chalmers.fleetspeak.model;

import android.os.Handler;
import android.util.Log;

/**
 * Created by David Gustafsson on 2015-08-31.
 */
public class ModelFactory {
    private static Model model;

    public static Model getCurrentModel(){
        if(model != null) {
            return model;
        }else{
            throw new IllegalStateException("Invalid call: No model has been started yet. (your code iz broken)");
        }
    }

    public static Model getModel(Handler handler){
        if(model == null){
            Log.d("ModelFactory", " Creating new Model");
            model = new Model(handler);
        }else{
            Log.d("ModelFactory", "Setting a new Handler to the model");
            model.setNewHandler(handler);
        }
        return model;
    }
}
