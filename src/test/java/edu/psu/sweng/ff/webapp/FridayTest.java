package edu.psu.sweng.ff.webapp;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class FridayTest {

	@Test
	public void testNextFriday() {
		
		ApplicationWatcher aw = new ApplicationWatcher();
		Date nf = aw.nextFriday();
		
		Calendar c = Calendar.getInstance();
		c.setTime(nf);
		
		// make sure date is a friday
		assertEquals(Calendar.FRIDAY, c.get(Calendar.DAY_OF_WEEK));
		
		// make sure date is no more than 1 week away
		long weekMillis = 7 * 24 * 60 * 60 * 1000;
		long now = System.currentTimeMillis();
		assertTrue(c.getTimeInMillis() < now + weekMillis);
		
	}
	
}
