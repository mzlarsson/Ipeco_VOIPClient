package se.chalmers.fleetspeak.network.udp;

import static org.junit.Assert.*;

import org.junit.Test;

public class RTPPacketTest {
	
	@Test
	public void testByteArrayConnstructor() {
		byte[] b = {(byte) 0xff, (byte) 0x91, (byte) 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11};
		RTPPacket p = new RTPPacket(b);
		assertEquals(3, p.version);
		assertEquals(true, p.padding);
		assertEquals(true, p.extensions);
		assertEquals(15, p.cc);
		assertEquals(true, p.marker);
		assertEquals(17, p.payloadType);
		assertEquals(4369, p.seqNumber);
		assertEquals(286331153, p.timestamp);
		assertEquals(286331153, p.ssrc);
		byte[] pay = p.getPayload();
		assertEquals(2, pay.length);
		assertEquals(17, pay[0]);
		assertEquals(17, pay[1]);
		assertTrue(p.toString()!=null);
		byte[] arr = p.toByteArrayDetailed();
		assertArrayEquals(b, arr);
		b = new byte[]{(byte) 0x80, (byte) 0x00, (byte) 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x00, 0x00, 0x00, 0x00, 0x11, 0x11};
		arr = p.toByteArraySimple();
		assertArrayEquals(b, arr);
	}
	
	@Test
	public void testDetailedConnstructor() {
		byte[] b = {(byte) 0x80, (byte) 0x11, (byte) 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x00, 0x00, 0x00, 0x00, 0x11, 0x11};
		RTPPacket p = new RTPPacket((byte)17, (short)4369, (long)286331153, new byte[] {0x11, 0x11});
		assertEquals(2, p.version);
		assertEquals(false, p.padding);
		assertEquals(false, p.extensions);
		assertEquals(0, p.cc);
		assertEquals(false, p.marker);
		assertEquals(17, p.payloadType);
		assertEquals(4369, p.seqNumber);
		assertEquals(286331153, p.timestamp);
		assertEquals(0, p.ssrc);
		byte[] pay = p.getPayload();
		assertEquals(2, pay.length);
		assertEquals(17, pay[0]);
		assertEquals(17, pay[1]);
		assertTrue(p.toString()!=null);
		byte[] arr = p.toByteArrayDetailed();
		assertArrayEquals(b, arr);
		b = new byte[]{(byte) 0x80, (byte) 0x00, (byte) 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x00, 0x00, 0x00, 0x00, 0x11, 0x11};
		arr = p.toByteArraySimple();
		assertArrayEquals(b, arr);
	}
	
	@Test
	public void testSimpleConnstructor() {
		byte[] b = {(byte) 0x80, (byte) 0x00, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x00, 0x00, 0x00, 0x00, 0x11, 0x11};
		RTPPacket p = new RTPPacket((short)4369, (long)286331153, new byte[] {0x11, 0x11});
		assertEquals(2, p.version);
		assertEquals(false, p.padding);
		assertEquals(false, p.extensions);
		assertEquals(0, p.cc);
		assertEquals(false, p.marker);
		assertEquals(0, p.payloadType);
		assertEquals(4369, p.seqNumber);
		assertEquals(286331153, p.timestamp);
		assertEquals(0, p.ssrc);
		byte[] pay = p.getPayload();
		assertEquals(2, pay.length);
		assertEquals(17, pay[0]);
		assertEquals(17, pay[1]);
		assertTrue(p.toString()!=null);
		byte[] arr = p.toByteArrayDetailed();
		assertArrayEquals(b, arr);
		arr = p.toByteArraySimple();
		assertArrayEquals(b, arr);
	}
	
	@Test
	public void testNullValues() {
		RTPPacket p1 = null;
		try {
			p1 = new RTPPacket(null);
		} catch (IllegalArgumentException e) {}
		assertNull(p1);
		RTPPacket p2 = new RTPPacket((byte)0, (short)0, (long)0, null);
		RTPPacket p3 = new RTPPacket((short)0, (long)0, null);
		String s = "RTP Packet[seq=0, timestamp=0, payload_size=0, payload=0]";
		assertTrue(s.equals(p2.toString()));
		assertTrue(s.equals(p3.toString()));
	}
	
	@Test
	public void testTooShortArray() {
		RTPPacket p = null;
		try {
			p = new RTPPacket(new byte[0]);
		} catch (IllegalArgumentException e) {}
		assertNull(p);
		try {
			p = new RTPPacket(new byte[5]);
		} catch (IllegalArgumentException e) {}
		assertNull(p);
		try {
			p = new RTPPacket(new byte[11]);
		} catch (IllegalArgumentException e) {}
		assertNull(p);
		try {
			p = new RTPPacket(new byte[12]);
		} catch (IllegalArgumentException e) {}
		assertNotNull(p);
	}
}
