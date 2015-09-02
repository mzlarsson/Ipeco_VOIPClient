package se.chalmers.fleetspeak.network.udp;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class JitterBufferQueueTest {

	JitterBufferQueue q = new JitterBufferQueue();
	RTPPacket first, second, third;
	
	@Before
	public void before() {
		q = new JitterBufferQueue();
		byte[] b = new byte[0];
		first = new RTPPacket((short) 0, 0, b);
		second = new RTPPacket((short) 1, 20, b);
		third = new RTPPacket((short) 2, 40, b);
	}
	
	@Test
	public void testEmpty() {
		assertEquals(0, q.getBufferedTime());
		assertNull(q.peek());
		assertNull(q.poll());
	}
	
	@Test
	public void testOneLong() {
		q.offer(second);
		assertEquals(0, q.getBufferedTime());
		assertEquals(second, q.peek());
		assertEquals(second, q.poll());
		assertNull(q.poll());
	}
	
	@Test
	public void testTwoLong() {
		q.offer(first);
		q.offer(second);
		assertEquals(20, q.getBufferedTime());
		assertEquals(first, q.poll());
		assertEquals(second, q.poll());
	}
	
	@Test
	public void testRearange() {
		q.offer(first);
		q.offer(third);
		q.offer(second);
		assertEquals(40, q.getBufferedTime());
		assertEquals(first, q.poll());
		assertEquals(second, q.poll());
		assertEquals(third, q.poll());
	}
	
	@Test
	public void testFillEmptyFillEmpty() {
		q.offer(first);
		assertEquals(first, q.poll());
		q.offer(first);
		assertEquals(first, q.poll());
	}
}
