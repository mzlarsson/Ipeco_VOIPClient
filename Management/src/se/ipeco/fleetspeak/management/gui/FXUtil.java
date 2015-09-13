package se.ipeco.fleetspeak.management.gui;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXUtil {

	public static void switchLayout(Stage stage, String name) {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FXUtil.class.getClassLoader().getResource(name+".fxml"));
            Parent rootLayout = loader.load();
            
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            stage.setScene(scene);
            if(!stage.isShowing()){
            	stage.show();
            }
            
//            stage.setResizable(false);
        	stage.sizeToScene();
        	stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
}
