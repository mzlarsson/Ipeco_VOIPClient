package se.chalmers.fleetspeak.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Authenticator implements Runnable {

	Socket socket;
	String password;


	public Authenticator(Socket socket) {
		this.socket = socket;
		password = "pass";
		new Thread(this).start();
	}



	@Override
	public void run() {
		try {
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}



}
