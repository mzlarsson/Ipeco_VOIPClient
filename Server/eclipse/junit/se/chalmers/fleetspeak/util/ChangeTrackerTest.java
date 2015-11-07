package se.chalmers.fleetspeak.util;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ChangeTrackerTest {

	ChangeTracker ct;
	@Before
	public void setUp() throws Exception {
		ct = new ChangeTracker();
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testAddGet() {
		try{
			JSONObject a = new JSONObject("{\"herp\":\"derp\"}");
			JSONObject av = new JSONObject("{\"herp\":\"derp\"}");
			av.put("structurestate", 0);
			JSONObject b = new JSONObject("{\"a\":1}");
			JSONObject bv = new JSONObject("{\"a\":1}");
			bv.put("structurestate", 1);
			JSONObject updateda = ct.addEntry(a);
			assertEquals(av.toString(), updateda.toString());
			ct.addEntry(b);

			List<JSONObject> entrys = ct.getChanges(0);
			assertEquals(av.toString(), entrys.get(0).toString());
			assertEquals(bv.toString(), entrys.get(1).toString());

			JSONObject c = new JSONObject("{\"kappa\":\"kappa\"}");
			JSONObject cv = new JSONObject("{\"kappa\":\"kappa\"}");
			cv.put("structurestate", 2);
			ct.addEntry(c);
			entrys = ct.getChanges(1);
			assertEquals(bv.toString(), entrys.get(0).toString());
			assertEquals(cv.toString(), entrys.get(1).toString());
		}catch(JSONException e){
			e.printStackTrace();
		}
	}

	@Test
	public void testVersion(){
		try{
			assertEquals(0, ct.getCurrentVersion());
			for(int i = 0; i < 10; i++){
				ct.addEntry(new JSONObject("{}"));
			}
			assertEquals(10, ct.getCurrentVersion());
		}catch(JSONException e){
			e.printStackTrace();
		}
	}


}
