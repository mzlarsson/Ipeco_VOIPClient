package se.chalmers.fleetspeak.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DatabaseCommunicatorTest {
	DatabaseCommunicator dc;
	String username, alias, password, errorMsg;
	UserInfo info;
	

	@Before
	public void setUp() {
		dc = DatabaseCommunicator.getInstance();
		username = "A very long and unlikely Username";
		alias = "Batman";
		password = "";
		info = null;
		errorMsg = null;		
	}
	
	@After
	public void tearDown() {
		dc.terminate();
	}
	
	@Test
	public void testAdd() {
		info = dc.findUser(username);
		assertNull(info);
		
		errorMsg = null;
		errorMsg = dc.addUser(username, alias, password);
		assertNull(errorMsg);
		
		info = dc.findUser(username);
		assertNotNull(info);
		
		assertEquals(username, info.getUsername());
		assertEquals(alias, info.getAlias());
		assertEquals(password, info.getPassword());
		
		errorMsg = null;
		errorMsg = dc.addUser(username, alias, password);
		assertNotNull(errorMsg);
	}
	
	@Test
	public void testRemove() {
		info = dc.findUser(username);
		assertNotNull(info);
		
		errorMsg = null;
		errorMsg = dc.deleteUser(username);
		assertNull(errorMsg);
		
		info = dc.findUser(username);
		assertNull(info);
		
		errorMsg = null;
		errorMsg = dc.deleteUser(username);
		assertNotNull(errorMsg);
	}

}
