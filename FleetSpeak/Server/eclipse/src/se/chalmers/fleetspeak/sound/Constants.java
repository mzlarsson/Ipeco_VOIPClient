package se.chalmers.fleetspeak.audio.sound;

public class Constants {

	public static final String RTP_SESSION_ID = "fleetspeak_connection";
	public static final int RTP_SOUND_PAYLOAD_TYPE = 0;		//http://www.iana.org/assignments/rtp-parameters/rtp-parameters.xml
	public static final int RTP_UPDATE_INTERVAL = 1;
	public static final int RTP_PACKET_SIZE = 160;
	
	private static String SERVER_IP = "127.0.0.1";
	
	
	
	public static String getServerIP(){
		return SERVER_IP;
	}
	public static void setServerIP(String serverIP){
		SERVER_IP = serverIP;
	}
	
}
