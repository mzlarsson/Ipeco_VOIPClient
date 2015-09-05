package se.chalmers.fleetspeak.newgui;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import se.chalmers.fleetspeak.util.Log;

public class AdminClient extends Application{
	

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Fleetspeak server");
        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("fleetspeak.png")));
        
        FXUtil.switchLayout(primaryStage, "adminclient_login");
    }

    public static void main(String[] args) {
    	Log.start();
    	launch();
    }
}
