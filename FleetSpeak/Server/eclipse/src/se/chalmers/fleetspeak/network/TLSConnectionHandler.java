package se.chalmers.fleetspeak.network;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import se.chalmers.fleetspeak.core.ClientCreator;

public class TLSConnectionHandler {
	private int port;
	private ClientCreator clientCreator;
	private Executor executor;
	private Logger logger;

	private ServerSocket serverSocket = null;

	private volatile boolean running;

	public TLSConnectionHandler(int port){
		this.port = port;
		executor = Executors.newSingleThreadExecutor();
		executor.execute(connectionListener);
	}

	Runnable connectionListener = ()->{
		running = true;
		//logger.log(Level.INFO, "Starting to listen to port " + port);
		try{

			SSLContext context = SSLContext.getInstance("TLSv1.2");
			System.out.println(context.getProtocol());
			context.init(getKeyManagers(), null, null);
			SSLServerSocketFactory factory = context.getServerSocketFactory();
			serverSocket = factory.createServerSocket(port);

		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
			//logger.log(Level.SEVERE, e.getMessage());
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		try{
			SSLSocket clientSocket = (SSLSocket)serverSocket.accept();
			System.out.println("got a connection");
			BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			System.out.println("reading");
			System.out.println(br.readLine());
		}catch(IOException e){
			e.printStackTrace();
		}
		System.out.println("closing");
		terminate();
	};

	private static KeyManager[] getKeyManagers(){
		KeyManagerFactory kmf = null;
		try {
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream("/Users/Nieo/Documents/Certs/keystore"), "fleetspeak".toCharArray());
			kmf = KeyManagerFactory.getInstance(KeyManagerFactory
					.getDefaultAlgorithm());

			kmf.init(ks, "fleetspeak".toCharArray());
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return kmf.getKeyManagers();
	}


	public static void main(String[]args){
		new TLSConnectionHandler(8867);
		try {
			SSLContext context = SSLContext.getInstance("TLSv1.2");
			context.init(getKeyManagers(), null, null);
			SSLSocketFactory factory = context.getSocketFactory();
			SSLSocket socket = (SSLSocket) factory.createSocket("127.0.0.1", 8867);
			socket.startHandshake();
			PrintWriter pw = new PrintWriter(socket.getOutputStream());
			System.out.println("sending");
			pw.println("herpaderp");
			pw.flush();
			System.out.println("done");

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private void terminate() {
		try{
			serverSocket.close();
		}catch(IOException e){
			e.printStackTrace();
		}

	}

}
