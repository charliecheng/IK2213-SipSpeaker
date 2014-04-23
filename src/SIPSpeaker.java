import java.io.*;
import java.util.*;
import java.util.Properties;
import java.net.*;
import javax.sound.sampled.*;
import com.sun.speech.freetts.FreeTTS;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.AudioPlayer;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;
import javax.sound.sampled.AudioFileFormat.Type;

public class SIPSpeaker {
    public static String currentmessage_main = "This is the default message";
    static final String USAGE = "USAGE: java SIPSpeaker [-c config_file_name] [-user sip_uri] [-http http_bind_address]";
     /*hard coded value*/ 
    public static String base="/home/charlie/sip/sipspeaker.cfg";
    static String default_message = "defualt_incode.wav";
    static String message_wav = "message_wav_incode";
    static String message_text = "This is a default message";
    static String message_recived = "message_recived_incode";
    static String sip_interface = "eth0";
    static String sip_port = "5060";
    static String sip_user = "charlie";
    static String http_interface = "eth0";
    static String http_port = "8080";

    public static void main(String[] args) throws IOException {
     /*check command lines validation*/
     boolean argscheck=checkargus(args);
     if (argscheck==false) {System.out.println(USAGE);System.exit(1);}
     String sip_ip=null;
     String http_ip=null;
     /*check given -c parameter*/
     boolean basechange=false;
     if (args.length==2&&args[0].equals("-c")) {base=args[1];basechange=true;}
     if (args.length==4&&args[0].equals("-c")) {base=args[1];basechange=true;}
     if (args.length==4&&args[2].equals("-c")) {base=args[3];basechange=true;}
     if (args.length==6&&args[0].equals("-c")) {base=args[1];basechange=true;}
     if (args.length==6&&args[2].equals("-c")) {base=args[3];basechange=true;}
     if (args.length==6&&args[4].equals("-c")) {base=args[5];basechange=true;}
     if (basechange==true)
     {
       Config cfg = new Config(base);
       boolean fileexist=cfg.fileexist;
       if (fileexist==true){
       if (cfg.getProperty("default_message")!=null) default_message = cfg.getProperty("default_message");
       if (cfg.getProperty("message_wav")!=null) message_wav = cfg.getProperty("message_wav");
       if (cfg.getProperty("message_text")!=null)message_text = cfg.getProperty("message_text");
       if (cfg.getProperty("message_recived")!=null)message_recived = cfg.getProperty("message_recived");
       if (cfg.getProperty("sip_interface")!=null)sip_interface = cfg.getProperty("sip_interface");
       if (cfg.getProperty("sip_port")!=null)sip_port = cfg.getProperty("sip_port");
       if (cfg.getProperty("sip_user")!=null)sip_user = cfg.getProperty("sip_user");
       if (cfg.getProperty("http_interface")!=null)http_interface = cfg.getProperty("http_interface");
       if (cfg.getProperty("http_port")!=null)http_port = cfg.getProperty("http_port");}
     }
       Enumeration<InetAddress> addr=null;
  NetworkInterface sipinterface=null;
  
  InetAddress current_addra=null;
  try{
      sipinterface=sipinterface.getByName(sip_interface);
      addr=sipinterface.getInetAddresses();
      while (addr.hasMoreElements()){
       current_addra = addr.nextElement();
       if (current_addra instanceof Inet4Address)
       {sip_ip=current_addra.getHostAddress();}
        else if (current_addra instanceof Inet6Address)
        {continue;}
      }
  }catch(Exception e){System.out.println("Invalid sip_interface, can not find interface or IP address");System.exit(1);}
     /*check given -user parameter*/
     String input_usernameandaddress=null;
     if (args.length==2&&args[0].equals("-user")) {input_usernameandaddress=args[1];}
     if (args.length==4&&args[0].equals("-user")) {input_usernameandaddress=args[1];}
     if (args.length==4&&args[2].equals("-user")) {input_usernameandaddress=args[3];}
     if (args.length==6&&args[0].equals("-user")) {input_usernameandaddress=args[1];}
     if (args.length==6&&args[2].equals("-user")) {input_usernameandaddress=args[3];}
     if (args.length==6&&args[4].equals("-user")) {input_usernameandaddress=args[5];}
     if (input_usernameandaddress!=null)
     {
       try{
       String input_username = input_usernameandaddress.split("@")[0];
       String input_useraddressandport=input_usernameandaddress.split("@")[1];
       String input_useraddress=input_useraddressandport.split(":")[0];
       String input_userport=null;
       try{input_userport=input_useraddressandport.split(":")[1];}catch(Exception AIE){input_userport=sip_port;}
       sip_user=input_username;
       InetAddress tempaddr=null;
       tempaddr=tempaddr.getByName(input_useraddress);
       sip_ip=tempaddr.getHostAddress();
       sip_port=input_userport;
       }catch (Exception AIE1){System.out.println("USAGE: [-user user@host[:port]]");System.exit(1);}
     }

     /*check given -http parameter*/
     String input_http=null;

     if (args.length==2&&args[0].equals("-http")) {input_http=args[1];}
     if (args.length==4&&args[0].equals("-http")) {input_http=args[1];}
     if (args.length==4&&args[2].equals("-http")) {input_http=args[3];}
     if (args.length==6&&args[0].equals("-http")) {input_http=args[1];}
     if (args.length==6&&args[2].equals("-http")) {input_http=args[3];}
     if (args.length==6&&args[4].equals("-http")) {input_http=args[5];}
     if (input_http!=null)
     {
      String input_httpinterface=null;
      String input_httpport=null;
      try{
        http_ip= input_http.split(":")[0];
        input_httpport= input_http.split(":")[1];
      }catch (Exception http1)
       {
          try
          { 
           int tempport=Integer.parseInt(input_http);
           input_httpport=input_http;
           input_httpinterface=null;
          }catch (Exception http2){
              InetAddress tempaddr=null;
       tempaddr=tempaddr.getByName(input_http);
       http_ip=tempaddr.getHostAddress();
          
          }
       }
       //http_interface=input_httpinterface;
       http_port=input_httpport;
      
     } 

  //System.out.println(default_message);
  System.out.println("final sip name used: "+sip_user);
  System.out.println("final sip port used: "+sip_port);
  System.out.println("final sip interface used: "+sip_interface);
  System.out.println("final http interface used: "+http_interface);
  System.out.println("final http port used: "+http_port);
  //get IP address of SIP interface;
  int intsip_port= Integer.parseInt(sip_port);

  String[] userparameter={sip_user,sip_port,sip_ip,sip_interface};
  System.out.println("final sip IP: "+sip_ip);
       try{
        wavfileoutput wfop2 = new wavfileoutput(currentmessage_main);
      int feedback_defaultmessage1=wfop2.generatewavfile();
      //wfop2.convetToPCMA();
      wfop2.encodeToPCMA();
       }catch(Exception ex1){}
  /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Start running the main program~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  TCPhandler tcphandler= new TCPhandler (http_interface,http_port);
       tcphandler.start();
  DatagramSocket socket = new DatagramSocket(intsip_port,current_addra);
  DatagramPacket packet = new DatagramPacket(new byte[1000],1000);
  HashMap<String,Working> hm = new HashMap<String,Working>();
  while (true){

      socket.receive(packet);
      //System.out.println("Handling client at "+packet.getAddress().getHostAddress()+" on port "+packet.getPort());
      //System.out.println(new String(packet.getData()));
 
      checkincomingpacket cip=new checkincomingpacket(packet);
      
      cip.parsingpacket(); 
      if ((cip.typeofpacket).equals("OPTIONS")) {continue;}
      System.out.println(cip.typeofpacket+" from "+packet.getAddress().getHostAddress());
     //System.out.println(cip.receivername);
      //if the user name doesn't match, do nothing and continue
      if (!(cip.receivername).equals(sip_user)) {continue;}
      //Handle the INVITE packet
      if ((cip.typeofpacket).equals("INVITE")){
          if (hm.containsKey(cip.CallID)){
              continue;
          }
          Working wk= new Working(packet,socket,userparameter);
          hm.put(cip.CallID,wk);
          wk.sendOKofinvite();
          //((Working)hm.get(cip.CallID)).start();
          //wk.start();

      }
      if ((cip.typeofpacket).equals("ACK")){
          if ((cip.Ceq).equals(((Working)hm.get(cip.CallID)).ceq)){
              System.out.println("After this Ack, start tramsmiting audio in the session with Call-ID: "+((Working)hm.get(cip.CallID)).CallID);

              
              ((Working)hm.get(cip.CallID)).start();

          }
          else {
              continue;
          }
      }
      
      if ((cip.typeofpacket).equals("BYE")){
          if (!hm.containsKey(cip.CallID)){
              continue;
          }
          //System.out.println("BYE's "+cip.CallID);

         ((Working)hm.get(cip.CallID)).stop();
         ((Working)hm.get(cip.CallID)).sendACK();
         hm.remove(cip.CallID);
         //System.out.println(((Working)hm.get(cip.CallID)).pr);
      }

      
      //System.out.println(cip.receivername);
      //System.out.println(cip.CallID);
      //System.out.println(cip.rtpport);
  }
  }
  


