package edu.psu.sweng.ff.notification;

import static org.junit.Assert.*;

import org.junit.Test;

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

}
