package se.ipeco.fleetspeak.management;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import se.ipeco.fleetspeak.management.connection.ServerHandler;
import se.ipeco.fleetspeak.management.core.Building;
import se.ipeco.fleetspeak.management.gui.FXUtil;
import se.ipeco.fleetspeak.management.util.Log;

public class Main extends Application{
	

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Fleetspeak server");
        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("fleetspeak.png")));
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
        	System.out.println("Wants to close");
        	Building.terminate();
        	ServerHandler.disconnect();
        });
        
        FXUtil.switchLayout(primaryStage, "login");
    }

    public static void main(String[] args) {
    	Log.start();
    	launch();
    }
}
