/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.util.Properties;

/**
 *
 * @author charlie
 */
public class Config {
    Properties configFile;
   String base;
   public static boolean fileexist=true;
   public Config(String base)
   {this.base=base;
    configFile = new java.util.Properties();
    try {
      //configFile.load(this.getClass().getClassLoader().getResourceAsStream("cfg/sipspeaker.cfg"));
      File filepath = new File (base);
      System.out.println(filepath.getCanonicalPath());
      configFile.load(new FileInputStream(filepath)); 

    }catch(Exception eta){System.out.println("Cannot find give path of configure file, using command line value or default value instead."); fileexist=false;
        //eta.printStackTrace();
    }
   }
 
   public String getProperty(String key)
   {
    String value = this.configFile.getProperty(key);
    if (value.length()==0) {value=null;}
    return value;
   }  
    
}
