package se.chalmers.fleetspeak;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ServerGUI extends JFrame implements ActionListener {

	private JLabel ip;
	private JTextField tcpText;
	private JTextField utpText;
	private JButton stop;
	private JButton start;
	private boolean running;
	private static ServerMain server;
	private static Thread serverThread;

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				if (server != null) {
					try {
						server.terminate();
						serverThread.join();
						server = null;
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}));

		new ServerGUI();
	}

	public ServerGUI() {
		setTitle("FleetSpeek Server");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setLocation(500, 400);
		populate();

		pack();
		setVisible(true);
	}

	private void populate() {
		try {
			ip = new JLabel("Your @LAN-IP is: "
					+ InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			ip = new JLabel("Your @LAN-IP is: NOT FOUND");
			e.printStackTrace();
		}
		add(ip, BorderLayout.NORTH);
		JPanel portsPanel = new JPanel();
		portsPanel.setLayout(new GridLayout(2, 2));
		JLabel tcpLabel = new JLabel("TCP-Port :");
		tcpText = new JTextField("8867", 4);
		JLabel utpLabel = new JLabel("UTP-Port :");
		utpText = new JTextField("8868", 4);
		portsPanel.add(tcpLabel);
		portsPanel.add(tcpText);
		portsPanel.add(utpLabel);
		portsPanel.add(utpText);
		add(portsPanel, BorderLayout.CENTER);
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(1, 2));
		stop = new JButton("Stop");
		start = new JButton("Start");
		stop.setEnabled(false);
		stop.addActionListener(this);
		start.addActionListener(this);
		buttons.add(stop);
		buttons.add(start);
		add(buttons, BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == stop) {
			if (running) {
				if (server != null) {
					try {
						server.terminate();
						serverThread.join();
						server = null;
						System.out.println("Server stopped.");
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				tcpText.setEnabled(true);
				utpText.setEnabled(true);
				start.setEnabled(true);
				stop.setEnabled(false);
				running = false;
			}
		} else if (e.getSource() == start) {
			if (!running) {
				serverThread = new Thread(new Runnable() {
					public void run() {
						try {
							server = new ServerMain(Integer.parseInt(tcpText
									.getText()),
									Integer.parseInt(utpText.getText()));
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				serverThread.start();
				tcpText.setEnabled(false);
				utpText.setEnabled(false);
				start.setEnabled(false);
				stop.setEnabled(true);
				running = true;
			}
		}
	}
}
