package se.chalmers.fleetspeak.sound;


public class JitterBuffer {

	private int jitter;
	private long timestamp;
	private long delta;
	private int period;
	private long duration;
	private boolean ready = false;
	
	private JitterBufferQueue queue = new JitterBufferQueue();
	
	
	public JitterBuffer(){
		jitter = 50;
		period = 20;
	}
	
	public void write(byte[] data, long timestamp){
		
		
		if(delta == 0){
			this.timestamp = System.currentTimeMillis();
			delta = timestamp - this.timestamp;
			timestamp += delta;
		}
		
		if(ready && timestamp > (this.timestamp + jitter)){
			//Packet arrived too late
			return; 
		}
		
		
		queue.offer(data,timestamp);
		
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
