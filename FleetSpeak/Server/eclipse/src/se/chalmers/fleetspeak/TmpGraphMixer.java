package se.chalmers.fleetspeak;


public class TmpGraphMixer {

	public static double byteRatio(byte b, boolean signed){
		double d = (double)b;
		if(signed){
			return d/(d<0?128:127);
		}else{
			return (d+128.0d)/255.0d;
		}
	}
	
	public static byte[] mixSounds(boolean signed, byte[]... data){
		byte[] mix = new byte[data[0].length];
		double sum = 0;
		for(int i = 0; i<mix.length; i++){
			sum = 0;
			for(int j = 0; j<data.length; j++){
				sum += byteRatio(data[j][i], signed);
			}
			
			sum /= 2;		//Lower all volume. IMPORTANT! This value effects MUCH!
			if(signed){
				sum = Math.max(-1.0d, Math.min(1.0d, sum));
				mix[i] = (byte)(sum<0?sum*128.0d:sum*127.0d);
			}else{
				sum = Math.max(0.0d, Math.min(1.0d, sum));
				mix[i] = (byte)(sum*255.0d-128.0d);
			}
		}
		
		return mix;
	}	
}
