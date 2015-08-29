package se.chalmers.fleetspeak.network.tcp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import se.chalmers.fleetspeak.core.ClientCreator;

public class TLSConnectionHandler{
	private int port;
	private ClientCreator clientCreator;
	private Executor executor;
	private Logger logger = Logger.getLogger("Debug");

	private ServerSocket serverSocket = null;

	private volatile boolean running;

	public TLSConnectionHandler(int port, ClientCreator cc) {
		this.port = port;
		clientCreator = cc;
		executor = Executors.newSingleThreadExecutor();
		executor.execute(connectionListener);
	}

	Runnable connectionListener = ()->{


		serverSocket = createServerSocket();
		if(serverSocket != null){
			try{
				logger.log(Level.INFO, "Starting server @LAN-IP "+InetAddress.getLocalHost().getHostAddress()+" on port "+port);
			}catch(Exception e){
				//I dont care that i cant print the local address
			}
			running = true;
		}else{
			logger.log(Level.SEVERE, "Failed to create serverSocket");
		}
		SSLSocket clientSocket;
		while(running){

			try{
				logger.log(Level.FINER, "Wainting for client");
				clientSocket = (SSLSocket) serverSocket.accept();
				logger.log(Level.INFO,"created socket" + clientSocket.getSession().getProtocol());
				clientCreator.addNewClient(clientSocket);
			}catch(SSLException e){
				e.printStackTrace();
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}};


		private ServerSocket createServerSocket() {
			ServerSocket socket = null;

			try {
				KeyStore ks = KeyStore.getInstance("JKS");
				ks.load(new FileInputStream("certificate/keystore"), "fleetspeak".toCharArray());

				KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
				kmf.init(ks, "fleetspeak".toCharArray());

				SSLContext context = SSLContext.getInstance("TLSv1.2");
				logger.log(Level.FINE, context.getProtocol());
				context.init(kmf.getKeyManagers(), null, null);

				SSLServerSocketFactory factory = context.getServerSocketFactory();
				socket = factory.createServerSocket(port);

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
			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}




			return socket;
		}

		public void terminate() {
			try{
				serverSocket.close();
			}catch(IOException e){
				e.printStackTrace();
			}

		}


}
