package se.ipeco.fleetspeak.management.gui;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class HomeController {

	@FXML
	private Pane root;
	@FXML
	private Pane mapContainer;
	
	private MapView mapView;
	
	public void initialize(){
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
		MainController.showDisconnectScreen();
	}
}
