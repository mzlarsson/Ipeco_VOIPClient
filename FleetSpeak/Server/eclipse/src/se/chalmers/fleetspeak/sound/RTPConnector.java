package se.chalmers.fleetspeak.sound;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import se.chalmers.fleetspeak.util.Log;

import com.biasedbit.efflux.packet.DataPacket;
import com.biasedbit.efflux.participant.RtpParticipant;
import com.biasedbit.efflux.participant.RtpParticipantInfo;
import com.biasedbit.efflux.session.MultiParticipantSession;
import com.biasedbit.efflux.session.RtpSession;
import com.biasedbit.efflux.session.RtpSessionDataListener;

/**
 * Class for handling an RTP connection session on a specific port on the local
 * IP. Notice that the connection will be opened on the IP that is specified,
 * which means that 'localhost' is not a valid input for WAN connections.
 * 
 * NOTE: This class uses properties from the se.chalmers.fleetspeak.sound.Constants class.
 * 			* Constants.RTP_SESSION_ID
 * 
 * INFO: To retrieve data from a client, follow these steps.
 * 			1. Call getConnector(ip, dataPort, controlPort) to get a connector instance.
 * 			2. Call addParticipant(IP) with the client's IP. (Save the returned sourceID)
 * 			3. Call setRTPListener(sourceID, listener) to bind a listener for the client
 * 
 * INFO: To send data to a client, follow these steps.
 * 			1-2. As above
 * 			3. Call sendData(sourceID, data) to send the actual data
 * 
 * @author Matz Larsson
 */

public class RTPConnector implements RtpSessionDataListener{
	
	//List of all the started active instances
	private static List<RTPConnector> connectors = new ArrayList<RTPConnector>();
	
	private long currentUnknownSource = -1;
	
	private RtpSession session;
	private String ip;
	private int port;
	
	private Map<Long, RtpParticipant> participants;
	private Map<Long, RTPListener> listeners;
	
	/**
	 * Opens a new connection which listens on the given IP and port
	 * @param serverIP The IP to listen at
	 * @param serverPort The port to listen to
	 * @param payloadType The expected payload type from the connection
	 */
	private RTPConnector(String serverIP, int serverPort, int payloadType){
		this.ip = serverIP;
		this.port = serverPort;
		this.participants = new HashMap<Long, RtpParticipant>();
		this.listeners = new HashMap<Long, RTPListener>();
		
		int dataport = serverPort;
		int ctrlport = serverPort+1;
		RtpParticipant server = getParticipant(serverIP, dataport, ctrlport);
		session = new MultiParticipantSession(Constants.RTP_SESSION_ID, payloadType, server);
		
		session.init();
		session.addDataListener(this);
		
		Log.log("A new RTPConnector was created [IP="+serverIP+";dataport="+dataport+";ctrlport="+ctrlport+"]");
	}
	
	/**
	 * Sets the listener for the given participant
	 * @param sourceID The source ID of the participant
	 * @param listener The listener which is to handle signals from the participant
	 */
	public void setRTPListener(long sourceID, RTPListener listener){
		if(listener != null){
			listeners.put(sourceID, listener);
		}
	}
	
	/**
	 * Removes the listener for the given participant
	 * @param sourceID The source ID of the participant
	 */
	public void removeRTPListener(long sourceID){
		listeners.remove(sourceID);
	}
	
	/**
	 * Starts to listen for packets from the given IP. If a connection already is established
	 * to the given IP, it returns that connection's source ID.
	 * @param clientIP The IP to listen to
	 * @return The source ID of the created participant
	 */
	public long addParticipant(InetAddress clientIP, int clientPort){
		this.currentUnknownSource = -1;
		RtpParticipant participant = getParticipant(clientIP, clientPort, clientPort+1);
		if(participant != null){
			resolveSourceID(participant);
			
			if(!participants.containsKey(participant.getInfo().getSsrc())){				
				Log.log("Created RTP client for [IP="+clientIP.getHostAddress()+";SOURCEID="+participant.getInfo().getSsrc()+"]");
				session.addReceiver(participant);
				participants.put(participant.getInfo().getSsrc(), participant);
			}
			return participant.getInfo().getSsrc();
		}else{
			return 0;
		}
	}
	
	/**
	 * Waits for a current unknown source and sets the given participants source ID
	 * to the source ID of the unknown source
	 * @param participant The participant to set source to
	 */
	private void resolveSourceID(RtpParticipant participant){
		while(this.currentUnknownSource<0){
			try{
				Thread.sleep(1);
			}catch(InterruptedException ie){}
		}
		
		participant.getInfo().setSsrc(currentUnknownSource);
		currentUnknownSource = -1;
	}

