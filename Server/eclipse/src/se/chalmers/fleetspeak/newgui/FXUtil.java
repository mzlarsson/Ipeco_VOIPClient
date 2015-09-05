package se.chalmers.fleetspeak.newgui;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class FXUtil {

	public static void switchLayout(Stage stage, String name) {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FXUtil.class.getClassLoader().getResource(name+".fxml"));
            Pane rootLayout = (Pane) loader.load();
            
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            stage.setScene(scene);
            if(!stage.isShowing()){
            	stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
}
