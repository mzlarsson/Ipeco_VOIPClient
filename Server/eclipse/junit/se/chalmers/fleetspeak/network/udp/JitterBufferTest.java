package se.chalmers.fleetspeak.network.udp;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.chalmers.fleetspeak.sound.AudioType;

public class JitterBufferTest {

	JitterBuffer buf;
	RTPPacket p1, p2, p3, p4, p12, p22, p32;
	
	@Before
	public void setUp() throws Exception {
		byte[] b = new byte[0];
		p1 = new RTPPacket(AudioType.NONE, (short) 0, 0, b);
		p2 = new RTPPacket(AudioType.NONE, (short) 1, 20, b);
		p3 = new RTPPacket(AudioType.NONE, (short) 2, 40, b);
		p4 = new RTPPacket(AudioType.NONE, (short) 3, 50, b);
		p12 = new RTPPacket(AudioType.NONE, (short) 0, 0, b);
		p22 = new RTPPacket(AudioType.NONE, (short) 1, 50, b);
		p32 = new RTPPacket(AudioType.NONE, (short) 2, 85, b);
	}

	@Test
	public void testReadEmpty() {
		buf = new JitterBuffer(40,10);
		assertNull(buf.read());
	}

	@Test
	public void testReadNonEmptyNonReady() {
		buf = new JitterBuffer(40,10);
		buf.write(p1);
		assertNull(buf.read());
		buf.write(p2);
		assertNull(buf.read());
	}

	@Test
	public void testReadReady() {
		buf = new JitterBuffer(40,10);
		buf.write(p1);
		assertNull(buf.read());
		buf.write(p2);
		assertNull(buf.read());
		buf.write(p3);
		assertSame(p1, buf.read());
	}

	@Test
	public void testReadFullToEmptyNoSilentPackages() {
		buf = new JitterBuffer(40,10);
		buf.write(p1);
		buf.write(p2);
		buf.write(p3);
		assertSame(p1, buf.read());
		assertSame(p2, buf.read());
		assertSame(p3, buf.read());
	}

	@Test
	public void testRearrange() {
		buf = new JitterBuffer(40,10);
		buf.write(p1);
		buf.write(p3);
		buf.write(p2);
		assertSame(p1, buf.read());
		assertSame(p2, buf.read());
		assertSame(p3, buf.read());
	}

	@Test
	public void testSilentTimeBuilding() {
		buf = new JitterBuffer(80,10);
		buf.write(p12);
		buf.write(p22);
		buf.write(p32);					// JitterBuffer should be ready now
		assertSame(p12, buf.read());
		assertSame(p22, buf.read());	// It should now be in build mode
		assertNull(buf.read());			// Since the delay is too high (>80ms) a silence is inserted
		assertSame(p32, buf.read());	// With the updated delay p22 is now within the range
	}
	
	@Test
	public void testSilentDroppedBuilding() {
		buf = new JitterBuffer(40,10);
		buf.write(p1);
		buf.write(p2);
		buf.write(p4);					// JitterBuffer should be ready now
		assertSame(p1, buf.read());
		assertSame(p2, buf.read());		// With only one element left (p4) it should be in build mode
		assertNull(buf.read());			// Since the package with sequence number 2 is missing a silence is inserted
		assertSame(p4, buf.read());		// With the updated sequence number it is now within the range
	}
}
