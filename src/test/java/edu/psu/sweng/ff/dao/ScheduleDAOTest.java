package edu.psu.sweng.ff.dao;

import static org.junit.Assert.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.psu.sweng.ff.common.League;
import edu.psu.sweng.ff.common.Matchup;
import edu.psu.sweng.ff.common.Member;
import edu.psu.sweng.ff.common.Player;
import edu.psu.sweng.ff.common.Schedule;

public class ScheduleDAOTest {

	
	@Test
	public void testStore() {

		ScheduleDAO dao = new ScheduleDAO();

		Schedule s = new Schedule();
		s.setLeagueId(9999);
		
		Matchup m1a = new Matchup();
		m1a.setWeek(1);
		m1a.setTeamA(110);
		m1a.setTeamB(111);
		
		Matchup m1b = new Matchup();
		m1b.setWeek(1);
		m1b.setTeamA(120);
		m1b.setTeamB(121);
		
		Matchup m1c = new Matchup();
		m1c.setWeek(1);
		m1c.setTeamA(130);
		m1c.setTeamB(131);
		
		Matchup m2a = new Matchup();
		m2a.setWeek(2);
		m2a.setTeamA(130);
		m2a.setTeamB(121);
		
		Matchup m2b = new Matchup();
		m2b.setWeek(2);
		m2b.setTeamA(110);
		m2b.setTeamB(120);
		
		Matchup m2c = new Matchup();
		m2c.setWeek(2);
		m2c.setTeamA(131);
		m2c.setTeamB(111);

		List<Matchup> week1 = s.getMatchups(1);
		week1.add(m1a);
		week1.add(m1b);
		week1.add(m1c);
		
		List<Matchup> week2 = s.getMatchups(2);
		week2.add(m2a);
		week2.add(m2b);
		week2.add(m2c);
		
		assertTrue(dao.store(s));
		
		// now read it back
		League l = new League();
		l.setId(9999);
		Schedule s2 = dao.loadByLeague(l);
		
		List<Matchup> oWeek1 = s2.getMatchups(1);
		List<Matchup> oWeek2 = s2.getMatchups(2);
		
		assertTrue(oWeek1.size() > 0);
		assertTrue(oWeek2.size() > 0);
		
		assertTrue(oWeek1.contains(m1a));
		assertTrue(oWeek1.contains(m1b));
		assertTrue(oWeek1.contains(m1c));
		
		assertTrue(oWeek2.contains(m2a));
		assertTrue(oWeek2.contains(m2b));
		assertTrue(oWeek2.contains(m2c));

		assertTrue(dao.remove(s));
		
	}

}
