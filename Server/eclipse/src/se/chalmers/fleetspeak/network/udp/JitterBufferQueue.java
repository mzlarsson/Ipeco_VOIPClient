package se.chalmers.fleetspeak.network.udp;

public class JitterBufferQueue<E> {

	private Node head;

	public JitterBufferQueue(){
		head = new Node(null, 0,null);
	}

	public void offer(E e, long timestamp){
		synchronized (head) {
			Node n = head;
			while(n.next != null && timestamp > n.next.timestamp){
				n = n.next;
			}
			Node newNode = new Node(e,timestamp,n.next);
			n.next = newNode;
		}
	}
	public Node poll(){
		synchronized (head) {
			Node e = head.next;
			if(e != null){
				head.next = e.next;
			}
			return e;
		}
	}


	protected class Node{
		E e;
		long timestamp;
		Node next;
		Node(E e, long timestamp, Node next){
			this.e = e;
			this.timestamp = timestamp;
			this.next = next;
		}
	}

}
