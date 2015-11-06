package se.chalmers.fleetspeak.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangeTracker{

	//TODO maybe not save all the things?
	private List<Entry> entrys;
	private int version = 0;

	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();


	public ChangeTracker(){
		entrys = new ArrayList<Entry>();
	}

	public JSONObject addEntry(JSONObject command){
		lock.writeLock().lock();
		try{
			try{
				command.put("structurestate", ++version);
			}catch(JSONException e){
				e.printStackTrace();
			}

			Entry e = new Entry(version, command);

			entrys.add(e);

		}finally{
			lock.writeLock().unlock();
		}
		return command;
	}

	public LinkedList<JSONObject> getChanges(int from){
		LinkedList<JSONObject> changes = null;
		lock.readLock().lock();
		try{
			if(from <= version && from >= 0){
				changes = new LinkedList<JSONObject>();
				int i = entrys.size()-1;
				while(i >= 0 && entrys.get(i).state >= from){
					changes.addFirst(entrys.get(i--).command);
				}
			} else
				return null;
		}finally{
			lock.readLock().unlock();
		}
		return changes;
	}

	public int getCurrentVersion(){
		int i;
		lock.readLock().lock();
		try{
			i = version;
		}finally{
			lock.readLock().unlock();
		}
		return i;
	}

	private class Entry{
		int state;
		JSONObject command;
		Entry(int state, JSONObject command){
			this.state = state;
			this.command = command;
		}
	}
}
