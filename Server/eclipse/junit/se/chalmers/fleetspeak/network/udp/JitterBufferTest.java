package se.chalmers.fleetspeak.network.udp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;

public class JitterBufferTest {

	boolean errors = false;

	JitterBuffer<Integer> buffer;

	Integer[] ints = {1,2,3,4};

	@Before
	public void setUp(){
		buffer = new JitterBuffer<Integer>();
	}

	@Test
	public void TestReadWrite(){
		assertNull(buffer.read());
		buffer.write(ints[0], ints[0]);
		assertNull(buffer.read());

		for(int i = 1; i < 4; i++) {
			buffer.write(ints[i], ints[i]);
		}
		for(int i = 0;i < 4; i++){
			assertEquals(ints[i], buffer.read());
		}
		buffer.write(0,0);
		assertNull(buffer.read());
	}

	@Test
	public void TestSyncronization(){

		Runnable reader = () ->{
			try{
				for(int i = 0; i < 100; i++){
					buffer.read();
				}
			}catch(Exception e){
				errors = true;
			}

		};
		Runnable writer = ()->{
			try{
				for(int i = 0; i < 100; i++){
					buffer.write(i,i);
				}
			}catch(Exception e){
				errors = true;
			}
		};
		Executor e = Executors.newFixedThreadPool(2);
		e.execute(reader);
		e.execute(writer);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		assertFalse(errors);
	}



}
