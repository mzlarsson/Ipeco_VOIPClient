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
import se.chalmers.fleetspeak.eventbus.IEventBusSubscriber;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.Log;

public class ServerGUI extends JFrame implements ActionListener, KeyListener, IEventBusSubscriber {

	private static final long serialVersionUID = 1L;
	private JLabel ip;
	private JLabel roomStructure;
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

	private ArrayList<String> cmdLog;
	private ArrayList<ServerCommand> searchCmds;
	private int cmdLogIndex, searchIndex;
	private Color FOREST_GREEN = new Color(20, 170, 0);
	
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
		setTitle("FleetSpeek Server: NullpointerExeption(\"Server not found\")");
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
		JPanel westSide = new JPanel();
		westSide.setLayout(new BorderLayout());
		JPanel westButtonGroup = new JPanel();
		westButtonGroup.setLayout(new BorderLayout());
		JPanel center = new JPanel();
		center.setLayout(new BorderLayout());
		JPanel centerSouth = new JPanel();
		centerSouth.setLayout(new BorderLayout());
		
		// The area for input of what ports to use.
		westButtonGroup.add(ip, BorderLayout.NORTH);
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
		westButtonGroup.add(portsPanel, BorderLayout.CENTER);
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(1, 2));
		stop = new JButton("Stop");
		start = new JButton("Start");
		stop.setEnabled(false);
		stop.addActionListener(this);
		start.addActionListener(this);
		buttons.add(stop);
		buttons.add(start);
		roomStructure = new JLabel("No clients connected");
		westButtonGroup.add(buttons, BorderLayout.SOUTH);
		westSide.add(westButtonGroup, BorderLayout.CENTER);
		westSide.add(roomStructure, BorderLayout.SOUTH);
		west.add(westSide);
		
		// The scrollable terminal window.
		terminal = new JTextPane();
		DefaultCaret caret = (DefaultCaret)terminal.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scrollableText = new JScrollPane(terminal);
		terminal.setEditable(false);
		scrollableText.setPreferredSize(new Dimension(700,500));
		center.add(scrollableText, BorderLayout.CENTER);
		
		// The command input line.
		JLabel cmdLineLabel = new JLabel("Command input:");
		cmdLine = new JTextField(10);
		cmdLine.addKeyListener(this);
		cmdLine.setFocusTraversalKeysEnabled(false);
		cmdLog = new ArrayList<String>();
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
					} else if (msg.equals("/i")) {
							StyleConstants.setItalic(attr, false);
					} else if (msg.equals("/error")) {
						StyleConstants.setForeground(attr, Color.BLACK);
					} else if (msg.equals("/info")) {
						StyleConstants.setForeground(attr, Color.BLACK);
					} else if (msg.equals("/debug")) {
						StyleConstants.setForeground(attr, Color.BLACK);
					} else {
						return false;
					}
				} else {
					if (msg.equals("b")) {
						StyleConstants.setBold(attr, true);
					} else if (msg.equals("i")) {
						StyleConstants.setItalic(attr, true);
					} else if (msg.equals("error")) {
						StyleConstants.setForeground(attr, Color.RED);
					} else if (msg.equals("info")) {
						StyleConstants.setForeground(attr, FOREST_GREEN);
					} else if (msg.equals("debug")) {
						StyleConstants.setForeground(attr, Color.MAGENTA);
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
			EventBus.getInstance().addSubscriber(this);
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
				EventBus.getInstance().removeSubscriber(this);
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
		// This is for using the TAB key to auto-complete
		if (e.getKeyCode()==KeyEvent.VK_TAB) {
			if (searchCmds == null) {
				searchCmds = ServerCommand.getPossibleCommands(cmdLine.getText());
				searchIndex = 0;
			}
			if (!searchCmds.isEmpty()) {
				cmdLine.setText(searchCmds.get(searchIndex).getName());
				searchIndex = (searchIndex+1)%searchCmds.size();
			}
		} else {
			if (e.getKeyCode()==KeyEvent.VK_ENTER) {
				String cmd = cmdLine.getText();
				cmdLog.add(cmd);
				cmdLogIndex = -1;
				log.log(Level.ALL, "<b>> "+cmd+"</b>");
				EventBus.getInstance().fireEvent(new EventBusEvent("CommandHandler", new Command("consoleCommand", null, cmd), this));
	
				cmdLine.setText("");
			} else if (e.getKeyCode()==KeyEvent.VK_UP) {
				cmdLogIndex = (cmdLogIndex-1)<0 ? cmdLog.size()-1 : cmdLogIndex-1;
				cmdLine.setText(cmdLog.get(cmdLogIndex));
			} else if (e.getKeyCode()==KeyEvent.VK_DOWN) {
				cmdLogIndex = (cmdLogIndex+1)%cmdLog.size();
				cmdLine.setText(cmdLog.get(cmdLogIndex));
			}
			searchCmds = null;
		}
	}

	@Override
	public void eventPerformed(EventBusEvent event) {
		if (event.getReciever().equals("ServerGUI")) {
			Command cmd = event.getCommand();
			if (cmd.getCommand().equals("roomsChanged")) {
				roomStructure.setText((String)cmd.getValue());
			}
		}
	}
	
}
