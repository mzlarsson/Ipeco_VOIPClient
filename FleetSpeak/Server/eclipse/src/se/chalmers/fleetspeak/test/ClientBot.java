package se.chalmers.fleetspeak.test;

public class ClientBot {

	private TCPBot tcpBot;
	private TCPModifierBot tcpMBot;
	private UDPBot udpBot;
	
	public ClientBot(String name, String ip, int port) {
		tcpBot = new TCPBot(name, ip, port);
		tcpBot.start();
//		tcpMBot = new TCPModifierBot(tcpBot);
		tcpMBot.start();
	}
	
	public static void main(String[] args) {
		String name = "VoltHacker";
		String ip = "192.168.43.36";
		int port = 8867;
		new ClientBot(name, ip, port);
	}
}
