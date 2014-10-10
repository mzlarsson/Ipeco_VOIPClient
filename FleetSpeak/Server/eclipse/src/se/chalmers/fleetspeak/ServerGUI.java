package se.chalmers.fleetspeak;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

public class ServerGUI extends JFrame implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;
	private JLabel ip;
	private JTextField tcpText;
	private JTextField udpText;
	private JButton stop;
	private JButton start;
	JScrollPane scrollableText;
	private JTextArea terminal;
	private JTextField cmdLine;
	
	private static ServerMain server;
	private static Thread serverThread;
	private Logger log;

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
		setLocation(500, 200);
		setupLogger();
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
		// Panels containing several components for better structure.
		JPanel west = new JPanel();
		JPanel westBorder = new JPanel();
		westBorder.setLayout(new BorderLayout());
		JPanel center = new JPanel();
		center.setLayout(new BorderLayout());
		JPanel centerSouth = new JPanel();
		centerSouth.setLayout(new BorderLayout());
		
		// The area for input of what ports to use.
		westBorder.add(ip, BorderLayout.NORTH);
		JPanel portsPanel = new JPanel();
		portsPanel.setLayout(new GridLayout(2, 2));
		JLabel tcpLabel = new JLabel("TCP-Port :");
		tcpText = new JTextField("8867", 4);
		JLabel utpLabel = new JLabel("UDP-Port :");
		udpText = new JTextField("8868", 4);
		portsPanel.add(tcpLabel);
		portsPanel.add(tcpText);
		portsPanel.add(utpLabel);
		portsPanel.add(udpText);
		westBorder.add(portsPanel, BorderLayout.CENTER);
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(1, 2));
		stop = new JButton("Stop");
		start = new JButton("Start");
		stop.setEnabled(false);
		stop.addActionListener(this);
		start.addActionListener(this);
		buttons.add(stop);
		buttons.add(start);
		westBorder.add(buttons, BorderLayout.SOUTH);
		
		// The scrollable terminal window.
		terminal = new JTextArea();
		DefaultCaret caret = (DefaultCaret)terminal.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scrollableText = new JScrollPane(terminal);
		terminal.setEditable(false);
		scrollableText.setPreferredSize(new Dimension(500,500));
		center.add(scrollableText, BorderLayout.CENTER);
		west.add(westBorder);
		
		// The command input line.
		JLabel cmdLineLabel = new JLabel("Command input:");
		cmdLine = new JTextField(10);
		cmdLine.addKeyListener(this);
		centerSouth.add(cmdLineLabel, BorderLayout.WEST);
		centerSouth.add(cmdLine, BorderLayout.CENTER);
		center.add(centerSouth, BorderLayout.SOUTH);
		add(west, BorderLayout.WEST);
		add(center, BorderLayout.CENTER);
	}

	private void setupLogger() {
		log = Logger.getGlobal();
		log.setLevel(Level.ALL);
		Handler logHandler = new Handler() {
			
			@Override
			public void publish(LogRecord record) {
				terminal.append(record.getMessage()+"\n");
				scrollableText.getVerticalScrollBar().setValue(scrollableText.getVerticalScrollBar().getMaximum());
			}
			
			@Override
			public void flush() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void close() throws SecurityException {
				// TODO Auto-generated method stub
				
			}
		};
		log.addHandler(logHandler);
		Log.setupLogger(log);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == stop) {
			if (server != null) {
				log.log(Level.ALL, "Terminating server...");
				try {
					server.terminate();
					serverThread.join();
					server = null;
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				log.log(Level.ALL, "Server stopped.");
				tcpText.setEnabled(true);
				udpText.setEnabled(true);
				start.setEnabled(true);
				stop.setEnabled(false);
			}
		} else if (e.getSource() == start) {
			if (server == null) {
				serverThread = new Thread(new Runnable() {
					public void run() {
						try {
							server = new ServerMain(Integer.parseInt(tcpText
									.getText()),
									Integer.parseInt(udpText.getText()));
							server.start();
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				serverThread.start();
				tcpText.setEnabled(false);
				udpText.setEnabled(false);
				start.setEnabled(false);
				stop.setEnabled(true);
			}
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_ENTER) {
			log.log(Level.ALL, "COMMAND INPUT: " + cmdLine.getText());
			cmdLine.setText("");
		}		
	}
}
