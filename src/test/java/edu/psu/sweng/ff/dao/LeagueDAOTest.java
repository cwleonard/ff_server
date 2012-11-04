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

public class LeagueDAOTest {

	
	@Test
	public void testStore() {

		LeagueDAO dao = new LeagueDAO();
		String ln = this.generateRandomLeagueName();

		Member commissioner = new Member();
		commissioner.setUserName(this.generateRandomUserName());

		League league = new League();
		league.setName(ln);
		league.setCommissioner(commissioner);
		
		assertTrue(dao.store(league));

		// make sure we got an id
		assertTrue(league.getId() != 0);
		
		assertTrue(dao.remove(league));
		
	}

	@Test
	public void testUpdate() {
		
		LeagueDAO dao = new LeagueDAO();
		String ln1 = this.generateRandomLeagueName();
		String ln2 = this.generateRandomLeagueName();

		Member commissioner = new Member();
		commissioner.setUserName(this.generateRandomUserName());

		League league = new League();
		league.setName(ln1);
		league.setCommissioner(commissioner);
		
		assertTrue(dao.store(league));

		league.setName(ln2);
		assertTrue(dao.update(league));
		
		League league2 = dao.loadById(league.getId());
		assertEquals(ln2, league2.getName());
		
		assertTrue(dao.remove(league));
		
	}

    private String generateRandomLeagueName() {
    	
    	String un = "league";
    	int r = 1000 + (int)(Math.random() * ((999999 - 1000) + 1));
    	return un + r;
    	
    }

    private String generateRandomUserName() {
    	
    	String un = "leagueOwner";
    	int r = 1000 + (int)(Math.random() * ((999999 - 1000) + 1));
    	return un + r;
    	
    }

}
