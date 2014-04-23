import java.io.*;
import java.util.*;
import java.net.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import jlibrtp.*;

public class Working extends Thread{
    static DatagramPacket packet;
    static DatagramSocket socket;
    String[] userparameter;
    static String incomingAddress="";
    static InetAddress ic=null;
    static int incomingport=0;
    static int SJrtpport=0;
    public static String CallID;
    static int sdpsessionid=0;
    static int stpport=12346;           //stpport used by application
    static int tag=123456789;
    public static double pr=0.0;
    static String ceq="";
    static int ceqbye=0;
    static String fromLine="";
    static String toLine="";
    static String viaLine="";
    static String CallIDLine="";
    static String contactLine="";
    static DatagramSocket rtpSocket;
    static DatagramSocket rtcpSocket;
    public Working(DatagramPacket packet,DatagramSocket socket,String[] userparameter){
        this.packet=packet;
        this.userparameter=userparameter;
        this.socket=socket;
        this.rtpSocket=rtpSocket;
        this.rtcpSocket=rtcpSocket;
        
    }
     
    
    public void run(){
        RTPSender aDemo = new RTPSender((stpport-10));
        
        //System.out.println(incomingAddress+"    "+SJrtpport);
	Participant p = new Participant(incomingAddress,SJrtpport,SJrtpport + 1);
	aDemo.rtpSession.addParticipant(p);
		//aDemo.filename = args[0];
	aDemo.run();
        try {
        Thread.sleep(aDemo.length+500);
         } catch (InterruptedException e1) {
        e1.printStackTrace();}
        sendBye();  
        
    }
    
    public void sendOKofinvite(){
        incomingAddress=packet.getAddress().getHostAddress();
        incomingport=packet.getPort();
        ic=packet.getAddress();
        String packetdata=new String(packet.getData());
        SJrtpport=Integer.parseInt((packetdata.split("m=audio ")[1]).split(" RTP")[0]);
        fromLine="From: "+(packetdata.split("From: ")[1]).split("\r\n")[0];
        toLine="To: "+(packetdata.split("To: ")[1]).split("\r\n")[0];
        contactLine="Contact: <sip:"+userparameter[0]+"@"+userparameter[2]+":"+userparameter[1]+">";
        String OKdata="SIP/2.0 200 OK";
        String CallSeqLine="CSeq: "+(packetdata.split("CSeq:")[1]).split("\r\n")[0];
        //System.out.println(CallSeqLine);
        ceq=(CallSeqLine.split("eq:  ")[1]).split(" ")[0];
        //System.out.println("ceq=="+ceq);
        CallID=(packetdata.split("Call-ID: ")[1]).split("\r\n")[0];
        CallIDLine="Call-ID: "+CallID;
        //String viaLine="Via: "+(packetdata.split("Via: ")[1]).split(";rport")[0]+":"+packet.getPort()+";rport="+packet.getPort()+(packetdata.split("rport")[1]).split("\r\n")[0];
        viaLine="Via: "+(packetdata.split("Via: ")[1]).split(";rport")[0]+";rport="+userparameter[1]
                +(packetdata.split("rport")[1]).split("\r\n")[0];
        
        String contenttypeLine="Content-Type: application/sdp";
        String sdpLine="v=0"+(packetdata.split("v=0")[1]).split("c=IN")[0]+"c=IN IP4 "+userparameter[2]
                +"\r\n"+"t=0 0"+"\r\n"+"m=audio "+stpport+" RTP/AVP 8 0"+"\r\n"+
                //"a=rtpmap:110 speex/8000/1"+"\r\n"+
                //"a=fmtp:110 vbr=on"+"\r\n"+
                "a=rtpmap:8 PCMA/8000"+"\r\n"+
                "a=rtpmap:0 PCMU/8000"+"\r\n";
        stpport=stpport+10;
        String tryingLine="SIP/2.0 100 Trying";
        String ringLine="SIP/2.0 180 Ringing";
        //stpport=stpport+100;
 
        String contentlengthLine="Content-Length: "+sdpLine.length();
        
        //System.out.println(OKdata);
        //System.out.println(viaLine);
        //System.out.println(fromLine);
        //System.out.println(toLine);
        //System.out.println(CallIDLine);
        //System.out.println(CallSeqLine);
        //System.out.println(contactLine);
        //System.out.println(sdpLine);
        //System.out.println("used sdp port: "+sdpport);
        //String sendingdata=OKdata+"\r\n"+viaLine+"\r\n"+fromLine+"\r\n"+toLine+"\r\n"+CallIDLine+"\r\n"+CallSeqLine+"\r\n"+contactLine+"\r\n"+contentlengthLine+"\r\n"+"\r\n"+sdpLine;
        String sendingdata=OKdata+"\r\n"+viaLine+"\r\n"+fromLine+"\r\n"+toLine+";tag="+tag+"\r\n"+
                CallIDLine+"\r\n"+CallSeqLine+"\r\n"+contactLine+"\r\n"+contenttypeLine+"\r\n"
                +contentlengthLine+"\r\n"+"\r\n"+sdpLine;
        String sendingTrying=tryingLine+"\r\n"+viaLine+"\r\n"+fromLine+"\r\n"+toLine+"\r\n"+CallIDLine+"\r\n"+
                CallSeqLine+"\r\n"+"Content-Length: 0\r\n\r\n";
        String sendingRing=ringLine+"\r\n"+viaLine+"\r\n"+fromLine+"\r\n"+toLine+";tag="+tag+"\r\n"+CallIDLine+"\r\n"+
                CallSeqLine+"\r\n"+contactLine+"\r\n"+"Content-Length: 0\r\n\r\n";
        //tag++;
        //System.out.println(sendingdata);
        byte[] sendingbyte=sendingdata.getBytes();
        byte[] sendingbyteTrying=sendingTrying.getBytes();
        byte[] sendingbyteRing=sendingRing.getBytes();
        DatagramPacket sendPacket=new DatagramPacket (sendingbyte,sendingbyte.length,packet.getAddress(),packet.getPort());
        DatagramPacket sendPacketTrying=new DatagramPacket (sendingbyteTrying,sendingbyteTrying.length,packet.getAddress(),packet.getPort());
        DatagramPacket sendPacketRing=new DatagramPacket (sendingbyteRing,sendingbyteRing.length,packet.getAddress(),packet.getPort());
        
        try{
        socket.send(sendPacketTrying);
        socket.send(sendPacketRing);
        socket.send(sendPacket);
        DatagramPacket receivePacket= new DatagramPacket (new byte[1000],1000);
         //DatagramSocket socketrtp=new DatagramSocket(stpport);
       //  socketrtp.receive(receivePacket);
        //socket.close();
        //System.out.println("here");
        }catch(Exception ex1){System.err.println(ex1);}

        
    }
    
