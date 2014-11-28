package se.chalmers.fleetspeak.sound;

public class RTPClock {

	private long realTimestamp;
	private long RTPTimestamp;
	
	private int multiplier;
	
	public RTPClock(int multiplier){
		this.multiplier = multiplier;
	}

	public long getTime(long RTPTimestamp){
		if(RTPTimestamp == 0){
			this.RTPTimestamp = RTPTimestamp;
			realTimestamp = System.currentTimeMillis();
			return realTimestamp;
		}else{
			realTimestamp  += ((RTPTimestamp-this.RTPTimestamp)/multiplier);	
			this.RTPTimestamp = RTPTimestamp;
			return realTimestamp ;
		}
	}
	
	
}
