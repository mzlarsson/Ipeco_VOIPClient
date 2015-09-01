package se.chalmers.fleetspeak.sound;

import java.nio.ByteBuffer;

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
			short mixedShort, dataShort;
			for(int i = 0; i<members; i++){																//For every member
				for(int j = 0; j+1<maxDataLength; j+=2){												//Mix every other byte (every short). (Make sure we have a complete pair.)
					mixedShort = 0;
					for(int k = 0; k<members; k++){														//By traversing all members sound
						if(data[k].length>j+1 && k != i){												//Except themself (or empty signals)
							dataShort = bytesToShort(data[k][j], data[k][j+1]);							//Make sure to convert byte to short
							mixedShort = (short)(Math.min(Short.MAX_VALUE, Math.max(Short.MIN_VALUE, mixedShort+dataShort)));		//And perform a+b on the signal (adapt max/min)
						}
					}
					mixedBuffer.putShort(mixedShort);
				}
				
				mixed[i] = mixedBuffer.array();
				mixedBuffer = ByteBuffer.allocate(maxDataLength);
			}
			
			return mixed;
		}else{
			return new byte[data.length][0];
		}
	}
	
	private short bytesToShort(byte big, byte small){
		return (short) (((big & 0xff) << 8) | (small & 0xff));
	}

}
