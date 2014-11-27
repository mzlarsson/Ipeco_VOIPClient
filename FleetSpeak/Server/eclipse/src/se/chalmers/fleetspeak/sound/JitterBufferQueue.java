package se.chalmers.fleetspeak.sound;



public class JitterBufferQueue {

	private Node head;
	
	public JitterBufferQueue(){
		head = new Node(null,0);
	}
	public void offer(byte[] data, long timestamp){
		Node n = new Node(data,timestamp);	
		Node q = head;
		while(q.next != null && n.timestamp>q.next.timestamp ){
			q = q.next;
		}
		n.next = q.next;
		q.next = n;
	}
	public byte[] poll(){
		if(head.next != null){
			byte[] b = head.next.data;
			head.next = head.next.next;
			return b;
		}
		return null;
	}
	
	public void clear(){
		head.next = null;
	}
	
	class Node{
		byte[] data;
		long timestamp;
		Node next;
		Node(byte[] data, long timestamp){
			
			this.data = data;
			this.timestamp = timestamp;
			next = null;
		}
	}

	


	
}
