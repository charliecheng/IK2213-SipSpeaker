/* This file is based on 
 * http://www.anyexample.com/programming/java/java_play_wav_sound_file.xml
 * Please see the site for license information.
 */
	 
//package jlibrtpDemos;


import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.lang.String;
import java.net.DatagramSocket;
import java.util.Enumeration;
import jlibrtp.*;
import java.nio.file.*;
/**
 * @author Arne Kepp
 */
public class RTPSender implements RTPAppIntf  {
	static AudioFormat alawformat= new AudioFormat(AudioFormat.Encoding.ALAW,8000,8,1,1,8000,false);
    	static AudioFormat ulawformat= new AudioFormat(AudioFormat.Encoding.ULAW,8000,8,1,1,8000,false);
	public RTPSession rtpSession = null;
	static int pktCount = 0;
	static int dataCount = 0;
	private String filename="currentmessageINPCMA.wav";
	private final int EXTERNAL_BUFFER_SIZE = 1024;
	SourceDataLine auline;
	private Position curPosition;
	int localport;
	 enum Position {
		LEFT, RIGHT, NORMAL
	};
        static int x=0;
	public static long length;
	public RTPSender(int localport)  {
		DatagramSocket rtpSocket = null;
		DatagramSocket rtcpSocket = null;
		this.localport = localport;
		try {
			rtpSocket = new DatagramSocket(localport);
			rtcpSocket = new DatagramSocket(localport+1);
                        //System.out.println("Used rtp port in rtpsocket "+localport);
		} catch (Exception e) {
			System.out.println("RTPSession failed to obtain port");
		}
		
		
		rtpSession = new RTPSession(rtpSocket, rtcpSocket);
		rtpSession.RTPSessionRegister(this,null, null);
		//System.out.println("CNAME: " + rtpSession.CNAME());
		
	}
	
	/**
	 * @param args
	
	public static void main(String[] args) {
		for(int i=0;i<args.length;i++) {
			System.out.println("args["+i+"]" + args[i]);
		}
			
		if(args.length == 0) {
			args = new String[4];
			args[1] = "192.168.0.104";
			args[0] = "currentmessageINPCMU.wav";
			args[2] = "7078";
			args[3] = "7079";
		}
		
		SoundSenderDemo aDemo = new SoundSenderDemo(false);
		Participant p = new Participant(args[1],Integer.parseInt(args[2]),Integer.parseInt(args[2]) + 1);
		aDemo.rtpSession.addParticipant(p);
		aDemo.filename = args[0];
		aDemo.run();
		System.out.println("pktCount: " + pktCount);
	}*/
	
	public void receiveData(DataFrame dummy1, Participant dummy2) {
		// We don't expect any data.
	}
	
	public void userEvent(int type, Participant[] participant) {
		//Do nothing
	}
	
	public int frameSize(int payloadType) {
		return 1;
	}
	
	public void run() {
		if(RTPSession.rtpDebugLevel > 1) {
			System.out.println("-> Run()");
		} 
		
                try {
                    Path FROM = Paths.get(filename);
                    Path TO = Paths.get(filename+x);
    //overwrite existing file, if exists
                    CopyOption[] options = new CopyOption[]{
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.COPY_ATTRIBUTES
                    }; 
                    Files.copy(FROM, TO, options);
                }catch (Exception exc){}
                File soundFile = new File(filename+x);
		if (!soundFile.exists()) {
			System.err.println("Wave file not found: " + filename);
			return;
		}
                	
                length = soundFile.length()/8;
                System.out.println("length=="+length);
		AudioInputStream audioInputStream = null;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		} catch (UnsupportedAudioFileException e1) {
			e1.printStackTrace();
			return;
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
                rtpSession.payloadType(8);
		int nBytesRead = 0;
		byte[] abData = new byte[160];
		long start = System.currentTimeMillis();
                long rtpTimestamp= System.currentTimeMillis();
                long seqNum=1;
                boolean[] marker=new boolean[1];
                marker[0]=true;
                
		try {   

			while (nBytesRead != -1 ) {
				nBytesRead = audioInputStream.read(abData, 0, abData.length);
				//System.out.println("total byte "+ nBytesRead);
				if (nBytesRead >= 0) {
                                        
					rtpSession.sendData(new byte[] []{abData},null,marker,rtpTimestamp,new long[] {seqNum});
                                        marker = null;
                                        try { Thread.sleep(20);} catch(Exception e) {}
                                        rtpTimestamp=rtpTimestamp+160;
                                        seqNum++;
                             

				}
			}                      
		} catch (IOException e) {
			e.printStackTrace();
			//return;
		}

		System.out.println("Time: " + (System.currentTimeMillis() - start)/1000 + " s");
		
		try { Thread.sleep(200);} catch(Exception e) {}
		
		this.rtpSession.endSession();
                x++;
                soundFile.delete();
		
		//try { Thread.sleep(2000);} catch(Exception e) {}
		if(RTPSession.rtpDebugLevel > 1) {
			System.out.println("<- Run()");
		} 
	}

}