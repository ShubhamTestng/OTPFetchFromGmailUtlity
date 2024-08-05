package FetchOTP;

import java.util.Properties;
import javax.mail.*;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class OTPStackOverflow {

    public static void main(String[] args) throws Exception {
        String mailFolderName = "INBOX";
        String emailSubjectContent = "Mail for OTP";
        String emailContent = "Your OTP is";
        int lengthOfOTP = 6;

        String otp = OutlookMailReader(mailFolderName, emailSubjectContent, emailContent, lengthOfOTP);
        if (otp != null) {
            System.out.println("Extracted OTP: " + otp);
        } else {
            System.out.println("OTP not found.");
        }
    }

    public static String OutlookMailReader(String mailFolderName, String emailSubjectContent, String emailContent, int lengthOfOTP) {
        String hostName = "imap.gmail.com"; // Change it according to your mail provider
        String username = "testwebfret@gmail.com"; // Username
        String password = "ogak dlem oqee tqwj"; // App-specific password

        String searchText = null;
        Properties sysProps = new Properties();
        sysProps.setProperty("mail.store.protocol", "imaps");
        sysProps.setProperty("mail.imap.ssl.enable", "true");
        sysProps.setProperty("mail.imap.ssl.trust", "*");
        sysProps.setProperty("mail.imap.ssl.checkserveridentity", "false");
        sysProps.setProperty("mail.imap.starttls.enable", "true");
        sysProps.setProperty("mail.imap.ssl.protocols", "TLSv1.2 TLSv1.1 TLSv1");
        sysProps.setProperty("mail.debug", "true");
        try {
            Session session = Session.getInstance(sysProps, null);
            Store store = session.getStore();
            store.connect(hostName, username, password);

            Folder emailBox = store.getFolder(mailFolderName);
            emailBox.open(Folder.READ_WRITE);

            int messageCount = emailBox.getMessageCount();
            System.out.println("Total Message Count: " + messageCount);

            int unreadMsgCount = emailBox.getNewMessageCount();
            System.out.println("Unread Emails count: " + unreadMsgCount);

            for (int i = messageCount; i > (messageCount - unreadMsgCount); i--) {
                Message emailMessage = emailBox.getMessage(i);
                String emailSubject = emailMessage.getSubject();

                if (emailSubject.contains(emailSubjectContent)) {
                    System.out.println("OTP mail found");

                    StringBuffer buffer = new StringBuffer();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(emailMessage.getInputStream()));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    reader.close();

                    String messageContent = emailContent;
                    String fullMessage = buffer.toString();
                    int startIndex = fullMessage.indexOf(messageContent);
                    if (startIndex != -1) {
                        int endIndex = startIndex + messageContent.length() + lengthOfOTP;
                        searchText = fullMessage.substring(startIndex + messageContent.length(), endIndex).trim();
                        System.out.println("Text found: " + searchText);
                    }

                    emailMessage.setFlag(Flags.Flag.SEEN, true);
                    break;
                }

                emailMessage.setFlag(Flags.Flag.SEEN, true);
            }

            emailBox.close(true);
            store.close();
        } catch (Exception mex) {
            mex.printStackTrace();
            System.out.println("OTP Not found");
        }

        return searchText;
    }
}