	/**
	 * Stops to listen for packets from the given participant
	 * @param sourceID The source ID of the participant
	 * @return <code>true</code> if the participant was found and removed, <code>false</code> otherwise
	 */
	public boolean removeParticipant(long sourceID){
		RtpParticipant participant = participants.get(sourceID);
		if(participant != null){
			Log.log("Removed RTP client with sourceID="+sourceID);
			session.removeReceiver(participant);
			participants.remove(sourceID);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Checks whether this connector is the same as another Object. This will be true if
	 * the object is a RTPConnector and has the same ip and portnumber as this object.
	 * @param obj The object to compare with
	 * @return If the object is the same as this object.
	 * 		   See method description for definition of equality.
	 */
	@Override
	public boolean equals(Object obj){
		if(obj instanceof RTPConnector){
			RTPConnector conn = (RTPConnector)obj;
			return (this.ip.equals(conn.ip) && this.port == conn.port);
		}
		
		return false;
	}
	
	/**
	 * Checks whether this connector is started and active.
	 * @return <code>true</code> if it is started, <code>false</code> otherwise
	 */
	public boolean isStarted(){
		return session != null;
	}
	
	/**
	 * Receives packets from the RTP line and redirects to the proper RTPListener
	 * @param session The session that it is part of
	 * @param participant The participant who sent the data
	 * @param packet The data packet that was received
	 */
	@Override
	public void dataPacketReceived(RtpSession session, RtpParticipantInfo participant, DataPacket packet) {
		RTPListener listener = listeners.get(participant.getSsrc());
		if(listener != null){
			listener.dataPacketReceived(participant.getSsrc(), packet.getSequenceNumber(), packet.getDataAsArray());
		}else{
			//Check if the packet is from an unknown sender
			if(participants.get(participant.getSsrc()) == null){
				this.currentUnknownSource = participant.getSsrc();
			}
		}
	}
	
	/**
	 * Sends particular data to a given participant
	 * NOTE: It will only send the data if data has length>0
	 * @param sourceID The source ID of the participant to send to
	 * @param data The data to send
	 * @return If any data was sent.
	 */
	public boolean sendData(long sourceID, byte[] data){
		RtpParticipant participant = participants.get(sourceID);
		if(participant != null){
			long timestamp = Calendar.getInstance(Locale.GERMANY).getTime().getTime();
			session.sendData(data, timestamp, true, participant);		//TODO Fix the boolean value?
			return data.length>0;
		}else{
			return false;
		}
	}
	
	/**
	 * Stops the connector and terminates its current session
	 */
	public void stop(){
		this.session.removeDataListener(this);
		this.session.terminate();
		this.session = null;
		connectors.remove(this);
		participants.clear();
		listeners.clear();
		
		Log.log("The RTPConnector at [IP="+ip+";dataport="+port+"] was stopped");
	}

	

	/**
	 * Fetches an instance of a RTPConnector. If it not exists any connector connected
	 * to the given IP and port, one is started.
	 * @param serverIP The IP of the server
	 * @param serverPort The port to use for the server
	 * @param payloadType The expected payload type
	 * @return A valid instance of a RTPConnector with an open session.
	 */
	public static RTPConnector getConnector(String serverIP, int serverPort, int payloadType){
		RTPConnector connector = findConnector(serverIP, serverPort);
		if(connector == null){
			connector = new RTPConnector(serverIP, serverPort, payloadType);
		}
		
		return connector;
	}
	
	/**
	 * Finds a RTPConnector on the given IP and port. If none is found null is returned.
	 * @param ip The IP to search for
	 * @param port The port to search for
	 * @return A valid instance of a RTPConnector on given ip and port if it exists. Otherwise null.
	 */
	private static RTPConnector findConnector(String ip, int port){
		for(int i = 0; i<connectors.size(); i++){
			if(ip.equals(connectors.get(i).ip) && port==connectors.get(i).port){
				return connectors.get(i);
			}
		}
		
		return null;
	}
	
	/**
	 * Creates a participant on the given IP and ports
	 * @param ip The IP to use
	 * @param dataport The port to send/receive RTP data on
	 * @param ctrlport The port to send/receive RTP control signals on
	 * @return A valid participant that is connected to the given IP and ports
	 */
	private RtpParticipant getParticipant(InetAddress ip, int dataport, int ctrlport){
		return getParticipant(ip.getHostAddress(), dataport, ctrlport);
	}

	/**
	 * Creates a participant on the given IP and ports
	 * @param ip The IP to use
	 * @param dataport The port to send/receive RTP data on
	 * @param ctrlport The port to send/receive RTP control signals on
	 * @return A valid participant that is connected to the given IP and ports
	 */
	private RtpParticipant getParticipant(String ip, int dataport, int ctrlport){
		RtpParticipant participant = findParticipant(ip, dataport, ctrlport);
		if(participant == null){
			participant = RtpParticipant.createReceiver(ip, dataport, ctrlport);
		}
		
		return participant;
	}

	/**
	 * Searches for a participant with the given IP and ports. Returns null if not found.
	 * @param ip The IP of the participant
	 * @param dataport The data port of the participant
	 * @param ctrlport The control port of the participant
	 * @return The participant that is connected to the given IP and ports. null if not found.
	 */
	private RtpParticipant findParticipant(String ip, int dataport, int ctrlport){
		InetSocketAddress dataAddress = null;
		InetSocketAddress ctrlAddress = null;
		for(RtpParticipant participant : participants.values()){
			dataAddress = (InetSocketAddress)participant.getDataDestination();
			ctrlAddress = (InetSocketAddress)participant.getControlDestination();
			
			if(dataAddress.getAddress().equals(ip)){
				if(dataAddress.getPort() == dataport && ctrlAddress.getPort() == ctrlport){
					return participant;
				}
			}
		}
		
		return null;
	}
	
}
