package edu.psu.sweng.ff.dao;

import static org.junit.Assert.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.psu.sweng.ff.common.Player;

public class PlayerDAOTest {

	
//	@Test
//	public void testStore() {
//		
//		String[] testFiles = getTestFilenames();
//		
//		for (int i = 0; i < testFiles.length; i++) {
//			Document doc = parseXmlFile(new File(testFiles[i]));
//			List<Player> players = getPlayers(doc);
//		
//			PlayerDAO dao = new PlayerDAO();
//			Iterator<Player> pi = players.iterator();
//			while (pi.hasNext()) {
//				Player p = pi.next();
//				assertTrue(dao.store(p));
//			}
//		}
//		
//	}
	
	private String[] getTestFilenames() {
		
		return new String[] {
			"D:\\Casey's Documents\\Dropbox\\FantasyFootballApp\\roster_mia.xml",
			"D:\\Casey's Documents\\Dropbox\\FantasyFootballApp\\roster_min.xml",
			"D:\\Casey's Documents\\Dropbox\\FantasyFootballApp\\roster_ne.xml",
			"D:\\Casey's Documents\\Dropbox\\FantasyFootballApp\\roster_nyj.xml",
			"D:\\Casey's Documents\\Dropbox\\FantasyFootballApp\\roster_pit.xml"
		};
		
	}

	@Test
	public void testLoadById() {
		
		String id = "ac568889-d10d-4ccc-89a1-8e4233848086";
		PlayerDAO dao = new PlayerDAO();
		Player p = dao.getById(id);
		assertEquals(id, p.getId());
		
	}

	@Test
	public void testUpdate() {
		
		String id = UUID.randomUUID().toString();
		PlayerDAO dao = new PlayerDAO();

		Player p = new Player();
		p.setId(id);
		p.setFirstName("Test");
		p.setLastName("Player");
		p.setNflTeam("Testers");
		p.setPosition("QB");
		p.setJerseyNumber(33);
		p.setHeight(80);
		p.setWeight(215);
		p.setBirthdate(new Date());
		p.setCollege("Penn State");
		
		assertTrue(dao.store(p));
		
		Player p2 = dao.getById(id);
		assertEquals(215, p2.getWeight());
		assertEquals(id, p2.getId());
		
		p2.setWeight(200);
		dao.store(p2);
		
		Player p3 = dao.getById(id);
		assertEquals(200, p3.getWeight());
		assertEquals(id, p3.getId());
		
		assertTrue(dao.remove(p3));
		
	}
	
	
	@Test
	public void testLoadByType() {
		
		String type = "QB";
		String[] types = {"RB","WR","TE"};
		
		PlayerDAO dao = new PlayerDAO();
		
		// test single type
		List<Player> players = dao.getByType(type);
		Iterator<Player> i = players.iterator();
		while (i.hasNext()) {
			Player p = i.next();
			assertEquals(type, p.getPosition());
		}
		
		// test multi-type
		players = dao.getByType(types);
		i = players.iterator();
		while (i.hasNext()) {
			Player p = i.next();
			String pos = p.getPosition();
			assertTrue(pos.equals(types[0]) || pos.equals(types[1]) || pos.equals(types[2]));
		}
		
	}
	
	private Document parseXmlFile(File f) {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom = null;

		try {

			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(f);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return dom;

	}

	private List<Player> getPlayers(Document doc){

		List<Player> players = new ArrayList<Player>();
		
		Element docEle = doc.getDocumentElement();
		String team = docEle.getAttribute("name");

		NodeList nl = docEle.getElementsByTagName("player");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {

				Element el = (Element)nl.item(i);
				Player p = getPlayer(el);
				p.setNflTeam(team);
				players.add(p);
				
			}
		}
		
		return players;
		
	}
	
	private Player getPlayer(Element el) {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Player p = new Player();
		
		try {
			
			String guid = el.getAttribute("id");
			String firstName = el.getAttribute("name_first");
			String lastName = el.getAttribute("name_last");
			String bdate = el.getAttribute("birthdate");
			String height = el.getAttribute("height");
			String weight = el.getAttribute("weight");
			String college = el.getAttribute("college");
			String position = el.getAttribute("position");
			String jersey = el.getAttribute("jersey_number");

			p.setId(guid);
			p.setFirstName(firstName);
			p.setLastName(lastName);
			p.setCollege(college);
			p.setPosition(position);
			p.setHeight(Integer.parseInt(height));
			p.setWeight(Integer.parseInt(weight));
			p.setBirthdate(formatter.parse(bdate));
			p.setJerseyNumber(Integer.parseInt(jersey));

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return p;
		
	}

}
