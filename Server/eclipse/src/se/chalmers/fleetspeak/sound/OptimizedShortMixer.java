package se.chalmers.fleetspeak.sound;

import java.util.Arrays;


public class OptimizedShortMixer extends ShortMixer{

	protected OptimizedShortMixer(int mixingInterval) {
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
			
			int[] talking = new int[data.length];
			int nbrOfTalking = 0;
			for(int i = 0; i<data.length; i++){
				if(data[i] != null && data[i].length==maxDataLength){
					talking[nbrOfTalking++] = i;
				}
			}
			
			//Adjust length
			talking = Arrays.copyOf(talking, nbrOfTalking);
			
			//Preprocess
			int[] baseMix = new int[maxDataLength/2];
			byte[] silentMix = new byte[maxDataLength];
			for(int i = 0; i<baseMix.length; i++){
				//Add all talkers
				for(int currentTalker : talking){
					baseMix[i] += bytesToShort(data[currentTalker][2*i+1], data[currentTalker][2*i]);				//Uses little endian
				}
				insertLittleEndianShort(silentMix, 2*i, setShortRoof(baseMix[i]));
			}
			
			byte[][] mixed = new byte[members][maxDataLength];
			int mixedData;
			for(int i = 0; i<members; i++){																//For every member
				if(data[i] == null || data[i].length!=maxDataLength){
					//User is silent
					mixed[i] = silentMix;
				}else{
					//User sounds in any way
					int byteIndex;
					for(int j = 0; 2*j+1<maxDataLength; j++){												//Mix every other byte (every short). (Make sure we have a complete pair.)
						byteIndex = 2*j;
						if(data[i].length>byteIndex){
							mixedData = baseMix[j]-bytesToShort(data[i][byteIndex+1], data[i][byteIndex]);
						}else{
							mixedData = baseMix[j];
						}
						insertLittleEndianShort(mixed[i], byteIndex, setShortRoof(mixedData));
					}
				}
			}
			
			return mixed;
		}else{
			return new byte[data.length][0];
		}
	}
	
	private short setShortRoof(int mix){
		return (short)(Math.min(Short.MAX_VALUE, Math.max(Short.MIN_VALUE, mix)));
	}
	
	private short bytesToShort(byte big, byte small){
		return (short) ((big << 8) | (small & 0xff));
	}
	
	private void insertLittleEndianShort(byte[] b, int offset, short value){
		b[offset] = (byte)(value & 0x00FF); 							//Should be little endian conversion.
	    b[offset+1] = (byte)((value & 0xFF00) >> 8);
	}
	
	@SuppressWarnings("unused")
	private void insertBigEndianShort(byte[] b, int offset, short value){
		b[offset] = (byte)((value & 0xFF00) >> 8);						//Should be big endian
		b[offset+1] = (byte)(value & 0x00FF); 
	}

}
