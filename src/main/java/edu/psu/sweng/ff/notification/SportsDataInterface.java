package edu.psu.sweng.ff.notification;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.psu.sweng.ff.common.Player;

public class SportsDataInterface {
	// Constants
	//private static final String API_KEY = "bzxrzyhacyfnvhbvevfps9zw"; //Our API Key
	private static final String API_KEY = "ecemnfwb82cgmkqz5fc8nhgw"; //Demo API Key
	
	private static String  currentTeamId = "";
	private static Document currentTeamDocument;

    // Suppress default constructor
    private SportsDataInterface() {
        throw new AssertionError();
    }
    
    public static void clearCache() {
    	//reset currentTeam to force it to reload
    	currentTeamId = "";
    	
    	//Delete team roster xml files
    	String[] teamIds = getTeamIds();
    	for (String teamId: teamIds) {
    		deleteFile(System.getProperty("java.io.tmpdir") + "\\roster_" + teamId + ".xml");
    	}
    	
    	//Delete hierarchy xml file
    	deleteFile(System.getProperty("java.io.tmpdir") + "\\hierarchy.xml");
    }
    
    public static String[] getTeamIds() {
    	ArrayList<String> teams = new ArrayList<String>();
    	
    	try {	    	
	    	File hierarchy_xml = new File(System.getProperty("java.io.tmpdir") + "\\hierarchy.xml");
	    	if (!hierarchy_xml.exists()) {
	    		downloadFile("http://api.sportsdatallc.org/nfl-t1/teams/hierarchy.xml?api_key=" + API_KEY, hierarchy_xml.getCanonicalPath());
	    	}
	    	
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document document = dBuilder.parse(hierarchy_xml);
			document.getDocumentElement().normalize();
			
			System.out.println("Root element :" + document.getDocumentElement().getNodeName());
			NodeList nList = document.getElementsByTagName("team");
			System.out.println("-----------------------");
			
			for (int i = 0; i < nList.getLength(); i++) {
			   Node node = nList.item(i);
			   if (node.getNodeType() == Node.ELEMENT_NODE) {
			      String teamId = getNodeAttr("id", node);
			      if (teamId != "") {
			    	  System.out.println("Team ID: " + teamId);
			    	  teams.add(teamId);
			      }
			   }
			}
	    } catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return teams.toArray(new String[teams.size()]);
    }
    
    public static String getTeamName(String teamId) {
    	String teamName = "";
    	
    	loadTeamRoster(teamId);
    	NodeList nList = currentTeamDocument.getElementsByTagName("team");
    	teamName = getNodeAttr("name", nList.item(0));
    	
    	return teamName;
    }
    
    public static Player[] getPlayers(String teamId) {
    	String teamName = getTeamName(teamId);
    	ArrayList<Player> players = new ArrayList<Player>();

		NodeList nList = currentTeamDocument.getElementsByTagName("player");
		System.out.println("-----------------------");
		
		for (int i = 0; i < nList.getLength(); i++) {
		   Node node = nList.item(i);
		   if (node.getNodeType() == Node.ELEMENT_NODE) {
		      String playerId = getNodeAttr("id", node);
		      if (playerId != "") {
		    	  
		    	  //Get Node Attributes
		    	  String position = getNodeAttr("position", node);
		    	  String college = getNodeAttr("college", node);
		    	  int weight = Integer.parseInt(getNodeAttr("weight", node));
		    	  int height = Integer.parseInt(getNodeAttr("height", node));
		    	  Date birthdate = new Date();
		    	  try {
		    		  birthdate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(getNodeAttr("birthdate", node));
		    	  } catch (ParseException e) {
		    		  e.printStackTrace();
		    	  }
		    	  String lastName = getNodeAttr("name_last", node);
		    	  String firstName = getNodeAttr("name_first", node);
		    	  
		    	  //Set Player Attributes
		    	  Player player = new Player();
		    	  player.setId(playerId);
		    	  player.setNflTeam(teamName);
		    	  player.setPosition(position);
		    	  player.setCollege(college);
		    	  player.setWeight(weight);
		    	  player.setHeight(height);
		    	  player.setBirthdate(birthdate);
		    	  player.setLastName(lastName);
		    	  player.setFirstName(firstName);
		    	  
		    	  //Add player to list
		    	  players.add(player);
		      }
		   }
		}
    	
    	
    	return players.toArray(new Player[players.size()]);
    }
    
    private static void loadTeamRoster(String teamId) {
    	//If the team roster is already loaded, don't reload it
    	if (teamId != currentTeamId) {
        	try {	    	
    	    	File roster_xml = new File(System.getProperty("java.io.tmpdir") + "\\roster_" + teamId + ".xml");
    	    	if (!roster_xml.exists()) {
    	    		downloadFile("http://api.sportsdatallc.org/nfl-t1/teams/" + teamId + "/roster.xml?api_key=" + API_KEY, roster_xml.getCanonicalPath());
    	    	}
    	    	
    			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    			currentTeamDocument = dBuilder.parse(roster_xml);
    			currentTeamDocument.getDocumentElement().normalize();
    			
    			currentTeamId = teamId;
		    } catch (Exception e) {
	    		e.printStackTrace();
	    	}
    	}
    }
    
    private static String getNodeAttr(String attrName, Node node) {
        NamedNodeMap attrs = node.getAttributes();
        for (int y = 0; y < attrs.getLength(); y++ ) {
            Node attr = attrs.item(y);
            if (attr.getNodeName().equalsIgnoreCase(attrName)) {
                return attr.getNodeValue();
            }
        }
        return "";
    }
    
    private static void downloadFile(String sourceURL, String destinationPath) {
		try {
			URL website = new URL(sourceURL);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(destinationPath);
			fos.getChannel().transferFrom(rbc, 0, 1 << 24);
			fos.close();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private static void deleteFile(String filePath) {
    	try {
    		File file = new File(filePath);
    		if (file.delete()) {
    			System.out.println(file.getName() + " is deleted!");
    		} else {
    			System.out.println("Delete operation is failed.");
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }
}
