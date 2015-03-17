package se.chalmers.fleetspeak.test;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import se.chalmers.fleetspeak.util.Command;

public class TCPModifierBot extends Thread{

	private TCPBot tcpBot;
	
	public TCPModifierBot(String name, String ip, int port){
		this.tcpBot = new TCPBot(name, ip, port);
		tcpBot.start();
	}
	
	@Override
	public void run(){
		Scanner sc;
		try{
			sc = new Scanner(new File("tcp_modbot_text.txt"));
			String cmd, key, value;
			int delay;
			while(sc.hasNextLine()){
				cmd = sc.next();
				key = sc.next();
				value = sc.next();
				delay = sc.nextInt();
				System.out.println("read "+cmd);
				if(!cmd.startsWith("//")){
					tcpBot.send(new Command(cmd, (key.equals("null")?null:key), value.equals("null")?null:value));
					Thread.sleep(delay);
				}
			}
		}catch(IOException ioe){
			System.out.println("IO Exception: "+ioe.getMessage());
		} catch (InterruptedException e) {}
	}
	
	public static void main(String[] args){
		String name = "VoltHacker";
		String ip = "129.16.72.50";
		int port = 8867;
		TCPModifierBot bot = new TCPModifierBot(name, ip, port);
		bot.start();
	}
}
