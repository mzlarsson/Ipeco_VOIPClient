package se.chalmers.fleetspeak.network_tmp.UDP;

import se.chalmers.fleetspeak.audio.sound.AudioType;

/**
 * @author Patrik Haar
 */
public class JitterBufferQueue {

    private Node head, tail;
    private Object lock;

    public JitterBufferQueue(){
        byte[] b = new byte[12];
        b[1] = (byte) (0x7f & AudioType.NONE.getPayloadType());
        RTPPacket p = new RTPPacket(b);
        p.seqNumber = -1;
        p.timestamp = -1;
        head = new Node(p, null, null);
        tail = head;
        lock = new Object();
    }

    public void offer(RTPPacket e){
        synchronized (lock) {
            Node n = tail;
            while(head != tail && ((short)(e.seqNumber - n.e.seqNumber)) < 0){
                n = n.previous;
            }
            Node newNode = new Node(e, n, n.next);
            n.next = newNode;
            if (tail == n) {
                tail = newNode;
            } else {
                n.next.previous = newNode;
            }
            //FIXME Temporary bad-ass printout.
//			System.out.print("[" + System.currentTimeMillis() + "]-[" + e.seqNumber + "]-[" + e.timestamp + "]-------------------------------------------");
//			int l = String.valueOf(e.seqNumber).length();
//			for (int i=6; i>l; i--) {
//				System.out.print("-");
//			}
//			System.out.print("\n|");
//			boolean full = false;
//			short seq = 0;;
//			n = head;
//			for(int i=0; i<20; i++) {
//				full = false;
//				if (i == 0) {
//					seq = n.next.e.seqNumber;
//				}
//				if(n.next!=null) {
//					n = n.next;
//					if(!(n.e.seqNumber>(seq++))) {
//						full = true;
//					}
//				}
//				System.out.print(" " + (full?"X":" ") + " |");
//			}
//			System.out.println();
//			System.out.println("---------------------------------------------------------------------------------");
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
        long time = (head.next!=null) ? (tail.e.timestamp - head.next.e.timestamp) : 0;
        return time<0 ? -time : time;
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
