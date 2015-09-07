package se.chalmers.fleetspeak.newgui;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import se.chalmers.fleetspeak.newgui.connection.ServerHandler;
import se.chalmers.fleetspeak.newgui.core.Building;
import se.chalmers.fleetspeak.util.Log;

public class AdminClient extends Application{
	

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Fleetspeak server");
        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("fleetspeak.png")));
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
        	System.out.println("Wants to close");
        	Building.terminate();
        	ServerHandler.disconnect();
        });
        
        FXUtil.switchLayout(primaryStage, "adminclient");//_login");
    }

    public static void main(String[] args) {
    	Log.start();
    	launch();
    }
}
