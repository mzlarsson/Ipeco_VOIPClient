package se.chalmers.fleetspeak.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import org.fxmisc.richtext.StyleClassedTextArea;

import se.chalmers.fleetspeak.core.MainController;

public class ServerGUIController implements StageOwner{

	@FXML
	private TextField portNumber;
	@FXML
	private Button startButton;
	@FXML
	private TextField commandInput;
	@FXML
	private ComboBox<String> logLevelChooser;

	private StyleClassedTextArea terminal;

	private MainController server;
	private Stage stage;

	private CommandLog commandLog;
	private Logger logger = Logger.getLogger("Debug");
	private Handler logHandler;
	private List<LogRecord> logRecords;
	private int nbrOfCharsInTerminal = 0;

	public ServerGUIController(){
		setupLogger();
		commandLog = new CommandLog();
		logRecords = new LinkedList<LogRecord>();
	}

	public void initialize(){
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				logLevelChooser.getItems().addAll("ALL", "FINE", "INFO", "SEVERE", "WARNING");
				logLevelChooser.getSelectionModel().select(0);
			}
		});
	}

	public void setTerminal(StyleClassedTextArea terminal){
		this.terminal = terminal;
		portNumber.requestFocus();
	}

	public void startServer(){
		//Clean up old server
		if(server != null){
			closeServer();
		}

		//Start new one
		try{
			final int port = Integer.parseInt(portNumber.getText());
			server = new MainController(port);

			//Fix UI components
			startButton.setText("Stop");
			startButton.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event) {
					closeServer();
				}
			});
			portNumber.setDisable(true);
		}catch(NumberFormatException nfe){
			//Do nothing.
		}
	}

	public void portNumberChanged(){
		boolean valid = false;
		//Parse result
		try{
			int port = Integer.parseInt(portNumber.getText());
			valid = port>=1024;
		}catch(NumberFormatException nfe){
			valid = false;
		}

		//Display result
		if(!portNumber.getStyleClass().contains(valid?"tfvalid":"tferror")){
			portNumber.getStyleClass().add(valid?"tfvalid":"tferror");
		}
		portNumber.getStyleClass().remove(valid?"tferror":"tfvalid");

		//Set startable or not
		startButton.setDisable(false);
	}

	public void changeLogLevel(){
		logger.setLevel(Level.parse(logLevelChooser.getValue()));
		clearConsole();
		int loggerLevelValue = logger.getLevel().intValue();
		for(LogRecord record : logRecords){
			if(record.getLevel().intValue() >= loggerLevelValue){
				displayLog(record);
			}
		}
	}

	public void closeServer(){
		//Notify
		logger.info("Shutting down server...");

		//Fix UI components
		startButton.setText("Start");
		startButton.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				startServer();
			}
		});
		portNumber.setDisable(false);

		//Close server
		if(server != null){
			server.terminate();
			server = null;
		}

		logger.info("Server closed");
	}

	private void setupLogger() {
		logHandler = new Handler() {
			@Override
			public synchronized void publish(LogRecord record) {
				logRecords.add(record);
				displayLog(record);
			}

			@Override
			public void flush() {
				Platform.runLater(new Runnable(){
					@Override
					public void run() {
						terminal.clear();
					}
				});
			}

			@Override
			public void close() throws SecurityException {}
		};

		Logger.getLogger("Debug").addHandler(logHandler);
	}

	private void displayLog(LogRecord record){
		String msg = record.getMessage();
		int start = 0, end = 0;
		String tmp = "";
		final List<Tag> tags = new ArrayList<Tag>();
		while((start = msg.indexOf("<"))>=0){
			end = msg.indexOf(">", start);
			tmp = msg.substring(start+1, end);
			tags.add(new Tag(nbrOfCharsInTerminal+start, tmp));
			msg = msg.substring(0, start)+msg.substring(end+1, msg.length());
		}

		final String message = msg;
		nbrOfCharsInTerminal += message.length()+1;

		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				terminal.appendText(message+"\n");

				Tag tmpTag = null;
				while(!tags.isEmpty()){
					tmpTag = tags.remove(0);
					if(tmpTag.isStart()){
						for(int i = 0; i<tags.size(); i++){
							if(tmpTag.getName().equals(tags.get(i).getName()) && !tags.get(i).isStart()){
								terminal.setStyleClass(tmpTag.getIndex(), tags.get(i).getIndex(), "tag_"+tmpTag.getName());
								tags.remove(i);
								break;
							}
						}
					}
				}
			}
		});
	}

	public void commandEntered(){
		String cmd = commandInput.getText();
		runCommand(cmd);
		commandLog.add(cmd);
		commandInput.clear();
	}

	public void commandInputPressed(KeyEvent ke){
		//Stop up key functionality to be performed
		if(ke.getCode().equals(KeyCode.UP) || ke.getCode().equals(KeyCode.TAB)){
			ke.consume();
		}
	}

	public void commandInputChanged(KeyEvent ke){
		if(ke.getCode().equals(KeyCode.UP)){
			String cmd = commandLog.getPreviousCommand();
			if(cmd != null){
				commandInput.setText(cmd);
			}
		}else if(ke.getCode().equals(KeyCode.DOWN)){
			String cmd = commandLog.getNextCommand();
			if(cmd != null){
				commandInput.setText(cmd);
			}
		}else if(ke.getCode().equals(KeyCode.TAB)){
			if(CommandSearcher.hasSearch()){
				commandInput.setText(CommandSearcher.next());
			}else{
				commandInput.setText(CommandSearcher.search(commandInput.getText()));
			}
		}else{
			CommandSearcher.clearSearch();
		}

		commandInput.positionCaret(commandInput.getText().length());
	}

	private void runCommand(String cmd){
		logger.info("<b> > "+cmd+"</b>");
		if (cmd.equals("party")) {
			startTheParty();
		} else {
			if(hasRunningServer()){
				logger.severe("Command functionality from GUI temporary disabled");
			}else{
				logger.warning("Commands are disabled when server is not running");
			}
		}
	}

	public void startTheParty(){
		//The party functionality
		final Runnable partyPart = new Runnable(){
			@Override
			public void run() {
				terminal.setStyle("-fx-background-color:"+getRandomHexColor()+";");
			}

			private String getRandomHexColor(){
				String color = "#";
				int tmp = 0;
				for(int i = 0; i<6; i++){
					tmp = (int)(Math.random()*16);
					color += tmp<10?tmp+"":Character.toChars((char)(tmp+55))[0];
				}

				return color;
			}
		};

		//Wrapper for the party (to avoid locking the main thread)
		final Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				System.out.println("patry for everybody, DANCE");
				int num = 0, max = 50;
				while(num<max){
					Platform.runLater(partyPart);
					num++;

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
				}

				//Reset terminal color
				Platform.runLater(new Runnable(){
					@Override
					public void run() {
						terminal.setStyle("-fx-background-color:#ffffff;");
					}
				});
			}
		});
		t.start();
	}

	public void openLog(){
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Choose log file");
		ExtensionFilter fslog = new ExtensionFilter("Fleetspeak log files", "*.fslog");
		ExtensionFilter allFiles = new ExtensionFilter("All files", "*.*");
		chooser.getExtensionFilters().addAll(fslog, allFiles);
		chooser.setSelectedExtensionFilter(fslog);
		chooser.setInitialDirectory(new File(System.getProperty("user.home")));
		File file = chooser.showOpenDialog(this.stage);
		if(file != null){
			try {
				clearConsole();
				Scanner sc = new Scanner(file);
				while(sc.hasNextLine()){
					logger.info(sc.nextLine());
				}
				sc.close();
			} catch (FileNotFoundException e) {
				Popup.alert(this, Popup.Level.ERROR, "Could not read from file");
			}
		}
	}

	public void saveLog(){
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Enter savefile info");
		ExtensionFilter fslog = new ExtensionFilter("Fleetspeak log files", "*.fslog");
		ExtensionFilter allFiles = new ExtensionFilter("All files", "*.*");
		chooser.getExtensionFilters().addAll(fslog, allFiles);
		chooser.setSelectedExtensionFilter(fslog);
		chooser.setInitialDirectory(new File(System.getProperty("user.home")));
		File file = chooser.showSaveDialog(this.stage);
		if(file != null){
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(file));
				out.write(terminal.getText());
				out.close();
			} catch (IOException e) {
				Popup.alert(this, Popup.Level.ERROR, "Could not save the log, try again!");
			}
		}
	}

	public void clearConsole(){
		terminal.clear();
		nbrOfCharsInTerminal = 0;
	}

	public void sendErrorMessage(String msg){
		Popup.alert(this, Popup.Level.ERROR, msg);
	}

	public boolean hasRunningServer(){
		return server != null;
	}

	public void setPrimaryStage(Stage stage){
		this.stage = stage;
	}
	@Override
	public Stage getPrimaryStage(){
		return this.stage;
	}

	@Override
	public void terminate(){
		stage.close();
	}
}
