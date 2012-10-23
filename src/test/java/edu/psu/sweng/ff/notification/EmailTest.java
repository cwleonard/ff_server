package edu.psu.sweng.ff.notification;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.psu.sweng.ff.common.Member;

public class EmailTest {

	@Test
	public void testSend() {
		//Invalid source email addresses
		assertFalse(Email.send("Tom Smith", "tom_smith_123atmail.com", "jane_taylor_123@mail.com", "Test", "Test Email"));
		assertFalse(Email.send("Tom Smith", "tom_smith_123@", "jane_taylor_123@mail.com", "Test", "Test Email"));
		assertFalse(Email.send("Tom Smith", "@mail.com", "jane_taylor_123@mail.com", "Test", "Test Email"));
		//Invalid destination email addresses
		assertFalse(Email.send("Tom Smith", "tom_smith_123@mail.com", "jane_taylor_123atmail.com", "Test", "Test Email"));
		assertFalse(Email.send("Tom Smith", "tom_smith_123@mail.com", "jane_taylor_123@", "Test", "Test Email"));
		assertFalse(Email.send("Tom Smith", "tom_smith_123@mail.com", "@mail.com", "Test", "Test Email"));
		//Valid source and destination email addresses
		assertTrue(Email.send("Tom Smith", "tom_smith_123@mail.com", "jane_taylor_123@mail.com", "Test", "Test Email"));
	}

	@Test
	public void testInvite() {
		
		EmailNotifier mailer = new EmailNotifier();
		Member m = new Member();
		m.setFirstName("John");
		m.setLastName("Tester");
		m.setEmail("tester@amphibian.com");
		
		assertTrue(mailer.invite(m, "casey@amphibian.com"));
		
	}
	
}
