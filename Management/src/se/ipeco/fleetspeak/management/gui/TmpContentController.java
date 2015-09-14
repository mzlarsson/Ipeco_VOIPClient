package se.ipeco.fleetspeak.management.gui;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import se.ipeco.fleetspeak.management.connection.ServerHandler;
import se.ipeco.fleetspeak.management.core.Building;

public class TmpContentController {

	@FXML
	private Pane root;
	@FXML
	private Pane mapContainer;
	
	private MapView mapView;
	
	public TmpContentController(){
		System.out.println("TMP CONTENT");
	}
	
	public void initialize(){
		System.out.println("Hello");
		mapView = new MapView(650, 450);
		mapContainer.getChildren().add(mapView);
		mapView.loadedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean wasLoaded, Boolean isLoaded) -> {
			if(isLoaded){
				mapView.addPopup(57.687325, 11.978694, "Idegr6");
			}
		});
	}

	public void clearMap(){
		mapView.clearMap();
	}
	
	public void goToMatz(){
		mapView.moveToLocation(57.680830, 11.985843);
		mapView.addCircle(57.680830, 11.985843, 20, Color.RED, "H&auml;r bor Matz!");
	}
	
	public void changeView(){
		MainController.setContent(null);
	}
	
	public void disconnect(){
		System.out.println("Disconnecting.");
		ServerHandler.disconnect();
		Building.terminate();
		FXUtil.switchLayout((Stage)root.getScene().getWindow(), "login");
	}
}
