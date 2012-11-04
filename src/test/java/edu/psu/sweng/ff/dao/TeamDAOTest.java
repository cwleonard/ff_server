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
import edu.psu.sweng.ff.common.Member;
import edu.psu.sweng.ff.common.Player;
import edu.psu.sweng.ff.common.Team;

public class TeamDAOTest {

	@Test
	public void testStore() {

		TeamDAO dao = new TeamDAO();
		String tn = this.generateRandomTeamName();

		Member owner = new Member();
		owner.setUserName(this.generateRandomUserName());

		Team t = new Team();
		t.setName(tn);
		t.setLeagueId(1);
		t.setLogo("logo");
		t.setOwner(owner);
		
		dao.store(t);

		// make sure we got an id
		assertTrue(t.getId() != 0);
		
		assertTrue(dao.remove(t));
		
	}
	
	@Test
	public void testLoadById() {
		
		TeamDAO dao = new TeamDAO();
		String tn = this.generateRandomTeamName();

		Member owner = new Member();
		owner.setUserName(this.generateRandomUserName());

		Team t = new Team();
		t.setName(tn);
		t.setLeagueId(1);
		t.setLogo("logo");
		t.setOwner(owner);
		t.setPoints(100);
		t.setWins(2);
		t.setLosses(2);
		
		dao.store(t);
		
		Team t2 = dao.loadById(t.getId());
		
		assertEquals(t.getName(), t2.getName());
		assertEquals(t.getLeagueId(), t2.getLeagueId());
		assertEquals(t.getLogo(), t2.getLogo());
		assertEquals(t.getPoints(), t2.getPoints());
		assertEquals(t.getWins(), t2.getWins());
		assertEquals(t.getLosses(), t2.getLosses());
		
		assertTrue(dao.remove(t2));
		
	}

	@Test
	public void testLoadByOwner() {
		
		TeamDAO dao = new TeamDAO();
		String tn1 = this.generateRandomTeamName();
		String tn2 = this.generateRandomTeamName();

		Member owner = new Member();
		owner.setUserName(this.generateRandomUserName());

		Team t = new Team();
		t.setName(tn1);
		t.setLeagueId(1);
		t.setLogo("logo");
		t.setOwner(owner);
		t.setPoints(100);
		t.setWins(2);
		t.setLosses(2);

		Team t2 = new Team();
		t2.setName(tn2);
		t2.setLeagueId(2);
		t2.setLogo("logo");
		t2.setOwner(owner);
		t2.setPoints(200);
		t2.setWins(4);
		t2.setLosses(4);

		dao.store(t);
		dao.store(t2);
		
		List<Team> teams = dao.loadByOwner(owner);
		
		assertEquals(2, teams.size());

		Iterator<Team> ti = teams.iterator();
		while (ti.hasNext()) {
			
			Team tt = ti.next();
			
			if (tt.getId() == t.getId()) {

				assertEquals(t.getName(), tt.getName());
				assertEquals(t.getLeagueId(), tt.getLeagueId());
				assertEquals(t.getLogo(), tt.getLogo());
				assertEquals(t.getPoints(), tt.getPoints());
				assertEquals(t.getWins(), tt.getWins());
				assertEquals(t.getLosses(), tt.getLosses());

			} else if (tt.getId() == t2.getId()) {
				
				assertEquals(t2.getName(), tt.getName());
				assertEquals(t2.getLeagueId(), tt.getLeagueId());
				assertEquals(t2.getLogo(), tt.getLogo());
				assertEquals(t2.getPoints(), tt.getPoints());
				assertEquals(t2.getWins(), tt.getWins());
				assertEquals(t2.getLosses(), tt.getLosses());
				
			} else {
				
				fail("Unknown team id " + tt.getId() + "!");
				
			}
			
		}

		assertTrue(dao.remove(t));
		assertTrue(dao.remove(t2));
		
	}

	@Test
	public void testLoadByLeague() {
		
		TeamDAO dao = new TeamDAO();
		String tn1 = this.generateRandomTeamName();
		String tn2 = this.generateRandomTeamName();

		Member owner = new Member();
		owner.setUserName(this.generateRandomUserName());

		League league1 = new League();
		league1.setId(9999);
		
		Team t = new Team();
		t.setName(tn1);
		t.setLeagueId(9999);
		t.setLogo("logo");
		t.setOwner(owner);
		t.setPoints(100);
		t.setWins(2);
		t.setLosses(2);

		Team t2 = new Team();
		t2.setName(tn2);
		t2.setLeagueId(9998);
		t2.setLogo("logo");
		t2.setOwner(owner);
		t2.setPoints(200);
		t2.setWins(4);
		t2.setLosses(4);

		dao.store(t);
		dao.store(t2);
		
		List<Team> teams = dao.loadByLeague(league1);
		
		assertEquals(1, teams.size());

		Team tt = teams.get(0);
		assertEquals(t.getName(), tt.getName());
		assertEquals(t.getLeagueId(), tt.getLeagueId());
		assertEquals(t.getLogo(), tt.getLogo());
		assertEquals(t.getPoints(), tt.getPoints());
		assertEquals(t.getWins(), tt.getWins());
		assertEquals(t.getLosses(), tt.getLosses());

		assertTrue(dao.remove(t));
		assertTrue(dao.remove(t2));
		
	}

	@Test
	public void testUpdate() {
		
		TeamDAO dao = new TeamDAO();
		String tn1 = this.generateRandomTeamName();
		String tn2 = this.generateRandomTeamName();

		Member owner = new Member();
		owner.setUserName(this.generateRandomUserName());

		Team t = new Team();
		t.setName(tn1);
		t.setLeagueId(1);
		t.setLogo("logo");
		t.setOwner(owner);
		
		dao.store(t);

		t.setName(tn2);
		t.setPoints(123);
		t.setWins(3);
		t.setLosses(1);
		assertTrue(dao.update(t));
		
		Team t2 = dao.loadById(t.getId());
		assertEquals(tn2, t2.getName());
		assertEquals(123, t2.getPoints());
		assertEquals(3, t2.getWins());
		assertEquals(1, t2.getLosses());
		
		assertTrue(dao.remove(t));
		
	}

    private String generateRandomTeamName() {
    	
    	String un = "team";
    	int r = 1000 + (int)(Math.random() * ((999999 - 1000) + 1));
    	return un + r;
    	
    }

    private String generateRandomUserName() {
    	
    	String un = "leagueOwner";
    	int r = 1000 + (int)(Math.random() * ((999999 - 1000) + 1));
    	return un + r;
    	
    }

}
