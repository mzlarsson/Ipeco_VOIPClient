import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public abstract class Handler extends Thread{

	private Socket clientSocket;

	public Handler(Socket clientSocket){
		super("ChatHandler");
		this.clientSocket = clientSocket;
	}

	public boolean terminate(){
		ServerMain.unregisterHandler(this);
		try {
			if(clientSocket != null){
				clientSocket.close();
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	protected InputStream getInputStream(){
		if(clientSocket == null){
			return null;
		}

		try {
			return clientSocket.getInputStream();
		} catch (IOException e) {
			System.out.println("Could not fetch input stream: "+e.getClass().getCanonicalName());
			return null;
		}
	}

	protected OutputStream getOutputStream(){
		if(clientSocket == null){
			return null;
		}

		try {
			return clientSocket.getOutputStream();
		} catch (IOException e) {
			System.out.println("Could not fetch output stream: "+e.getClass().getCanonicalName());
			return null;
		}
	}
}