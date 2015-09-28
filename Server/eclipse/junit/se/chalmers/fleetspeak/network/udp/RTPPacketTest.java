package se.chalmers.fleetspeak.network.udp;

import static org.junit.Assert.*;

import org.junit.Test;

import se.chalmers.fleetspeak.sound.AudioType;

public class RTPPacketTest {
	
	@Test
	public void testByteArrayConnstructor() {
		byte[] b = {(byte) 0xff, (byte) 0x91, (byte) 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11};
		RTPPacket p = new RTPPacket(b);
		assertEquals(3, p.version);
		assertEquals(false, p.padding);
		assertEquals(true, p.extensions);
		assertEquals(15, p.cc);
		assertEquals(true, p.marker);
		assertEquals(AudioType.NONE, p.payloadType);
		assertEquals(4369, p.seqNumber);
		assertEquals(286331153, p.timestamp);
		assertEquals(286331153, p.ssrc);
		byte[] pay = p.getPayload();
		assertEquals(2, pay.length);
		assertEquals(17, pay[0]);
		assertEquals(17, pay[1]);
		assertTrue(p.toString()!=null);
		byte[] arr = p.toByteArrayDetailed();
		b[0] = (byte)0xdf;
		b[1] = (byte)0xff;
		assertArrayEquals(b, arr);
	}
	
	@Test
	public void testDetailedConstructor() {
		byte[] b = {(byte) 0x80, (byte) 0x11, (byte) 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x00, 0x00, 0x00, 0x00, 0x11, 0x11};
		RTPPacket p = new RTPPacket(AudioType.NONE, (short)4369, (long)286331153, new byte[] {0x11, 0x11});
		assertEquals(2, p.version);
		assertEquals(false, p.padding);
		assertEquals(false, p.extensions);
		assertEquals(0, p.cc);
		assertEquals(false, p.marker);
		assertEquals(AudioType.NONE, p.payloadType);
		assertEquals(4369, p.seqNumber);
		assertEquals(286331153, p.timestamp);
		assertEquals(0, p.ssrc);
		byte[] pay = p.getPayload();
		assertEquals(2, pay.length);
		assertEquals(17, pay[0]);
		assertEquals(17, pay[1]);
		assertTrue(p.toString()!=null);
		byte[] arr = p.toByteArrayDetailed();
		b[1] = (byte)0xff;
		assertArrayEquals(b, arr);
	}
	
	@Test
	public void testNullValues() {
		RTPPacket p1 = null;
		try {
			p1 = new RTPPacket(null);
		} catch (IllegalArgumentException e) {}
		assertNull(p1);
		RTPPacket p2 = null;
		try {
			p2 = new RTPPacket(null, (short)0, (long)0, null);
		} catch (NullPointerException e) {}
		assertNull(p2);
		p2 = new RTPPacket(AudioType.NONE, (short)0, (long)0, new byte[0]);
		String s = "RTP Packet[seq=0, timestamp=0, payload_size=0, payload=NONE]";
		assertTrue(s.equals(p2.toString()));
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
		assertNull(p);
		try {
			p = new RTPPacket(new byte[332]);
		} catch (IllegalArgumentException e) {}
		assertNotNull(p);
	}
	
	@Test
	public void testPadding() {
		byte[] b = new byte[10];
		RTPPacket p = new RTPPacket(AudioType.NONE, (short)0, 0, b);
		assertEquals(b.length+RTPPacket.HEADER_SIZE, p.toByteArrayDetailed().length);
		for(AudioType at : AudioType.values()) {
			if (!at.equals(AudioType.NONE)) {				
				p = new RTPPacket(at, (short)0, 0, b);
				assertEquals(at.toString(), at.getMaxLength()+RTPPacket.HEADER_SIZE, p.toByteArrayDetailed().length);			
			}
		}
	}
	
	@Test
	public void testDePadding() {
		byte[] b = new byte[332];
		b[0] = (byte) (b[0] | 0x20);
		RTPPacket p = null;
		for(AudioType at : AudioType.values()) {
			b[1] = (byte)(0x7f & at.getPayloadType());
			b[RTPPacket.HEADER_SIZE+at.getMaxLength()-1] = 1;
			p = new RTPPacket(b);
			if (!at.equals(AudioType.NONE)) {
				assertEquals(at.toString(), at.getMaxLength()-1, p.getPayload().length);
			} else {
				assertEquals(at.toString(), b.length-RTPPacket.HEADER_SIZE, p.getPayload().length);
			}
		}
	}
	
	@Test
	public void testGetPayload() {
		byte[] b;
		RTPPacket p = null;
		for(AudioType at : AudioType.values()) {
			if (at.getMaxLength()>10) {
				b = new byte[at.getMaxLength()-5];				
			} else {
				b = new byte[10];
			}
			p = new RTPPacket(at, (short)0, 0, b);
			assertEquals(at.toString(), b.length, p.getPayload().length);			
		}
	}
}
