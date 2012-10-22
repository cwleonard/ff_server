package edu.psu.sweng.ff.notification;

import edu.psu.sweng.ff.common.Member;
import edu.psu.sweng.ff.common.Notifier;

public class EmailNotifier implements Notifier {

	public void notify(Member m, String subj, String msg) {
		
		Email.send("SWENG500 Fantasy Football",
				"fantasy-football-server@amphibian.com", m.getEmail(), subj,
				msg);

	}

}