  /*a method that check the input arguments validation*/
  static boolean checkargus(String[] args)
  { 
    if (args.length==0) {return true;}
    if (args.length>6||args.length==1||args.length==3||args.length==5) {return false;}
    if (args.length==2) {
      if ((args[0].equals("-c"))||(args[0].equals("-user"))||(args[0].equals("-http"))) {return true;}
      else return false;
    }
    if (args.length==4) {
      if ((args[0].equals("-c")&&args[2].equals("-user"))||(args[0].equals("-user")&&args[2].equals("-c"))||(args[0].equals("-c")&&args[2].equals("-http"))||(args[0].equals("-http")&&args[2].equals("-c"))||(args[0].equals("-user")&&args[2].equals("-http"))||(args[0].equals("-http")&&args[2].equals("-user"))) {return true;}
      else return false;
    }
    if (args.length==6) {
      if ((args[0].equals("-c")&&args[2].equals("-user")&&args[4].equals("-http"))||(args[0].equals("-user")&&args[2].equals("-c")&&args[4].equals("-http"))||(args[0].equals("-c")&&args[2].equals("-http")&&args[4].equals("-user"))||(args[0].equals("-http")&&args[2].equals("-c")&&args[4].equals("-user"))||(args[0].equals("-user")&&args[2].equals("-http")&&args[4].equals("-c"))||(args[0].equals("-http")&&args[2].equals("-user")&&args[4].equals("-c"))) {return true;}
      else return false;
    }
    return true;
  }

}

