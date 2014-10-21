package se.chalmers.fleetspeak;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFrame;

public class TmpGraph extends JFrame implements Runnable{
	
	private static final long serialVersionUID = 1L;
	public static AudioFormat AUDIOFORMAT = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 8000.0F, 8, 1, 1, 8000.0F, false);
	
	private Graphics bufferGraphics = null;
	private Image bufferImage = null;
	private int counter = 0;

	private static final int WIDTH = 1500;
	private static final int HEIGHT = 800;
	private static final int BORDER = 100;
	
	private int imageWidth = 100*WIDTH;

	public static void main(String[] args) throws LineUnavailableException{
		new Thread(new TmpGraph()).start();
	}
	
	public TmpGraph(){
		super();
		this.setSize(WIDTH, HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
		bufferImage = this.createImage(imageWidth, HEIGHT);
		bufferGraphics = bufferImage.getGraphics();
		drawImageBackground();
	}
	
	@Override
	public void run(){
        TargetDataLine line = null;
        Mixer mixer = AudioSystem.getMixer(null);
		SourceDataLine sourceLine = null;
        try{
        	line = AudioSystem.getTargetDataLine(AUDIOFORMAT);
        	line.open();
        	line.start();
			sourceLine = (SourceDataLine)mixer.getLine(new DataLine.Info(SourceDataLine.class, AUDIOFORMAT));
			sourceLine.open();
			sourceLine.start();
        }catch(LineUnavailableException lue){
        	System.out.println("Line unavailable");
        }
		
        int maxHeight = HEIGHT/2-BORDER;
        int centerHeight = HEIGHT/2;
        int lastX = 0, lastY = centerHeight;
        boolean signed = true;
//        StringBuffer b = new StringBuffer();
        Scanner sc = null;
//        Scanner sc2 = null;
        try {
			sc = new Scanner(new BufferedReader(new FileReader("savedata/mixer1/mixer.log")));
//			sc2 = new Scanner(new BufferedReader(new FileReader("savedata/mixer1/235971083.log")));
		} catch (FileNotFoundException e1) {e1.printStackTrace();}
		while(true){
			if(this.bufferGraphics != null){
				try{
					byte[] data = new byte[160];
	//				byte[] data2 = new byte[160];
	//				byte[] data3 = new byte[160];
	//				line.read(data3, 0, data.length);
					for(int i = 0; i<data.length; i++){
						data[i] = (byte)sc.nextInt();
	//					data2[i] = (byte)sc2.nextInt();
					}
					
	//				byte[] mixData = new byte[160];
	//				mixData = TmpGraphMixer.mixSounds(false, data, data2, data3);
					
	//				sourceLine.write(data, 0, 160);
					
					int h = 0;
					double r = 0;
					for(int i = 0; i<data.length; i++){
						r = TmpGraphMixer.byteRatio(data[i], signed);
						h = (centerHeight+(int)(r*maxHeight));
						bufferGraphics.drawLine(lastX, lastY, counter+1, h);
						
						counter++;
							
						lastX = counter;
						lastY = h;
					}
					
	//				try{Thread.sleep(30);}catch(InterruptedException ioe){}
					
					if(counter>=imageWidth){
						counter = 0;
						drawImageBackground();
					}
					
					repaint();
				}catch(NoSuchElementException nsee){
					sc.close();
				}
			}
		}
		
//		try {
//			BufferedWriter out = new BufferedWriter(new FileWriter("savedata/mixer1/mixed.log"));
//			out.write(b.toString());
//			out.flush();
//			out.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	public void drawImageBackground(){
		System.out.println("Drawing background!");
		bufferGraphics.clearRect(0, 0, imageWidth, HEIGHT);
		int h = HEIGHT/2;
		bufferGraphics.setColor(Color.magenta);
		bufferGraphics.drawLine(0, h, imageWidth, h);
		bufferGraphics.setColor(Color.black);
	}
	
	public void paint(Graphics g){
		if(bufferImage != null){
			int scroll = (counter<1200?0:1200-counter);
			g.drawImage(bufferImage, scroll, 0, null);
		}
	}
}
