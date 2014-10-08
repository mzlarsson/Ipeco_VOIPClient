package se.chalmers.fleetspeak.tests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.chalmers.fleetspeak.tcp.Commands;

public class TestCommandHandler {
	BufferedReader br;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	br = new BufferedReader(new InputStreamReader(System.in));
	}

	@After
	public void tearDown() throws Exception {
	br.close();
	}

	@Test
	public final void testRun() {
		/*try{
			//Listen for messages from the client
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			ObjectOutputStream sender = new ObjectOutputStream(System.out);
			
			while(true){
				
				String message = reader.readLine();
				if(message != null){
					System.out.print("Command recived: ");
					if(message.startsWith(Commands.DISCONNECT.toString())){
						System.out.println(Commands.DISCONNECT.toString());
					}else if(message.startsWith(Commands.SET_NAME.getName())){
						System.out.println(Commands.SET_NAME);
					}else if(message.equals(Commands.MUTE.getName())){
						System.out.println(Commands.MUTE);
					}else if(message.equals(Commands.UNMUTE.getName())){
						System.out.println(Commands.UNMUTE);
					}else if(message.equals("data")){
						//send data to client
						System.out.println("Data");
						String s = "This string can be sent to the phone";
						sender.writeObject(s);
						sender.flush();
					}else{
						System.out.println("Unknown command. " + message);
					}
				}
			}
		}catch(IOException e){
			System.out.println("[CommandHandler] "+e.getMessage());
		}*/
		
	}

}
