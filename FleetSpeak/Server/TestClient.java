import java.io.*;
import java.lang.System;
import java.net.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class TestClient {
	private Socket socket;
    private OutputStream outStream;
    private InputStream inputStream;
    private boolean muted;

    public static void main(String[] args) throws IOException,
            InterruptedException {
        String ip = (args != null && args.length > 0 ? args[0] : "localhost");
        int port = (args != null && args.length > 1 ? Integer.parseInt(args[1]) : 8868);
        
        new TestClient(ip, port);
    }

    public TestClient(String ip, int port) {
        final AudioFormat format = getFormat();
        int bufferSize = (int) format.getSampleRate()*format.getFrameSize();
        System.out.println("Using "+bufferSize+" as buffer size");
        
        try {
            socket = new Socket(ip, port);
            outStream = socket.getOutputStream();
            inputStream = socket.getInputStream();

            broadcastAudio();
            playbackAudio();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    private void broadcastAudio() {
        try {
            final AudioFormat format = getFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            Runnable runner = new Runnable() {
                ByteArrayOutputStream outAudio;
                int bufferSize = (int) format.getSampleRate()*format.getFrameSize();
                byte buffer[] = new byte[bufferSize];

                public void run() {
                    outAudio = new ByteArrayOutputStream();
                    try {
                        while (true) {
                        	if(!muted){
	                            int count = line.read(buffer, 0, buffer.length);
	                            if (count > 0) {
	                                System.out.println("Writing to socket");
	                                outAudio.write(buffer, 0, count);
	                                outStream.write(buffer, 0, count);
	                            }
                        	}
                        }
                    } catch (IOException e) {
                        System.err.println("I/O problems: " + e);
                        System.exit(-1);
                    }
                    
                    try {
                    	if(outAudio != null){
                    		outAudio.close();
                    	}
					} catch (IOException e) {}
                }
            };
            Thread captureThread = new Thread(runner);
            //captureThread.setDaemon(true);
            captureThread.start();
        } catch (LineUnavailableException e) {
            System.err.println("Line unavailable: " + e);
            System.exit(-2);
        }
    }

    private void playbackAudio() throws IOException {
        try {
            final AudioFormat format = getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            Runnable runner = new Runnable() {
                int bufferSize = (int) format.getSampleRate()*format.getFrameSize();
                byte buffer[] = new byte[bufferSize];

                public void run() {
                    try {
                    	while(true){
	                        int count;
	                        while ((count = inputStream.read(buffer, 0, buffer.length)) != -1) {
	                            if (count > 0) {
	                                line.write(buffer, 0, count);
	                            }
	                        }
                    	}
                    } catch (IOException e) {
                        System.err.println("I/O problems: " + e);
                        System.exit(-3);
                    }

                    if(line != null){
	                	line.drain();
	                	line.close();
                    }
                }
            };
            Thread playThread = new Thread(runner);
            //playThread.setDaemon(true);
            playThread.start();
        } catch (LineUnavailableException e) {
            System.err.println("Line unavailable: " + e);
            System.exit(-4);
        }
    }

    private AudioFormat getFormat() {
        float sampleRate = 8000;
        int sampleSizeInBits = 8;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

}