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

import edu.psu.sweng.ff.common.Member;
import edu.psu.sweng.ff.common.Player;

public class MemberDAOTest {

	
	@Test
	public void testStore() {
		
		MemberDAO dao = new MemberDAO();
		
		String un = this.generateRandomUserName();
		
		Member m = new Member();
		m.setUserName(un);
		m.setEmail("test@test.com");
		m.setFirstName("Test");
		m.setLastName("Tester");
		m.setMobileNumber("555-555-5555");
		m.setHideEmail(false);
		m.setHideName(false);
		m.setPassword("password");
		m.setAccessToken("111-222-333");
		
		assertTrue(dao.store(m));
		assertTrue(dao.remove(m));
		
	}

	@Test
	public void testLoadByUserName() {
		
		MemberDAO dao = new MemberDAO();
		
		String un = this.generateRandomUserName();
		
		Member m = new Member();
		m.setUserName(un);
		m.setEmail("test@test.com");
		m.setFirstName("Test");
		m.setLastName("Tester");
		m.setMobileNumber("555-555-5555");
		m.setHideEmail(false);
		m.setHideName(false);
		m.setPassword("password");
		m.setAccessToken("111-222-333");
		
		assertTrue(dao.store(m));
		
		Member m2 = dao.loadByUserName(un);
		assertEquals(m.getEmail(), m2.getEmail());
		assertEquals(m.getFirstName(), m2.getFirstName());
		assertEquals(m.getAccessToken(), m2.getAccessToken());
		
		assertTrue(dao.remove(m));
		
	}

	@Test
	public void testUpdate() {
		
		MemberDAO dao = new MemberDAO();
		
		String un = this.generateRandomUserName();
		
		String firstEmail = "test@test.com";
		String secondEmail = "new.email@test.net";
		
		Member m = new Member();
		m.setUserName(un);
		m.setEmail(firstEmail);
		m.setFirstName("Test");
		m.setLastName("Tester");
		m.setMobileNumber("555-555-5555");
		m.setHideEmail(false);
		m.setHideName(false);
		m.setPassword("password");
		m.setAccessToken("111-222-333");
		
		assertTrue(dao.store(m));

		m.setEmail(secondEmail);
		assertTrue(dao.update(m));
		
		Member m2 = dao.loadByUserName(un);
		assertEquals(secondEmail, m2.getEmail());
		
		assertTrue(dao.remove(m));
		
	}

    private String generateRandomUserName() {
    	
    	String un = "johndoe";
    	int r = 1000 + (int)(Math.random() * ((999999 - 1000) + 1));
    	return un + r;
    	
    }

}
