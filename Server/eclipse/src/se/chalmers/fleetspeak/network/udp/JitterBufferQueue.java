package se.chalmers.fleetspeak.network.udp;

public class JitterBufferQueue {

	private Node head, tail;
	private Object lock;
	
	public JitterBufferQueue(){
		RTPPacket p = new RTPPacket(new byte[12]);
		p.seqNumber = -1;
		p.timestamp = -1;
		head = new Node(p, null, null);
		tail = head;
		lock = new Object();
	}

	public void offer(RTPPacket e){
		synchronized (lock) {
			Node n = tail;
			while(head != tail && e.seqNumber - n.e.seqNumber < 0){
				n = n.previous;
			}
			Node newNode = new Node(e, n, n.next);
			n.next = newNode;
			if (tail == n) {
				tail = newNode;
			} else {
				n.next.previous = newNode;
			}
		}
	}
	
	public RTPPacket poll(){
		synchronized (lock) {
			Node n = head.next;
			if(n != null){
				head.next = n.next;
				if (n.next!=null) {
					n.next.previous = head;
				} else {
					tail = head;
				}
				return n.e;
			}
			return null;
		}
	}

	public RTPPacket peek() {
		synchronized (lock) {
			Node n = head.next;
			if(n != null){
				return n.e;
			}
			return null;
		}
	}
	
	/**
	 * The time difference between the first and last packet in the queue
	 * in milliseconds.
	 * @return The timestamp difference between the first and last packet.
	 */
	public long getBufferedTime() {
		return (head.next!=null) ? (tail.e.timestamp - head.next.e.timestamp) : 0;
	}
	
	private class Node{
		private RTPPacket e;
		private Node next, previous;
		public Node(RTPPacket e, Node previous, Node next){
			this.e = e;
			this.previous = previous;
			this.next = next;
		}
	}
}
