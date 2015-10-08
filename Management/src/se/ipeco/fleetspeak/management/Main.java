package se.ipeco.fleetspeak.management;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import se.ipeco.fleetspeak.management.gui.FXUtil;
import se.ipeco.fleetspeak.management.gui.ImageLoader;
import se.ipeco.fleetspeak.management.gui.MainController;
import se.ipeco.fleetspeak.management.util.Log;

public class Main extends Application{
	
	private static Application currentApplication;
	
	public Main(){
		super();
		currentApplication = this;
	}

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Fleetspeak server");
        primaryStage.getIcons().add(ImageLoader.loadImage("fleetspeak.png"));
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
        	MainController.disconnect();
        });
        
        FXUtil.switchLayout(primaryStage, "login");
    }

    public static Application getCurrentApplication(){
    	return currentApplication;
    }
    
    public static void main(String[] args) {
    	Log.start();
    	launch();
    }
}
