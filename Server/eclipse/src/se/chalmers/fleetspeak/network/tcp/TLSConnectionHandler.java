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
import java.util.concurrent.ExecutorService;
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
	private ExecutorService handshakeThreads;
	private Logger logger = Logger.getLogger("Debug");

	private ServerSocket serverSocket = null;

	private volatile boolean running;

	public TLSConnectionHandler(int port, ClientCreator cc) {
		this.port = port;
		clientCreator = cc;
		executor = Executors.newSingleThreadExecutor();
		executor.execute(connectionListener);
		handshakeThreads = Executors.newFixedThreadPool(10);
	}

	Runnable connectionListener = ()->{


		serverSocket = createServerSocket();
		if(serverSocket != null){
			try{
				logger.log(Level.INFO, "Starting server @LAN-IP "+InetAddress.getLocalHost().getHostAddress()+" on port "+port);
			}catch(Exception e){
				//I dont care that i cant print the local address
				//BUT I DO!
				logger.severe("CAN'T PRINT LOCALHOST IP ADDRESS");
			}
			running = true;
		}else{
			logger.log(Level.SEVERE, "Failed to create serverSocket");
		}
		while(running){

			try{
				logger.log(Level.FINER, "Wainting for client");
				final SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
				handshakeThreads.execute(() -> {
					logger.log(Level.FINER, "Starting TLS handshake");
					try{
						clientSocket.startHandshake();
						
						//Handshake succeeded
						logger.log(Level.FINER, "Finished TLS handshake");
						logger.log(Level.INFO, "Created socket" + clientSocket.getSession().getProtocol());
						clientCreator.addNewClient(clientSocket);
					}catch (IOException e){
						logger.log(Level.SEVERE, "Failed with TLS handshake", e);
					}
				});
			}catch(SSLException e){
				logger.log(Level.SEVERE, "Failed with TLS initialization", e);
			}catch(IOException e){
				logger.log(Level.SEVERE, "Failed with socket initialization", e);
			}
		}};


		private ServerSocket createServerSocket() {
			ServerSocket socket = null;

			try {
				KeyStore ks = KeyStore.getInstance("JKS");
				ks.load(new FileInputStream("certificate/keystore"), "fleetspeak".toCharArray());

				KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
				kmf.init(ks, "fleetspeak".toCharArray());

				SSLContext context = SSLContext.getInstance("TLSv1");
				logger.log(Level.FINE, context.getProtocol());
				context.init(kmf.getKeyManagers(), null, null);

				SSLServerSocketFactory factory = context.getServerSocketFactory();
				socket = factory.createServerSocket(port);

			} catch (KeyStoreException e) {
				logger.log(Level.SEVERE, "Could not create SSLSocket: KeyStoreException.", e);
			} catch (NoSuchAlgorithmException e) {
				logger.log(Level.SEVERE, "Could not create SSLSocket: NoSuchAlgorithmException.", e);
			} catch (CertificateException e) {
				logger.log(Level.SEVERE, "Could not create SSLSocket: CertificateException.", e);
			} catch (FileNotFoundException e) {
				logger.log(Level.SEVERE, "Could not create SSLSocket: FileNotFoundException.", e);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Could not create SSLSocket: IOException.", e);
			} catch (UnrecoverableKeyException e) {
				logger.log(Level.SEVERE, "Could not create SSLSocket: UnrecoverableKeyException.", e);
			} catch (KeyManagementException e) {
				logger.log(Level.SEVERE, "Could not create SSLSocket: KeyManagementException.", e);
			}




			return socket;
		}

		public void terminate() {
			try{
				running = false;
				if(serverSocket != null){
					serverSocket.close();
				}
				((ExecutorService)executor).shutdownNow();
			}catch(IOException e){
				logger.warning("Could not close server socket. Check if the software has been terminated correctly.");
			}

		}


}
