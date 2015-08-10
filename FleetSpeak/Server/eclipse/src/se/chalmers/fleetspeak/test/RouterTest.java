/**
 *
 */
package se.chalmers.fleetspeak.test;

import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.chalmers.fleetspeak.sound.Router;

/**
 * @author Nieo
 *
 */
public class RouterTest {

	Router router;
	int port = 10000;
	List<DatagramReciever> recivers;
	List<DatagramSender> senders;

	InetAddress localhost;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		System.out.println("Setup");
		localhost = InetAddress.getByName("localhost");
		recivers = new ArrayList<DatagramReciever>();
		senders = new ArrayList<DatagramSender>();

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		System.out.println("tearDown");

	}

	@Test
	public void testRemove(){
		System.out.println("Test remove");

	}

	@Test
	public void testSending() {
		System.out.println("Test sending");

		long time = System.currentTimeMillis();
		for(int i= 0; i < 10; i++) {
			createRoom();
		}
		for(DatagramSender s: senders){
			new Thread(s).start();
		}

		try {
			Thread.sleep(1300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(System.currentTimeMillis()- time);
		for(DatagramReciever r: recivers){
			assertTrue(900 < r.received);
			System.out.println(r.received);
		}

	}
	void createRoom(){
		Router r = new Router();
		senders.add(new DatagramSender(port++, r.getReceivePort()));
		r.start();
		DatagramReciever d1 = new DatagramReciever(port++);
		DatagramReciever d2 = new DatagramReciever(port++);
		recivers.add(d1);
		recivers.add(d2);
		r.addClient(0, localhost, d1.socket.getLocalPort());
		r.addClient(1, localhost, d2.socket.getLocalPort());
		new Thread(d1).start();
		new Thread(d2).start();
	}
}
