package FetchOTP;

import javax.mail.*;
import javax.mail.internet.*;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import com.testing.framework.*;

public class SendEmail {
    public static void main(String[] args) throws Exception {
    	
    	EmailUtils emailUtils = new EmailUtils();
    	Properties prop = new Properties();
    	
    	prop.load(new FileInputStream("C:\\Users\\wbox62\\eclipse-workspace\\FetchOTP01\\target\\config.properties"));
    	
    	Store connection = emailUtils.connectToGmail(prop);
    	
    	// emailUtils.getUnreadMessages(connection, "Inbox");
    	
//    	ssiagro@agroworlds.com
//    	One time password
           
    	List<String> emailtext = emailUtils.getUnreadMessageByFromEmail(connection, "Inbox", "ssiagro@agroworlds.com", "One time password");
    	
    	if (emailtext.size()<1) 

    	throw new Exception("No email received");
    	
    	else
    	{
    		String regex = "[^\\d]+";
    		
    		String[] OTP = emailtext.get(0).split(regex);
    		
    		System.out.println("OTP is " + OTP[1]);
    	
    	}

    	
    }
}

