package FetchOTP;

import javax.mail.*;
import javax.mail.search.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import org.jsoup.Jsoup; // Add this dependency

public class Fetchotp {

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        String configFilePath = "C:\\Users\\wbox62\\eclipse-workspace\\FetchOTP01\\target\\config.properties";
        try (InputStream input = new FileInputStream(configFilePath)) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties at " + configFilePath);
                return;
            }
            props.load(input);
        }

        // Connect to Gmail
        Store store = connectToGmail(props);

        // Fetch unread messages
        String senderEmail = props.getProperty("gmail_from");
        String subject = "One time password";
        Message[] messages = getUnreadMessages(store, "INBOX", senderEmail, subject);

        if (messages.length == 0) {
            System.out.println("No email received");
        } else {
            for (Message message : messages) {
                String emailContent = getTextFromMessage(message);

                // Extract OTP from the email content
                String otp = extractOtp(emailContent);

             
                if (!otp.isEmpty()) {
                    System.out.println("OTP from Gmail account is : " + otp);
                } else {
                    System.out.println("No OTP found in the email content.");
                }
            }
        }

        store.close();
    }

    private static Store connectToGmail(Properties props) throws MessagingException {
        String username = props.getProperty("gmail_username");
        String password = props.getProperty("gmail_password");

        Properties mailProps = new Properties();
        mailProps.put("mail.imap.host", "imap.gmail.com");
        mailProps.put("mail.imap.port", "993");
        mailProps.put("mail.imap.ssl.enable", "true");
        mailProps.put("mail.imap.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(mailProps);
        Store store = session.getStore("imap");
        store.connect(username, password);
        return store;
    }

    private static Message[] getUnreadMessages(Store store, String folderName, String senderEmail, String subject) throws MessagingException {
        Folder folder = store.getFolder(folderName);
        folder.open(Folder.READ_ONLY);

        // Create search terms
        SearchTerm fromTerm = new FromStringTerm(senderEmail);
        SearchTerm subjectTerm = new SubjectTerm(subject);
        SearchTerm unseenTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false); // Unread messages

        // Combine search terms
        SearchTerm combinedTerm = new AndTerm(new SearchTerm[]{fromTerm, subjectTerm, unseenTerm});

        // Search for messages
        Message[] messages = folder.search(combinedTerm);
        return messages;
    }

    private static String getTextFromMessage(Message message) throws MessagingException, java.io.IOException {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent();
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    result.append(bodyPart.getContent().toString());
                } else if (bodyPart.isMimeType("text/html")) {
                    // Use Jsoup to parse HTML content and extract plain text
                    String html = (String) bodyPart.getContent();
                    String text = Jsoup.parse(html).text();
                    result.append(text);
                }
            }
            return result.toString();
        }
        return "";
    }

    private static String extractOtp(String content) {
        // Define a regex pattern to extract a 4 to 6 digit OTP
        String regex = "\\b\\d{4,6}\\b";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }
}


//package FetchOTP;
//
//import javax.mail.*;
//import javax.mail.search.*;
//import java.io.BufferedReader;
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.Properties;
//
//public class Fetchotp {
//
//  public static void main(String[] args) throws Exception {
//      Properties props = new Properties();
//      String configFilePath = "C:\\Users\\wbox62\\eclipse-workspace\\FetchOTP01\\target\\config.properties";
//      try (InputStream input = new FileInputStream(configFilePath)) {
//          if (input == null) {
//              System.out.println("Sorry, unable to find config.properties at " + configFilePath);
//              return;
//          }
//          props.load(input);
//      }
//
//      // Connect to Gmail
//      Store store = connectToGmail(props);
//
//      // Fetch unread messages
//      String senderEmail = props.getProperty("gmail_from");
//      String subject = "One time password";
//      Message[] messages = getUnreadMessages(store, "INBOX", senderEmail, subject);
//
//      if (messages.length == 0) {
//          throw new Exception("No email received");
//      } else {
//          // Extract OTP from the first email
//          String emailContent = getTextFromMessage(messages[0]);
//          System.out.println("Email Content: " + emailContent);
//          String otp = extractOtp(emailContent);
//          System.out.println("OTP is: " + otp);
//      }
//
//      store.close();
//  }
//
//  private static Store connectToGmail(Properties props) throws MessagingException {
//      String username = props.getProperty("gmail_username");
//      String password = props.getProperty("gmail_password");
//
//      Properties mailProps = new Properties();
//      mailProps.put("mail.imap.host", "imap.gmail.com");
//      mailProps.put("mail.imap.port", "993");
//      mailProps.put("mail.imap.ssl.enable", "true");
//      mailProps.put("mail.imap.ssl.protocols", "TLSv1.2");
//
//      Session session = Session.getInstance(mailProps);
//      Store store = session.getStore("imap");
//      store.connect(username, password);
//      return store;
//  }
//
//  private static Message[] getUnreadMessages(Store store, String folderName, String senderEmail, String subject) throws MessagingException {
//      Folder folder = store.getFolder(folderName);
//      folder.open(Folder.READ_ONLY);
//
//      // Create search terms
//      SearchTerm fromTerm = new FromStringTerm(senderEmail);
//      SearchTerm subjectTerm = new SubjectTerm(subject);
//      SearchTerm unseenTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false); // Unread messages
//
//      // Combine search terms
//      SearchTerm combinedTerm = new AndTerm(new SearchTerm[]{fromTerm, subjectTerm, unseenTerm});
//
//      // Search for messages
//      Message[] messages = folder.search(combinedTerm);
//      return messages;
//  }
//
//  private static String getTextFromMessage(Message message) throws MessagingException, java.io.IOException {
//      if (message.isMimeType("text/plain")) {
//          return message.getContent().toString();
//      } else if (message.isMimeType("multipart/*")) {
//          Multipart multipart = (Multipart) message.getContent();
//          for (int i = 0; i < multipart.getCount(); i++) {
//              BodyPart bodyPart = multipart.getBodyPart(i);
//              if (bodyPart.isMimeType("text/plain")) {
//                  return bodyPart.getContent().toString();
//              } else if (bodyPart.isMimeType("text/html")) {
//                  String html = (String) bodyPart.getContent();
//                  return html;
//              }
//          }
//      }
//      return "";
//  }
//
//  private static String extractOtp(String content) {
//      // Define a regex pattern to extract a 4-digit OTP
//  	  String regex = "\\b\\d{4,6}\\b"; 
//      java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
//      java.util.regex.Matcher matcher = pattern.matcher(content);
//      if (matcher.find()) {
//          return matcher.group();
//      }
//      return "";
//  }
//}
