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
		
		if(data.length>1){
			int maxDataLength = getMaxDataLength(data);
			byte[][] mixed = new byte[members][maxDataLength];
			ByteBuffer mixedBuffer = ByteBuffer.allocate(maxDataLength);
			short mixedShort, dataShort;
			for(int i = 0; i<members; i++){																//For every member
				for(int j = 0; j+1<maxDataLength; j+=2){												//Mix every other byte (every short). (Make sure we have a complete pair.)
					mixedShort = 0;
					for(int k = 0; k<members; k++){														//By traversing all members sound
						if(data[k].length>j && k != i){													//Except themself (or empty signals)
							dataShort = bytesToShort(data[k][j], data[k][j+1]);							//Make sure to convert byte to short
							mixedShort = (short)(mixedShort+dataShort-mixedShort*dataShort/127);		//And perform a+b-ab on the signal
						}
					}
					mixedBuffer.putShort(mixedShort);
				}
				
				mixed[i] = mixedBuffer.array();
				mixedBuffer.clear();
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