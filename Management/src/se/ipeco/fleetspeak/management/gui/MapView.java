package se.ipeco.fleetspeak.management.gui;

import java.io.IOException;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import se.ipeco.fleetspeak.management.Main;

import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import com.sun.javafx.application.HostServicesDelegate;

public class MapView extends AnchorPane{
	
	@FXML
	private AnchorPane root;
	@FXML
	private WebView mapView;
	
	private ReadOnlyBooleanWrapper loadedProperty;
	private int width, height;

	public MapView(int width, int height){
		this.width = width;
		this.height = height;
		loadedProperty = new ReadOnlyBooleanWrapper(false);
		
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
		//Set user agent
		mapView.getEngine().setUserAgent("Ipeco Map Viewer");
		
		//Load map view
		String url = getClass().getClassLoader().getResource("mapview.html").toExternalForm();
		mapView.getEngine().load(url);
		
		//Wait for page to finish load
		mapView.getEngine().getLoadWorker().stateProperty().addListener((ObservableValue<? extends State> observable, State from, State to) -> {
			if(to == State.SUCCEEDED){
				//Add handlers for internal links
				JSObject windowObj = (JSObject)mapView.getEngine().executeScript("window");
				windowObj.setMember("managementApp", new MapViewHandler());
				
				//Load leaflet with correct size
				this.setWidth(width);
				this.setHeight(width);
				mapView.getEngine().executeScript("load("+width+","+height+");");

				//Transform dynamically added leaflet link to internal link standard
				mapView.getEngine().executeScript("replaceLeafletLinkToExternal();");
				
				loadedProperty.set(true);
			}
		});
	}
	
	public boolean isLoaded(){
		return loadedProperty.get();
	}
	
	public ReadOnlyBooleanProperty loadedProperty(){
		return loadedProperty.getReadOnlyProperty();
	}
	
	/**
	 * Moves map to given location (decimal format). Zoom will be reset to 16
	 * @param longitude The longitude
	 * @param latitude The latitude
	 */
	public void moveToLocation(double latitude, double longitude){
		mapView.getEngine().executeScript("moveToLocation("+latitude+","+longitude+", 16)");
	}
	
	public void addCircle(double latitude, double longitude, String text){
		addCircle(latitude, longitude, Color.RED, text);
	}
	
	public void addCircle(double latitude, double longitude, Color color, String text){
		addCircle(latitude, longitude, 100, color, text);
	}
	
	public void addCircle(double latitude, double longitude, int size, Color color, String text){
		mapView.getEngine().executeScript("addCircle("+latitude+","+longitude+","+size+",'"+colorToHex(color)+"','"+text+"');");
	}
	
	public void addPopup(double latitude, double longitude, String text){
		addPopup(latitude, longitude, text, true);
	}
	
	public void addPopup(double latitude, double longitude, String text, boolean display){
		mapView.getEngine().executeScript("addPopup("+latitude+","+longitude+",'"+text+"',"+display+")");
	}
	
	public void clearMap(){
		mapView.getEngine().executeScript("clearMapObjects();");
	}
	
	
	private String colorToHex(Color color){
		return String.format("#%02X%02X%02X", (int)(color.getRed()*255), (int)(color.getGreen()*255), (int)(color.getBlue()*255));
	}
	
	public class MapViewHandler{
		
		public void gotoLink(String link){
			System.out.println("Called: "+link);
			HostServicesDelegate hostServices = HostServicesFactory.getInstance(Main.getCurrentApplication());
			hostServices.showDocument(link);
		}
		
		public void log(String text){
			System.out.println(text);
		}
		
	}
}
