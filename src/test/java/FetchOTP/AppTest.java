package FetchOTP;
import com.mailosaur.MailosaurClient;
import com.mailosaur.MailosaurException;
import com.mailosaur.models.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.annotations.Test;

public class AppTest {

	@Test 
	public void testExample() throws IOException, MailosaurException {
		// Available in the API tab of a server
		String APIkey = "m7bza8ShjeJxo5N3dXcKxobBgJqXASui";

		String severID = "1qa4x41w";

		String serverDomain = "1qa4x41w.mailosaur.net";

		MailosaurClient mailosaur = new MailosaurClient(APIkey);

		MessageSearchParams params = new MessageSearchParams();
		params.withServer(severID);

		SearchCriteria criteria = new SearchCriteria();
		criteria.withSentTo("anything@" + serverDomain);

		Message message = mailosaur.messages().get(params, criteria);
		
		System.out.println(message.subject());
		System.err.println(message.cc());
		System.out.println(message.to());
		
		System.out.println(message.text().body()); // "Your access code is 243546."

		Pattern pattern = Pattern.compile(".*([0-9]{4}).*");
		Matcher matcher = pattern.matcher(message.text().body());
		matcher.find();

		System.out.println(matcher.group(1) + "And OTP is"); 


		assertNotNull(message);
		assertEquals("Testing mail", message.subject());
	}
}