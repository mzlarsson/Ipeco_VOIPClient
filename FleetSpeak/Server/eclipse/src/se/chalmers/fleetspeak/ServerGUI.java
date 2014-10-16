package se.chalmers.fleetspeak;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import se.chalmers.fleetspeak.eventbus.EventBus;
import se.chalmers.fleetspeak.eventbus.EventBusEvent;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.Log;

public class ServerGUI extends JFrame implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;
	private JLabel ip;
	private JTextField tcpText;
	private JTextField udpText;
	private JButton stop;
	private JButton start;
	JScrollPane scrollableText;
	private JTextPane terminal;
	private JTextField cmdLine;
	
	private static ServerMain server;
	private static Thread serverThread;
	private Logger log;

	private ArrayList<ServerCommand> searchCmds;
	private int searchIndex;
	
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
		terminal = new JTextPane();
		DefaultCaret caret = (DefaultCaret)terminal.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scrollableText = new JScrollPane(terminal);
		terminal.setEditable(false);
		scrollableText.setPreferredSize(new Dimension(700,500));
		center.add(scrollableText, BorderLayout.CENTER);
		west.add(westBorder);
		
		// The command input line.
		JLabel cmdLineLabel = new JLabel("Command input:");
		cmdLine = new JTextField(10);
		cmdLine.addKeyListener(this);
		cmdLine.setFocusTraversalKeysEnabled(false);
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
				try {
					String msg = record.getMessage();
					StyledDocument doc = terminal.getStyledDocument();
					SimpleAttributeSet attributes = new SimpleAttributeSet();
					
					if (msg.indexOf("<")==-1) {
						doc.insertString(doc.getLength(), msg+"\n", null);
					} else {
						int tagStart = msg.indexOf("<");
						int tagEnd = msg.indexOf(">", tagStart);
						while (tagStart!=-1) {
							doc.insertString(doc.getLength(), msg.substring(0, tagStart), attributes);
							if (tagEnd!=-1) {
								if (!setAttributes(msg.substring(tagStart+1, tagEnd), attributes)) {
									doc.insertString(doc.getLength(), msg.substring(tagStart, tagEnd+1), attributes);
								}
								msg = msg.substring(tagEnd+1);
							} else {
								doc.insertString(doc.getLength(), "<", attributes);
								msg = msg.substring(1);
							}
							tagStart = msg.indexOf("<");
							tagEnd = msg.indexOf(">", tagStart);
						}
						doc.insertString(doc.getLength(), msg+"\n", null);
					}
				} catch (BadLocationException e) {}
				scrollableText.getVerticalScrollBar().setValue(scrollableText.getVerticalScrollBar().getMaximum());
			}
			
			// Returns true if the given string is an actual attribute.
			private boolean setAttributes(String msg, SimpleAttributeSet attr) {
				if (msg.startsWith("/")) {
					if (msg.equals("/b")) {
						StyleConstants.setBold(attr, false);
					} else if (msg.equals("/error")) {
						StyleConstants.setForeground(attr, Color.BLACK);
					} else {
						return false;
					}
				} else {
					if (msg.equals("b")) {
						StyleConstants.setBold(attr, true);
					} else if (msg.equals("error")) {
						StyleConstants.setForeground(attr, Color.RED);
					} else {
						return false;
					}
				}
				return true;
			}
			
			@Override
			public void flush() {
				terminal.setText("");
			}
			
			@Override
			public void close() throws SecurityException {
				// TODO Auto-generated method stub
				
			}
		};
		log.addHandler(logHandler);
		Log.setupLogger(log);
	}

	public void start() {
		if (server == null) {
			terminal.setText("");
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

	public void stop() {
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
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == stop) {
			stop();
		} else if (e.getSource() == start) {
			start();
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
		if (e.getKeyCode()==KeyEvent.VK_TAB) {
			if (searchCmds == null) {
				searchCmds = ServerCommand.getPossibleCommands(cmdLine.getText());
				searchIndex = 0;
			}
			if (!searchCmds.isEmpty()) {
				cmdLine.setText(searchCmds.get(searchIndex).getName());
			}
		} else {
			if (e.getKeyCode()==KeyEvent.VK_ENTER) {
				String cmd = cmdLine.getText();
				log.log(Level.ALL, "<b>> "+cmd+"</b>");
				EventBus.getInstance().fireEvent(new EventBusEvent("CommandHandler", new Command("consoleCommand", null, cmd), this));
	
				cmdLine.setText("");
			}
			searchCmds = null;
		}
	}
}