class TCPhandler extends Thread{
    String http_ip;
    String http_port;
    static ServerSocket ss=null;
    TCPhandler (String http_ip,String http_port){
        setDaemon(true);
        this.http_ip=http_ip;
        this.http_port=http_port;
        try {ss = new ServerSocket(Integer.parseInt(http_port));}catch(Exception ex2){}
    }
    public void run(){
        try{
        while (true){
        Socket s = ss.accept();
        Handler h = new Handler(s, SIPSpeaker.base,SIPSpeaker.currentmessage_main);
        h.setPriority(h.getPriority() + 1);
        h.start();}
    }    
        catch(Exception ex3){}
    }

}

class Handler extends Thread implements Serializable {
  static final String SERVER = "Server: Httpd 1.0";
  static final String OK = "HTTP/1.0 200 OK";
  static final String NOT_FOUND = "HTTP/1.0 404 File Not Found";
  static final String NOT_FOUND_HTML =
      "<HTML><HEAD><TITLE>File Not Found</TITLE></HEAD><BODY><H1>HTTP Error 404: File Not Found</H1></BODY></HTML>";

 
  private Socket s;
  private String base;
  private String currentmessage;
  Handler (Socket s, String base,String currentmessage) {
    this.s = s; this.base = base;this.currentmessage=currentmessage;
  }
  public void run() {
    try {
      //System.out.println(currentmessage);
      BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
      String str;
      if ((str = r.readLine()) == null){
        s.close(); return;
      };
      OutputStream os = s.getOutputStream(); // get output stream of the socket
      //System.out.println(str);   // for log
      StringTokenizer st = new StringTokenizer(str);
      String method = st.nextToken();
      String name = st.nextToken();
      String version = st.nextToken();

      
      /*while ((str = r.readLine()) != null && !str.trim().equals("")) {
        System.out.println(str); // for log
      } // away empty lines */
      //if (str != null) System.out.println(str); // for log
      
      // Here is the GET request processed
      if (method.equals("GET")) { //System.out.println("method get");
        if (name.endsWith("/")) {name += "index.html";}
        if (name.startsWith("/index")){
            String indexpage="<html><head><title>Text to Audio by FreeTTS</title></head><body><h3>The current message is:<br>"+currentmessage+".<br><br><form method=\"post\"><div style=\"width:120px; float:left\">Message:</div><div><textarea cols=\"50\" rows=\"8\" name=message></textarea></div><input type=\"submit\" value=\"Send\" /><input type=\"reset\" value=\"Reset\"></form><form action=\"deletemessage.html\" method=\"get\"><input type=\"submit\" value=\"Delete Current Message\"/></h3></form></body></html>";
            sendHTMLMessage(os,OK,indexpage, version);
        }
        else if (name.startsWith("/deletemessage")){
            wavfileoutput wfop = new wavfileoutput("This is a default message");
            int feedback_defaultmessage=wfop.generatewavfile();
            //wfop.convetToPCMA();
            wfop.encodeToPCMA();
            currentmessage="This is a default message";
            SIPSpeaker.currentmessage_main="This is a default message";
            System.out.println("The current message is changed to This is a default message.");
            String deletemessage_html="<HTML><HEAD><TITLE>Message Deleted</TITLE></HEAD><BODY><H1>Message Deleted.<br>The default message: \"This is a default message.\" is used.</H1></BODY></HTML>";
        sendHTMLMessage(os,OK,deletemessage_html, version);}
        
      }
      if (method.equals("POST")){
          String query;
        while ((query = r.readLine()) != null)
	{
   	  if (query.startsWith("Content-Length: "))
   	  {
      	     break;
   	  }
	}
	//int contentLength = Integer.parseInt(query.substring("Content-Length: ".length()));

        char[] inBuff = new char[10000];
        int charsRead = 0;
        String inputLine="";
        charsRead = r.read(inBuff, 0, 10000);
	inputLine += String.valueOf(inBuff,0, charsRead);
	//System.out.println(inputLine);

        String content=inputLine.split("message=")[1];
        //System.out.println(content);
      
      content=URLDecoder.decode(content,"UTF-8");
      System.out.println("The current message is changed to "+content);
      currentmessage=content;
      SIPSpeaker.currentmessage_main=currentmessage;
      wavfileoutput wfop1 = new wavfileoutput(content);
      int feedback_defaultmessage1=wfop1.generatewavfile();
      //wfop1.convetToPCMA();
      wfop1.encodeToPCMA();
      String message_failed_html="<HTML><HEAD><TITLE>Message Failed</TITLE></HEAD><BODY><H1>Unable to convert message to wav audio. Please try again.</H1></BODY></HTML>";
      String message_success_html="<HTML><HEAD><TITLE>Message Changed</TITLE></HEAD><BODY><H1>The message is changed.<br>The current used message is:<br>"+currentmessage+"</H1></BODY></HTML>";
      if (feedback_defaultmessage1==0) {sendHTMLMessage(os,OK,message_success_html, version);}
      else {sendHTMLMessage(os,OK,message_failed_html, version);}
      }else {sendHTMLMessage(os,NOT_FOUND,NOT_FOUND_HTML, version);}
      

      s.close();
    } catch (Exception e) {
      System.err.println("OBS, "+e.toString());
    }
  }



