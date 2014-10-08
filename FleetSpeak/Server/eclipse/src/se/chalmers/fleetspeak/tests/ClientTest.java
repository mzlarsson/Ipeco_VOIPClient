package se.chalmers.fleetspeak.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.ServerSocket;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import se.chalmers.fleetspeak.Client;

public class ClientTest {
	
	private static Client client;
	private static ServerSocket ss;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//ss = new ServerSocket(8867);
		//client = new Client(ss.accept(),8868,"test");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	

	
	@Test
	public final void testClient() throws IOException {
		//assertSame(client, new Client(ss.accept(),8868,"test"));
		//ss.close();
		
	}

	@Test
	public final void testGetUserCode() {
	assertTrue(true);
	}

	@Test
	public final void testClientConnected() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testClientDisconnected() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testClose() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testConnectionLost() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetClient() {
		fail("Not yet implemented"); // TODO
	}

}
