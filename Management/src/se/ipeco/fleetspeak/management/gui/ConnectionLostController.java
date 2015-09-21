package se.ipeco.fleetspeak.management.gui;

import java.awt.event.ActionEvent;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javax.swing.Timer;

public class ConnectionLostController {

	@FXML
	private Label timeLeftLabel;
	
	private int defaultCountdownTime = 11;
	private IntegerProperty timeLeft;
	private Timer countdownTimer;
	
	public void initialize(){
		timeLeft = new SimpleIntegerProperty(defaultCountdownTime);
		timeLeftLabel.textProperty().bind(Bindings.concat(timeLeft, " s"));
		countdownTimer = new Timer(1000, (ActionEvent event) -> {
			if(timeLeft.get() == 0){
				countdownTimer.stop();
				Platform.runLater(() -> {
					MainController.disconnect();
				});
			}else{
				Platform.runLater(() -> {
					timeLeft.set(timeLeft.get()-1);
				});
			}
		});
		countdownTimer.setRepeats(true);
		countdownTimer.setInitialDelay(0);
		countdownTimer.start();
	}
	
}
