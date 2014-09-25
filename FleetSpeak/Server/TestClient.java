import java.io.*;
import java.lang.System;
import java.net.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class TestClient {
    private OutputStream outStream;
    private InputStream inputStream;
    private boolean muted;

    public static void main(String[] args) throws IOException,
            InterruptedException {
        String ip = (args != null && args.length > 0 ? args[0] : "localhost");
        int port = (args != null && args.length > 1 ? Integer.parseInt(args[1])
                : 8868);
        new TestClient(ip, port);
    }

    public TestClient(String ip, int port) {

        try {
            Socket socket = new Socket(ip, port);
            outStream = socket.getOutputStream();
            inputStream = socket.getInputStream();

            PrintWriter out = new PrintWriter(outStream, true);

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(
                    System.in));

            broadcastAudio();
            playbackAudio();
            DisplayMessage d = new DisplayMessage(in);
            d.start();

            String messageSent;

            while (true) {
                if ((messageSent = keyboard.readLine()) != null) {
                    out.println(messageSent);
                }

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    private void broadcastAudio() {
        try {
            final AudioFormat format = getFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            final TargetDataLine line = (TargetDataLine) AudioSystem
                    .getLine(info);
            line.open(format);
            line.start();
            Runnable runner = new Runnable() {
                ByteArrayOutputStream outAudio;
                int bufferSize = (int) format.getSampleRate()
                        * format.getFrameSize();
                byte buffer[] = new byte[bufferSize];

                public void run() {
                    outAudio = new ByteArrayOutputStream();
                    try {
                        while (!muted) {
                            int count = line.read(buffer, 0, buffer.length);
                            if (count > 0) {
                                System.out.println("Writing to socket");
                                outAudio.write(buffer, 0, count);
                                outStream.write(buffer, 0, count);
                            }
                        }
                        outAudio.close();
                    } catch (IOException e) {
                        System.err.println("I/O problems: " + e);
                        System.exit(-1);
                    }
                }
            };
            Thread captureThread = new Thread(runner);
            captureThread.setDaemon(true);
            captureThread.start();
        } catch (LineUnavailableException e) {
            System.err.println("Line unavailable: " + e);
            System.exit(-2);
        }
    }

    private void playbackAudio() throws IOException {
        try {
            final AudioFormat format = getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            final SourceDataLine line = (SourceDataLine) AudioSystem
                    .getLine(info);
            line.open(format);
            line.start();

            Runnable runner = new Runnable() {
                int bufferSize = (int) format.getSampleRate()
                        * format.getFrameSize();
                byte buffer[] = new byte[bufferSize];

                public void run() {
                    try {
                        int count;
                        while ((count = inputStream.read(buffer, 0, buffer.length)) != -1) {
                            if (count > 0) {
                                line.write(buffer, 0, count);
                            }
                        }
                        line.drain();
                        line.close();
                    } catch (IOException e) {
                        System.err.println("I/O problems: " + e);
                        System.exit(-3);
                    }
                }
            };
            Thread playThread = new Thread(runner);
            playThread.setDaemon(true);
            playThread.start();
        } catch (LineUnavailableException e) {
            System.err.println("Line unavailable: " + e);
            System.exit(-4);
        }
    }

    private AudioFormat getFormat() {
        float sampleRate = 44100;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
                bigEndian);
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    private class DisplayMessage extends Thread {
        private BufferedReader br;

        public DisplayMessage(BufferedReader br) {
            super("DisplayMessage");
            this.br = br;
        }

        public void run() {
            try {
                while (true) {
                    System.out.print(br.readLine() + "\n");

                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

    }

}