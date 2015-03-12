package se.chalmers.fleetspeak.gui;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.fxmisc.richtext.StyleClassedTextArea;

public class ServerGUI extends Application implements StageOwner{

    private Stage primaryStage;
    private BorderPane rootLayout;
    
    private ServerGUIController controller;
    
    @Override
    public void start(Stage primaryStage) {
    	final StageOwner gui = this;
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Fleetspeak server");
        this.primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("fleetspeak.png")));
        this.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
			@Override
			public void handle(WindowEvent event) {
				if(controller != null && controller.hasRunningServer()){
					event.consume();
					Popup.showPopup(gui, Popup.Type.YES_NO, Popup.Level.QUESTION,
							"The server is running. Close anyway?",
							new Function<StageOwner>(){
								@Override
								public void perform(StageOwner gui) {
									gui.terminate();
								}
							});
				}
			}
        });
        
        initRootLayout();
        initTerminal();
        initDynamicInfo();
        
        try {
			loadData(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {}
    }
 
    public void loadData(String ip){
    	((Label)(primaryStage.getScene().lookup("#ipLabel"))).setText(ip);
    }
    
    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getClassLoader().getResource("servergui.fxml"));
            rootLayout = (BorderPane) loader.load();
            
            controller = loader.getController();
            controller.setPrimaryStage(this.primaryStage);
            
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void initTerminal(){
    	AnchorPane wrapper = ((AnchorPane)primaryStage.getScene().lookup("#terminalWrapper"));
    	StyleClassedTextArea terminal = new StyleClassedTextArea();
    	terminal.setPrefWidth(wrapper.getPrefWidth());
    	terminal.setPrefHeight(wrapper.getPrefHeight());
    	terminal.setStyle("-fx-background-color: #ffffff;");
    	terminal.setId("terminal");
    	terminal.setWrapText(true);
    	terminal.setEditable(false);
    	terminal.setCache(true);
    	
    	wrapper.getChildren().add(terminal);
    	controller.setTerminal(terminal);
    	primaryStage.getScene().getStylesheets().add(this.getClass().getClassLoader().getResource("terminal.css").toExternalForm());
    }
    
    public void initDynamicInfo(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getClassLoader().getResource("dynamicinfo.fxml"));
            VBox dynamicInfo = (VBox) loader.load();
            
            ((ScrollPane)this.primaryStage.getScene().lookup("#dynamicInfo")).setContent(dynamicInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Stage getPrimaryStage(){
    	return this.primaryStage;
    }
    
    public void terminate(){
    	if(controller != null){
    		controller.closeServer();
    	}
    	this.primaryStage.close();
    }

    public static void main(String[] args) {
    	launch();
    }
}