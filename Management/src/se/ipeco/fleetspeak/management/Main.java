package se.ipeco.fleetspeak.management;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import se.ipeco.fleetspeak.management.connection.ConnectionListener;
import se.ipeco.fleetspeak.management.connection.ServerHandler;
import se.ipeco.fleetspeak.management.core.Building;
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
        	System.out.println("Wants to close");
        	MainController.disconnect();
        });
        
//        FXUtil.switchLayout(primaryStage, "login");
        sneakIn(primaryStage);
    }
    
	private void sneakIn(Stage stage){
		ServerHandler.connect("46.239.103.195", 8867, "v", "", new ConnectionListener(){

			@Override
			public void onConnect() {
				ServerHandler server = ServerHandler.getConnectedServer();
				Building building = Building.getInstance(server.getUserID());
				server.setCommandHandler(building);
				Platform.runLater(() -> {
					FXUtil.switchLayout(stage, "main");
				});
			}

			@Override
			public void onConnectionFailure(String msg) {
				System.out.println("Failed to connect. Closing.");
			}
		});
    }

    public static Application getCurrentApplication(){
    	return currentApplication;
    }
    
    public static void main(String[] args) {
    	Log.start();
    	launch();
    }
}
