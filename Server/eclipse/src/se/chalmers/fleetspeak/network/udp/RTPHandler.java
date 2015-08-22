package se.chalmers.fleetspeak.network.udp;

import java.net.DatagramSocket;
import java.util.Arrays;

public class RTPHandler implements PacketReceiver{
	
	private byte[] data;
	private UDPHandler handler;

	public RTPHandler(DatagramSocket socket) {
		handler = new UDPHandler(socket, 172);
		handler.setReceiver(this);
		handler.start();
	}
	
	@Override
	public void handlePacket(byte[] packet){
		this.data = packet;
	}
	
	private int getRTPVersion(){
		return data[0]>>>6;
	}
	
	private boolean hasPadding(){
		return (data[0]&0x20) > 0; 
	}
	
	private boolean hasExtension(){
		return (data[0]&0x10) > 0;
	}
	
	private int getCrscCount(){
		return data[0]&0x0F;
	}
	
	private boolean isMarked(){
		return (data[1]>>>7) > 0;
	}
	
	private int getPayloadType(){
		return (data[1]&0x7F);
	}
	
	private short getSequenceNumber(){
		return (short)(Byte.toUnsignedInt(data[2])*256+data[3]);
	}
	
	private long getTimeStamp(){
		return (long)Byte.toUnsignedInt(data[4])*16777216+Byte.toUnsignedInt(data[5])*65536+Byte.toUnsignedInt(data[6])*256+data[7];
	}
	
	private long getSourceID(){
		return (long)Byte.toUnsignedInt(data[8])*16777216+Byte.toUnsignedInt(data[9])*65536+Byte.toUnsignedInt(data[10])*256+data[11];
	}
	
	private byte[] getData(){
		return Arrays.copyOfRange(data, 12, 160);
	}
	
	private class JitterBuffer {
		
		private long bufferTime;
		
		/**
		 * Constructs a JitterBuffer which delays the media in favor of consistency
		 * where packets have more time to arrive and can be sorted in the right order.
		 * Higher buffer time will improve consistency on poor networks but will add a
		 * delay to the processing of the media equal to the buffer time. 
		 * @param bufferTime The delay the JitterBuffer has to work with in milliseconds.
		 */
		public JitterBuffer(long bufferTime) {
			this.bufferTime = bufferTime;
		}
	}
}
