package edu.psu.sweng.ff.notification;

import edu.psu.sweng.ff.common.League;
import edu.psu.sweng.ff.common.Member;
import edu.psu.sweng.ff.common.Notifier;

public class EmailNotifier implements Notifier {

	public boolean notify(Member m, String subj, String msg) {
		
		return Email.send("SWENG500 Fantasy Football",
				"fantasy-football-server@amphibian.com", m.getEmail(), subj,
				msg);

	}
	
	public boolean invite(Member m, String toAddress) {

		String fromName = m.getFirstName() + " " + m.getLastName();
		String msg = fromName
				+ " has invited you to join their fantasy football league. "
				+ "Log into the application using an account with this email address ("
				+ toAddress + ") and " + "accept or decline this invitation.";
		return Email.send(fromName, m.getEmail(), toAddress,
				"Fantasy Football Invitation", msg);
		
	}

}
