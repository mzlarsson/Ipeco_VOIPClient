import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;


public class SoundHandler extends Handler{
	
	private static final int bufferSize = 8000;

	public SoundHandler(Socket clientSocket) {
		super(clientSocket);
	}

	@Override
	public void run(){
		try{
			OutputStream out = this.getOutputStream();
			InputStream in = this.getInputStream();
			
			byte[] data = new byte[bufferSize];
			while(true){
				in.read(data);
				ServerMain.sendMessage(this, data);
				out.flush();
				Arrays.fill(data, (byte)0);
			}
		}catch(IOException e){
			System.out.println("[SoundHandler] "+e.getMessage());
			this.terminate();
		}
	}
}
