package se.chalmers.fleetspeak.sound;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ShortMixer extends SimpleMixer{

	protected ShortMixer(int mixingInterval) {
		super(mixingInterval);
	}
	
	/**
	 * Returns the mixed byte array of data (PCM) retrieved from all registered BufferedStreams to this mixer, interprets all
	 * data as short data (pair of bytes).
	 * @return An array of the mixed PCM
	 */
	@Override
	protected byte[][] getMixed() {
		byte[][] data = getData();
		int members = data.length;			//Used for clarity in the code.
		
		if(data.length==2){
			return new byte[][]{data[1], data[0]};
		}else if(data.length>2){
			int maxDataLength = getMaxDataLength(data);
			byte[][] mixed = new byte[members][maxDataLength];
			ByteBuffer mixedBuffer = ByteBuffer.allocate(maxDataLength);
			mixedBuffer.order(ByteOrder.LITTLE_ENDIAN);
			int mixedData;
			for(int i = 0; i<members; i++){																//For every member
				for(int j = 0; j+1<maxDataLength; j+=2){												//Mix every other byte (every short). (Make sure we have a complete pair.)
					mixedData = 0;
					for(int k = 0; k<members; k++){														//By traversing all members sound
						if(data[k] != null && data[k].length>j+1 && k != i){												//Except themself (or empty signals)
							mixedData += bytesToShort(data[k][j+1], data[k][j]);						//And perform a+b on the signal
						}
					}
					mixedBuffer.putShort((short)(Math.min(Short.MAX_VALUE, Math.max(Short.MIN_VALUE, mixedData))));		//Make sure Short.MIN < signal < Short.MAX
				}
				
				mixedBuffer.flip();
				mixedBuffer.get(mixed[i]);
				mixedBuffer.clear();
			}
			
			return mixed;
		}else{
			return new byte[data.length][0];
		}
	}
	
	private short bytesToShort(byte big, byte small){
		return (short) ((big << 8) | (small & 0xff));
	}

}
