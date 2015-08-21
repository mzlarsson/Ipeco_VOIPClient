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
	
	public RTPHandler(byte[] data){
		this.data = data;
	}
	
	protected int getRTPVersion(){
		return data[0]>>>6;
	}
	
	protected boolean hasPadding(){
		return (data[0]&0x20) > 0; 
	}
	
	protected boolean hasExtension(){
		return (data[0]&0x10) > 0;
	}
	
	protected int getCrscCount(){
		return data[0]&0x0F;
	}
	
	protected boolean isMarked(){
		return (data[1]>>>7) > 0;
	}
	
	protected int getPayloadType(){
		return (data[1]&0x7F);
	}
	
	protected short getSequenceNumber(){
		return (short)(Byte.toUnsignedInt(data[2])*256+data[3]);
	}
	
	protected long getTimeStamp(){
		return (long)Byte.toUnsignedInt(data[4])*16777216+Byte.toUnsignedInt(data[5])*65536+Byte.toUnsignedInt(data[6])*256+data[7];
	}
	
	protected long getSourceID(){
		return (long)Byte.toUnsignedInt(data[8])*16777216+Byte.toUnsignedInt(data[9])*65536+Byte.toUnsignedInt(data[10])*256+data[11];
	}
	
	protected byte[] getData(){
		return Arrays.copyOfRange(data, 12, 160);
	}
}
