package se.chalmers.fleetspeak.gui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Popup {
	
	public static void alert(StageOwner gui, Level level, String msg){
		showPopup(gui, Type.OK, level, msg, null);
	}

	public static void showPopup(StageOwner gui, Type type, Level level, String msg, Function<StageOwner> onAccept){
		showPopup(gui, type, level, msg, onAccept, null);
	}
	
	public static void showPopup(StageOwner gui, Type type, Level level, String header, String msg, Function<StageOwner> onAccept){
		showPopup(gui, type, level, header, msg, onAccept, null);
	}
	
	public static void showPopup(final StageOwner gui, Type type, Level level, String msg, final Function<StageOwner> onAccept, final Function<StageOwner> onDecline){
		showPopup(gui, type, level, level.getTextualRepresentation(), onAccept, onDecline);
	}
	
	public static void showPopup(final StageOwner gui, Type type, Level level, String header, String msg, final Function<StageOwner> onAccept, final Function<StageOwner> onDecline){
        try {
        	final Stage dialog = new Stage();
        	dialog.setTitle(level.name());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(gui.getPrimaryStage());
            dialog.setOnCloseRequest(new EventHandler<WindowEvent>(){
				@Override
				public void handle(WindowEvent event) {
					if(onDecline != null){
						onDecline.perform(gui);
					}
				}
            });

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(gui.getClass().getClassLoader().getResource("popup_"+type.name().toLowerCase()+".fxml"));
            AnchorPane popup = (AnchorPane) loader.load();
            
            ImageView levelLabelIcon = (ImageView)popup.lookup("#messageLevelIcon");
            if(levelLabelIcon != null){
            	levelLabelIcon.setImage(new Image(gui.getClass().getClassLoader().getResourceAsStream("popup_"+level.name()+".png")));
            }
            Label headerLabel = (Label)popup.lookup("#headerLabel");
            if(headerLabel != null){
            	headerLabel.setText(header);
            }
            Label msgLabel = (Label)popup.lookup("#messageLabel");
            if(msgLabel != null){
            	msgLabel.setText(msg);
            }
            Button acceptButton = (Button)popup.lookup("#acceptButton");
            if(acceptButton != null){
            	acceptButton.setOnAction(new EventHandler<ActionEvent>(){
					@Override
					public void handle(ActionEvent event) {
						if(onAccept != null){
							onAccept.perform(gui);
						}
						dialog.close();
					}
            	});
            }
            Button declineButton = (Button)popup.lookup("#declineButton");
            if(declineButton != null){
            	declineButton.setOnAction(new EventHandler<ActionEvent>(){
					@Override
					public void handle(ActionEvent event) {
						if(onDecline != null){
							onDecline.perform(gui);
						}
						dialog.close();
					}
            	});
            }
            
            dialog.setScene(new Scene(popup));
            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public enum Level {
		INFO("Information"),
		WARNING("Warning"),
		ERROR("Fatal error"),
		QUESTION("Important choice"),
		IDEA("Thinkable idea");
		
		private String text;
		
		private Level(String text){
			this.text = text;
		}
		
		public String getTextualRepresentation(){
			return text;
		}
	};
	
	public enum Type{
		YES_NO,
		OK_CANCEL,
		OK;
	}
}
