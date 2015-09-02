package se.chalmers.fleetspeak.test.sound;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import se.chalmers.fleetspeak.sound.BufferedAudioStream;
import se.chalmers.fleetspeak.sound.Mixer;
import se.chalmers.fleetspeak.sound.MixerFactory;

public class MixerTest {

	private Mixer m;
	
	public static void main(String[] args){
		new MixerTest(true);
//		SongHandler.playAudioLocally(1);
	}
	
	public MixerTest(boolean playBack){
		m = MixerFactory.getDefaultMixer();
		String[] filenames = {"test2", "test1", "test3"};
		for(int i = 0; i<filenames.length; i++){
			MixerTestOutputAudioBuffer out = new MixerTestOutputAudioBuffer(filenames[i]);
			out.start();
			MixerTestInputAudioBuffer in = new MixerTestInputAudioBuffer(filenames[i]);
			m.addStream(in, out.getQueue());
		}
		
		if(playBack){
			MixerTestImmediateAudioBuffer mixerOutput = new MixerTestImmediateAudioBuffer("silent");
			m.addStream(new MixerTestInputAudioBuffer("silent"), mixerOutput.getQueue());
			new Thread(m).start();
			SongHandler.playAudioLocally(mixerOutput);
		}else{
			MixerTestOutputAudioBuffer out = new MixerTestOutputAudioBuffer("silent");
			out.start();
			m.addStream(new MixerTestInputAudioBuffer("silent"), out.getQueue());
			new Thread(m).start();
		}
	}
	
	public class MixerTestImmediateAudioBuffer extends InputStream{
		private BlockingQueue<byte[]> queue;
		public MixerTestImmediateAudioBuffer(String filename){
			queue = new LinkedBlockingQueue<byte[]>();
		}
		
		public BlockingQueue<byte[]> getQueue(){
			return queue;
		}
		
		@Override
		public int available(){
			return 1;
		}

		@Override
		public int read() throws IOException {
			throw new IllegalArgumentException("Use method read(byte[]) instead.");		//TODO FIXME lol.
		}
		
		@Override
		public int read(byte[] arr){
			return read(arr, 0, arr.length);
		}
		
		@Override
		public int read(byte[] arr, int offset, int length){
			try {
				byte[] b = queue.take();
				System.arraycopy(b, 0, arr, offset, length);
				return b.length;
			} catch (InterruptedException e) {
				e.printStackTrace();
				return -1;
			}
		}
	}
	
	public class MixerTestOutputAudioBuffer extends Thread{
		private BlockingQueue<byte[]> queue;
		private OutputStream output;
		public MixerTestOutputAudioBuffer(String filename){
			try {
				output = new FileOutputStream(new File(Constants.MUSIC_BASEDIR+filename+"_mix.bad"));
			} catch (FileNotFoundException e) {
				System.out.println("File not found: "+filename+"_mix.bad");
			}
			queue = new LinkedBlockingQueue<byte[]>();
		}
		
		public BlockingQueue<byte[]> getQueue(){
			return queue;
		}
		
		@Override
		public void run(){
			byte[] tmp;
			try {
				while((tmp = queue.take()) != null){
					output.write(tmp, 0, tmp.length);
				}
				output.flush();
				output.close();
			} catch (InterruptedException e) {
				System.out.println("Was interrupted!");
			} catch (IOException e) {
				System.out.println("Got an IO exception on output write");
			}
		}
	}
	
	public class MixerTestInputAudioBuffer implements BufferedAudioStream{
		private InputStream in;
		private byte[] storage = new byte[320];
		private boolean removalOnNextRead = false;
		public MixerTestInputAudioBuffer(String filename){
			try {
				in = new FileInputStream(new File(Constants.MUSIC_BASEDIR+filename+".bad"));
			} catch (FileNotFoundException e) {
				System.out.println("Could not find input file: "+filename);
			}
		}

		@Override
		public byte[] read() {
			try {
				if(removalOnNextRead){
					MixerTest.this.m.removeStream(this);
					in.close();
					return null;
				}
				
				int readBytes = in.read(storage, 0, storage.length);
				if(readBytes==0){
					removalOnNextRead = true;
					System.out.println("Removal on next");
				}
				return storage;
			} catch (IOException e) {
				System.out.println("Failed to find data from in stream");
				return null;
			}
		}
		
	}
	
}
