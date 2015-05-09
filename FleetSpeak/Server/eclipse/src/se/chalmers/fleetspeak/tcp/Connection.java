package se.chalmers.fleetspeak.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import se.chalmers.fleetspeak.util.Command;

public class Connection implements Runnable{
	private Socket socket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;

	private Thread myThread;
	private Boolean running;
	private CommandHandler callback;


	public Connection(Socket socket, CommandHandler handler){
		this.socket = socket;
		callback = handler;
		initStreams();
		myThread = new Thread(this);
		myThread.start();
	}

	private void initStreams(){
		try {
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			System.out.println("Failed to initialize streams for socket: " + socket.getInetAddress().getHostAddress());
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		running = true;
		while(running){
			Object o;
			try {
				if((o = inputStream.readObject()) != null){
					callback.handleCommand((Command) o);
					System.out.println("Stuff");
				}
				Thread.sleep(100);
			} catch (ClassNotFoundException | IOException e) {
				running = false;
				e.printStackTrace();
			} catch (InterruptedException e) {
				running = false;
				e.printStackTrace();
			}
		}

	}
	public void sendCommand(Command command){
		try {
			outputStream.writeObject(command);
		} catch (IOException e) {
			System.out.println("Failed to send command: " + command.getCommand());
			e.printStackTrace();
		}
	}

	public void close(){
		running = false;
		try {
			inputStream.close();
			outputStream.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
