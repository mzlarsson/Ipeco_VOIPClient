package se.chalmers.fleetspeak;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import com.biasedbit.efflux.packet.DataPacket;
import com.biasedbit.efflux.participant.RtpParticipant;
import com.biasedbit.efflux.participant.RtpParticipantInfo;
import com.biasedbit.efflux.session.MultiParticipantSession;
import com.biasedbit.efflux.session.RtpSession;
import com.biasedbit.efflux.session.RtpSessionDataListener;

/**
 * Created by Nieo on 01/10/14.
 * For TCP connection to the server
 */
public class TmpConnector{

    private Socket socket;
    private InputStream input;
    private PrintWriter output;
    private final String ip;
    private final int port;
    private boolean isConnected = false;
        
	private static int CLIENT_RTP_DATA_PORT = 1024;
	private static int CLIENT_RTP_CTRL_PORT = 1025;

	//Values provided by client, now hard coded for practical reasons.
	//TODO SERVER and CLIENT must be set for testing!
    public static String SERVER = "192.168.1.3";
    public static String CLIENT = "192.168.1.3";
    public static int SERVER_PORT = 8867;
    public static int SERVER_RTP_DATA_PORT = 8868;
    
    private RtpSession session;
    
    public static void main(String[] args){

    	final TmpConnector connector = new TmpConnector(SERVER, SERVER_PORT);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				connector.disconnect();
			}
		}));
    	
		connector.connect();
    }

    public TmpConnector(String ip, int port){
        this.ip = ip;
        this.port = port;
    }
    /**
     * Sends a command to the server
     * @param command
     */
    public void sendCommand(String command){

        if(output != null){
            output.println(command);
            System.out.println("Sent command: " + command);
        }
    }

    /**
     * tries to etablish a connection to a server

     */
    public void connect(){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ip, port);
                    System.out.println("Connection established to" + socket.toString());
                    isConnected = true;

                    output = new PrintWriter(socket.getOutputStream(), true);
                    System.out.println("Outputsteam ready");
                    
                    initRTPSession();
                    
                    Scanner in = new Scanner(System.in);
                    String s = "";
                    while(true){
                    	s = in.nextLine();
                    	output.println(s);
                		session.sendData(s.getBytes(), 123456789, true);
                    }

                }catch(IOException e){
                    System.out.println("Connection failed " + e.getMessage() );
                }
            }
        });
        thread.start();
    }

    private void initRTPSession(){
		String sessionid = "fleetspeak_connection"; // you need to set this
		
		RtpParticipant client = getParticipant(CLIENT, CLIENT_RTP_DATA_PORT, CLIENT_RTP_CTRL_PORT);
		RtpParticipant server = getParticipant(SERVER, SERVER_RTP_DATA_PORT, SERVER_RTP_DATA_PORT+1);
		session = new MultiParticipantSession(sessionid, 0, client);

		session.addReceiver(server);
		
		session.addDataListener(new RtpSessionDataListener() {
		    @Override
		    public void dataPacketReceived(RtpSession session, RtpParticipantInfo participant, DataPacket packet) {
		    	System.out.println("Client got packet: '"+new String(packet.getDataAsArray())+"'");
		    }
		});

		session.init();
    }
	private RtpParticipant getParticipant(String ip, int dataport, int ctrlport){
		return RtpParticipant.createReceiver(ip, dataport, ctrlport);
	}
    
    
    public void disconnect(){
    	if(session != null){
    		session.terminate();
    	}
    	
    	try {
    		if(socket != null){
    			socket.close();
    		}
		} catch (IOException e) {
			System.out.println("Could not close socket!");
		}
    }
    
    public boolean isConnected() {
        return isConnected;
    }
}
