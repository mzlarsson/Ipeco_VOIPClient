package se.chalmers.fleetspeak.network.udp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class JitterBufferQueueTest {

	@Test
	public void queueTest(){
		JitterBufferQueue<Object> queue = new JitterBufferQueue<Object>();
		queue.offer("first", 1);
		queue.offer("second", 2);

		assertEquals("first", queue.poll().e);
		assertEquals("second", queue.poll().e);

		queue.offer("first", 1);
		queue.offer("third", 3);
		queue.offer("second", 2);

		assertEquals("first", queue.poll().e);
		assertEquals("second", queue.poll().e);
		assertEquals("third", queue.poll().e);

	}
}
