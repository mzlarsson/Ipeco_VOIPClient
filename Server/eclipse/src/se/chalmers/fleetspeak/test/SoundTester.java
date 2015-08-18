package se.chalmers.fleetspeak.test;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JFrame;

public class SoundTester extends JFrame{

	private static final long serialVersionUID = 1L;
	
	private Node start;
	
	private Image image;
	private int offset;
	
	public SoundTester(String filename){
		setSize(1500, 300);
		setVisible(true);
		
		Scanner sc;
		try{
			sc = new Scanner(new File(filename));
			Node current = null;
			while(sc.hasNextInt()){
				if(start == null){
					start = new Node(null, sc.nextInt());
					current = start;
				}else{
					current = new Node(current, sc.nextInt());
				}
			}
		}catch(IOException ioe){
			System.out.println(ioe.getMessage());
		}
		
		createImage();
	}
	
	public void createImage(){
		Node current = start.next;
		int halfHeight = 150;
		image = createImage(10000, halfHeight*2);
		Graphics g = image.getGraphics();
		int prevNum = start.getValue();
		int count = 1;
		do{
			g.drawLine(count-1, halfHeight-prevNum, count, halfHeight-current.getValue());
			prevNum = current.getValue();
			count++;
		}while((current = current.next) != null && count<10000);
	}
	
	public void run(){
		try{
			while(offset<8500){
				Thread.sleep(10);
				offset++;
				repaint();
			}
		}catch(InterruptedException ie){}
	}
	
	public void paint(Graphics g){
		g.drawImage(image, -offset, 0, null);
	}
	
	public static void main(String[] args){
		SoundTester t = new SoundTester("routerLog.txt");
		t.run();
	}
	
	public class Node{
		
		private Node next;
		private int num;
		
		public Node(Node prev, int num){
			if(prev != null){
				prev.next = this;
			}
			this.num = num;
		}
		
		public int getValue(){
			if(num<0){
				return num;
			}else{
				return 127-num;
			}
		}
	}
	
}
