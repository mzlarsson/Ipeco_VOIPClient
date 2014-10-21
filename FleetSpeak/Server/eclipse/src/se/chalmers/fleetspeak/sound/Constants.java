package se.chalmers.fleetspeak.sound;

import javax.sound.sampled.AudioFormat;

public class Constants {

	public static final String RTP_SESSION_ID = "fleetspeak_connection";
	public static final int RTP_SOUND_PAYLOAD_TYPE = 0;		//http://www.iana.org/assignments/rtp-parameters/rtp-parameters.xml
	public static final int RTP_UPDATE_INTERVAL = 1;
	public static final int RTP_PACKET_SIZE = 160;
	
	private static final float sampleRate = 8000.0F;
	private static final int sampleSizeInBits = 8;
	private static final int channels = 1;
	private static final int frameSize = 1;
	private static final float frameRate = 8000.0F;
	private static final boolean bigEndian = false;
	private static final AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
	public static AudioFormat AUDIOFORMAT = new AudioFormat(encoding, sampleRate, sampleSizeInBits, channels, frameSize, frameRate, bigEndian);
	
	private static String SERVER_IP = "127.0.0.1";
	
	
	
	public static String getServerIP(){
		return SERVER_IP;
	}
	public static void setServerIP(String serverIP){
		SERVER_IP = serverIP;
	}
	
}