  /* Gets the requested documents type from its extension*/
  String ContentTypeFrom(String name) {
    if (name.endsWith(".html") || name.endsWith(".htm")) return "text/html";
    else if (name.endsWith(".txt") || name.endsWith(".java")) return "text/plain";
    else if (name.endsWith(".gif") ) return "image/gif";
    else if (name.endsWith(".class") ) return "application/octet-stream";
    else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
    else return "text/plain";
  }

  /* Prints an error code into a given output stream */
  void sendHTMLMessage(OutputStream os, String code, String html, String version) {
    PrintWriter pw = new PrintWriter(os);
    if (version.startsWith("HTTP/")) {
      pw.println(code);
      pw.println("Date:" + (new Date()));
      pw.println(SERVER);
      pw.println("Content-type: text/html");
    pw.println();}
    pw.println(html); pw.flush();pw.close();
  }



}



class wavfileoutput {
    static AudioFormat alawformat= new AudioFormat(AudioFormat.Encoding.ALAW,8000,8,1,1,8000,false);
    static AudioFormat ulawformat= new AudioFormat(AudioFormat.Encoding.ULAW,8000,8,1,1,8000,false);
    String inputmessage;
    public wavfileoutput(String inputmessage){this.inputmessage=inputmessage;}
    public  int generatewavfile() {

           // listAllVoices();

            FreeTTS freetts;
       AudioPlayer audioPlayer = null;
            String voiceName = "kevin";

            //System.out.println();
            //System.out.println("Using voice: " + voiceName);

            /* The VoiceManager manages all the voices for FreeTTS.
             */
            VoiceManager voiceManager = VoiceManager.getInstance();
            Voice helloVoice = voiceManager.getVoice(voiceName);

            if (helloVoice == null) {
                System.err.println(
                    "Cannot find a voice named "
                    + voiceName + ".  Please specify a different voice.");
                return 1;
            }

            /* Allocates the resources for the voice.
             */
            helloVoice.allocate();

            /* Synthesize speech.
             */
//create a audioplayer to dump the output file
           audioPlayer = new SingleFileAudioPlayer("currentmessage",Type.WAVE);
    //attach the audioplayer 
           helloVoice.setAudioPlayer(audioPlayer);



            helloVoice.speak(inputmessage);



            /* Clean up and leave.
             */
            helloVoice.deallocate();
//don't forget to close the audioplayer otherwise file will not be saved
            audioPlayer.close();
            //Convert .wav PCM to PCMA

            return 0;
        }
    public static void convetToPCMA() throws Exception{
        
        InputStream in = new FileInputStream("currentmessage.wav");
        AudioInputStream stream;
        stream=AudioSystem.getAudioInputStream(new File("currentmessage.wav"));
      //  long filelength=(new File("currentmessage.wave")).length();
        byte[] buffer1=new byte[10000000];
        byte[] bufferfile=new byte[in.read(buffer1)];
        System.out.println(bufferfile.length);

        
        CompressInputStream cis= new CompressInputStream(stream,true);
        cis.read(bufferfile);
        System.out.println(bufferfile.length);
        //System.out.println(in.read(buffer1));
        File newfile= new File("currentmessageINPCMA.wav");
        newfile.delete();
        WaveOutputFile wof=new WaveOutputFile(newfile,alawformat,bufferfile);
        wof.close();

    }
    public static void encodeToPCMA() {
        File inputFile=new File("currentmessage.wav");
        File outputFile=new File("currentmessageINPCMA.wav");
        AudioInputStream audioInputStream = null;
       
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.ALAW,((float) 8000.0), 8, 1, 1, ((float) 8000.0) ,false);

        try {
            audioInputStream = AudioSystem.getAudioInputStream(inputFile);
            //System.out.println(audioInputStream.getFormat().toString());
            AudioInputStream input = AudioSystem
                    .getAudioInputStream(format/*AudioFormat.Encoding.ALAW*/, audioInputStream
                    /*AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, audioInputStream)*/);
           // System.out.println(input.getFormat().toString());
           
            AudioSystem.write(input, AudioFileFormat.Type.WAVE, outputFile);
            //System.out.println(inputFile + " -(pcma)> " + outputFile);
            input.close();
            audioInputStream.close();
        } catch (UnsupportedAudioFileException e1) {
            e1.printStackTrace();
            return;
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }
    }
    
    
}