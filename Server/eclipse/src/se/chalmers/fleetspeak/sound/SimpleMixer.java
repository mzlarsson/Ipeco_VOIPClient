package se.chalmers.fleetspeak.sound;

/**
 * Basic mixer as a first step towards real audio mixing.
 * @author Matz Larsson
 * @version 1.0
 */

public class SimpleMixer extends AbstractMixer{
	
	protected SimpleMixer(int mixingInterval){
		super(mixingInterval);
	}

	/**
	 * Returns the mixed byte array of data (PCM) retrieved from all registered BufferedStreams to this mixer.
	 * @param nbrOfBytes The number of bytes to mix together
	 * @return An array of the mixed PCM
	 */
	@Override
	protected byte[][] getMixed() {
		byte[][] data = getData();
		int members = data.length;			//Used for clarity in the code.
		
		byte[][] mixed = new byte[members][data.length];
		for(int i = 0; i<members; i++){																//For every member
			for(int j = 0; j<mixed[i].length; j++){													//Mix every byte
				for(int k = 0; k<members; k++){														//By traversing all members sound
					if(data[k].length>j && k != i){													//Except themself (or empty signals)
						mixed[i][j] = (byte) (mixed[i][j]+data[k][j]-mixed[i][j]*data[k][j]/127);		//And perform a+b-ab on the signal
					}
				}
			}
		}
		
		return mixed;
	}
}
