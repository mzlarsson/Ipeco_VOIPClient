package se.ipeco.fleetspeak.management.gui;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;

public class MapView extends AnchorPane{
	
	@FXML
	private AnchorPane root;
	@FXML
	private WebView mapView;

	public MapView(){
		FXMLLoader loader = new FXMLLoader(this.getClass().getClassLoader().getResource("mapview.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not load mapview: "+e.getMessage());
		}
	}
	
	public void initialize(){
		mapView.getEngine().onStatusChangedProperty().addListener(new ChangeListener<EventHandler<WebEvent<String>>>(){
			
			@Override
			public void changed(
					ObservableValue<? extends EventHandler<WebEvent<String>>> observable,
							EventHandler<WebEvent<String>> oldValue,
							EventHandler<WebEvent<String>> newValue) {
				System.out.println(oldValue+" --> "+newValue);
			}
			
		});
		String url = getClass().getClassLoader().getResource("mapview.html").toExternalForm();
		mapView.getEngine().load(url);
	}
	
	/**
	 * Moves map to given location (decimal format). Zoom will be reset to 15
	 * @param longitude The longitude
	 * @param latitude The latitude
	 */
	public void moveToLocation(double longitude, double latitude){
		mapView.getEngine().executeScript("moveToLocation("+longitude+","+latitude+", 16)");
	}
	
	public void addPopup(double longitude, double latitude, String text, boolean display){
		mapView.getEngine().executeScript("addPopup("+longitude+","+latitude+",'"+text+"',"+display+")");
	}
	
}
