package se.chalmers.fleetspeak.sound;

public class PCMUtil {
	
	/**
	 * Truncates the sample
	 * @param sample The sample to truncate
	 * @return A truncated sample
	 */
	public static short truncate(short sample){
		return (short)(sample & 0xff00);
	}
		/**
	 * Encodes a PCM short
	 * NOTE: Based on http://www.speech.cs.cmu.edu/comp.speech/Section2/Q2.7.html
	 * @param sample The sample to encode
	 * @return An encoded PCMU sample
	 */
	public static byte encode(short sample){
		final short BIAS = 132;//0x84
		final short CLIP = 32635;//32767-BIAS
			//Convert sample to sign-magnitude
		int sign = sample & 0x8000;
		if(sign != 0){
			sample = (short)-sample;
			sign = 0x80;
		}
			//Prevent overflow
		if(sample > CLIP) sample = CLIP;
			sample += BIAS;
			int exp;
		//Shift sign bit off to the left
		short temp = (short)(sample << 1);
		for(exp = 7; exp > 0; exp--){
			if((temp & 0x8000) != 0) break;//found it
			temp = (short)(temp << 1);//shift and loop
		}
			temp = (short)(sample >> (exp + 3));
		//Mask and save
		int mantis = temp & 0x000f;
		//Construct the complement of the ulaw byte.
		byte ulawByte = (byte)(sign | (exp << 4) | mantis);
		//Complement to create actual ulaw byte
		return (byte)~ulawByte;
	}
	
	/**
	 * Decodes a ulaw sound byte
	 * NOTE: Based on http://web.umr.edu/~dcallier/school/311_final_report.doc
	 * @param ulawByte The byte to decode
	 * @return The decoded byte
	 */
	public static short decode(byte ulawByte){
		//Perform one complement
		ulawByte = (byte)(~ulawByte);
		//Get the sign bit
		int sign = ulawByte & 0x80;
		//Get the value of the exponent
		int exp = (ulawByte & 0x70) >> 4;
		//Get the mantissa
		int mantis = ulawByte & 0xf;
		//Construct the 16-bit output value as int
		int rawValue = (mantis << (12 - 8 + (exp - 1))) + (132 << exp) - 132;
		//Change the sign if necessary
		return (short)((sign != 0)?-rawValue : rawValue);
	}
}