    public void sendACK(){
        ceqbye=(Integer.parseInt(ceq)+1);
        String sendingACK="SIP/2.0 200 OK"+"\r\n"+viaLine+"\r\n"+fromLine+"\r\n"+toLine+"\r\n"+
                CallIDLine+"\r\n"+"CSeq: "+ceqbye+" BYE"+"\r\n"+"Content-Length: 0\r\n\r\n";
         byte[] sendingbyteACK=sendingACK.getBytes();
         DatagramPacket sendPacketACK=new DatagramPacket (sendingbyteACK,sendingbyteACK.length,packet.getAddress(),packet.getPort());
         try {socket.send(sendPacketACK);}catch(Exception ex2){System.err.println(ex2);}
         System.out.println("Sending Ack of Bye to "+incomingAddress);
         //stpport=stpport+10;
         //socket.close();
    }
    
    public void sendBye(){
        String sendingBye="BYE "+toLine.substring(toLine.indexOf("<")+1,toLine.indexOf(">"))+" SIP/2.0"+"\r\n"
                +viaLine+"\r\n"
                +"From: "+toLine.substring(toLine.indexOf("<"),toLine.length())+";tag=123456789\r\n"
                +"To: "+fromLine.substring(fromLine.indexOf(" ")+1,fromLine.length())+"\r\n"
                +CallIDLine+"\r\n"
                +"CSeq: "+(Integer.parseInt(ceq)+1)+" BYE"+"\r\n"
                +contactLine+"\r\n"
                +"Content-Length: 0"+"\r\n"+"\r\n";
        byte[] sendingbyteBye=sendingBye.getBytes();
         DatagramPacket sendPacketBye=new DatagramPacket (sendingbyteBye,sendingbyteBye.length,ic,incomingport);
         try {socket.send(sendPacketBye);}catch(Exception ex3){System.err.println(ex3);}
         System.out.println("Sending Bye to "+incomingAddress);
         //stpport=stpport+10;
         //System.out.println(sendingBye);
        
    }

}
