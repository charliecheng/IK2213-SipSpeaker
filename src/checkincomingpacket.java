import java.io.*;
import java.util.*;
import java.net.*;

public class checkincomingpacket {
    DatagramPacket packet;
    public String typeofpacket;
    public String CallID;
    public String receivername;
    public int rtpport;               //rtpport used by SJphone 
    public String sendername;
    public String Ceq;
    
    public checkincomingpacket(DatagramPacket packet){
        this.packet=packet;
    }
    void parsingpacket(){
        String data=new String(packet.getData());
        //System.out.println(data);
        try{
        StringTokenizer st = new StringTokenizer(data);
        typeofpacket = st.nextToken();
        if (typeofpacket.equals("OPTIONS")) {return;}
        //System.out.println("type "+typeofpacket);
        receivername = (data.split("sip:")[1]).split("@")[0];
          //      System.out.println("user "+receiver);
        Ceq=(data.split("CSeq: ")[1]).split(" ")[0];
        
        CallID= (data.split("Call-ID: ")[1]);
        String tempstringrtp=CallID;
        String tempstringsendername=CallID;
        //sendername=(tempstringsendername.split("From: \"")[1]).split("\"<sip:")[0]; 
        CallID=CallID.substring(0,CallID.indexOf(13));
        if (typeofpacket.equals("INVITE")){
        tempstringrtp=(tempstringrtp.split("m=audio ")[1]).split(" RTP")[0];
        rtpport=Integer.parseInt(tempstringrtp);
        }
        
        } catch (Exception ex){System.err.println(ex);};

    }
    
}
