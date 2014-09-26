import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;


public class ChatHandler extends Handler{
	
	public static final String ENCODING_TYPE = "ISO-8859-1";
	
	private String username = "[NoName]";

	public ChatHandler(Socket clientSocket) {
		super(clientSocket);
		setUsername("User_"+ServerMain.getHandlerCount());
	}

	@Override
	public void run(){
		try{
			//Send welcoming message
			PrintStream out = new PrintStream(this.getOutputStream());
			out.print("Welcome. There are currently "+ServerMain.getHandlerCount()+" people here.");
			
			//Listen for messages from the client
			BufferedReader reader = new BufferedReader(new InputStreamReader(this.getInputStream()));
			while(true){
				String message = reader.readLine();
				if(message != null){
					if(message.startsWith("/")){
						if(message.startsWith("/nick ")){
							String username = message.substring(6);
							ServerMain.sendMessage(this, ("[INFO] "+getUsername().substring(1, getUsername().length()-1)+" is now known as "+username).getBytes(ENCODING_TYPE));
							this.setUsername(username);
							out.print("[INFO] Your username has been set.");
						}else if(message.equals("/close")){
							this.terminate();
							break;
						}
					}else{
						message = this.getUsername()+" "+message;
						
						//Send messages to all other
						ServerMain.sendMessage(this, message.getBytes(ChatHandler.ENCODING_TYPE));
					}
				}
			}
		}catch(IOException e){
			System.out.println("[ChatHandler] "+e.getMessage());
			this.terminate();
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = "["+username+"]";
	}	
}
