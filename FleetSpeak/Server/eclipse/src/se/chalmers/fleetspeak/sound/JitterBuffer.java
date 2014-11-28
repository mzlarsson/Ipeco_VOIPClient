package se.chalmers.fleetspeak.sound;



import se.chalmers.fleetspeak.util.Log;


public class JitterBuffer {

	private int jitter;
	private long timestamp;
	private long delta;
	private int period;
	private long duration;
	private boolean ready = false;
	
	private RTPClock clock;
	
	private JitterBufferQueue queue = new JitterBufferQueue();
	
	
	public JitterBuffer(){
		Log.log("[JitterBuffer] JitterBuffer created");
		jitter = 50;
		period = 20;
		clock = new RTPClock(8);
	}
	
	public void write(byte[] data, long timestamp){
		
		long t = clock.getTime(timestamp);
//		System.out.println("InuptTime " + t + "    Time for last read " + 
//				System.currentTimeMillis()  + "    Diff " + (System.currentTimeMillis()-t));
		
		if(delta == 0){
			this.timestamp = System.currentTimeMillis();
			delta = t - this.timestamp;
			this.timestamp += delta;
		}
		
		if(ready && t > (this.timestamp + jitter)){
			System.out.println("packet arrived too late");
			return; 
		}
		
		
		queue.offer(data,t);
		
		duration += period;
		if(!ready & duration > (period+jitter))
			ready = true;
		
		
	}
	public byte[] read(){
		if(!ready)
			return null;
		this.timestamp = System.currentTimeMillis() + delta;
		return queue.poll();
	}
	
	
	
	public void reset(){
		duration = 0;
		delta = 0;
		queue.clear();
		
		
	}
	
	
	
}
