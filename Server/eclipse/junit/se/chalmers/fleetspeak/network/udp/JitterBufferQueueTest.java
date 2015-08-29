package se.chalmers.fleetspeak.network.udp;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class JitterBufferQueueTest {

	@Test
	public void test() {
		RTPPacket first = new RTPPacket((short) 0, 0, null);
		first.cc = 4;
		fail("Not yet implemented");
	}

}
