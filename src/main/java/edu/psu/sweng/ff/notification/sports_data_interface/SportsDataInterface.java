package edu.psu.sweng.ff.notification.sports_data_interface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
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
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.psu.sweng.ff.common.Player;

public class SportsDataInterface {
	// Constants
	//private static final String API_KEY = "bzxrzyhacyfnvhbvevfps9zw"; //Our API Key
	private static final String API_KEY = "ecemnfwb82cgmkqz5fc8nhgw"; //Demo API Key
	private static final int API_QUERY_DELAY = 1500; //Delay between API queries, in milliseconds
	
	private static String  currentTeamId = "";
	private static Document currentTeamDocument;
	private static long nextApiQuery = new Date().getTime();
	private static String tmpDir = System.getProperty("java.io.tmpdir") + "\\ff_server";

    // Suppress default constructor
    private SportsDataInterface() {
        throw new AssertionError();
    }
    
    public static void clearCache() {
    	//reset currentTeam to force it to reload
    	currentTeamId = "";
    	
    	//Delete ff_server temp directory
    	removeDirectory(new File(tmpDir));
    }
    
    private static void downloadFile(String sourceURL, String destinationPath, String filename) {
		try {
			//Wait until the next allowed API Query time
			long now = new Date().getTime();
			if (now < nextApiQuery) {
				try {
					Thread.sleep(nextApiQuery - now);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			//Create the directory
			File directory = new File(destinationPath);
			if (!directory.exists()) {
				directory.mkdirs();
			}
			
			//Download the file
			File file = new File(directory, filename);
			URL website = new URL(sourceURL);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(file.getCanonicalPath());
			fos.getChannel().transferFrom(rbc, 0, 1 << 24);
			fos.close();
			now = new Date().getTime();
			nextApiQuery = now + API_QUERY_DELAY;
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private static Document getXmlDocument(String queryUrl, String tempDirectory, String filename) {
    	Document document = null;
    	
    	try {
    		File xmlFile = new File(tempDirectory, filename);
	    	if (!xmlFile.exists()) {
				downloadFile(queryUrl, tempDirectory, filename);
	    	}
	    	
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			document = dBuilder.parse(xmlFile);
			document.getDocumentElement().normalize();	
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return document;
    }
    
    public static String[] getTeamIds() {
    	ArrayList<String> teams = new ArrayList<String>();
    	
    	try {	    	
    		Document document = getXmlDocument("http://api.sportsdatallc.org/nfl-t1/teams/hierarchy.xml?api_key=" + API_KEY, tmpDir, "hierarchy.xml");
			
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
        		currentTeamDocument = getXmlDocument("http://api.sportsdatallc.org/nfl-t1/teams/" + teamId + "/roster.xml?api_key=" + API_KEY, tmpDir, "roster_" + teamId + ".xml");
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
    
    public static boolean removeDirectory(File directory) {
    	// System.out.println("removeDirectory " + directory);
    	if (directory == null)
    		return false;
    	if (!directory.exists())
    		return true;
    	if (!directory.isDirectory())
    		return false;
    	
    	String[] list = directory.list();
    	
    	// Some JVMs return null for File.list() when the directory is empty.
    	if (list != null) {
    		for (int i = 0; i < list.length; i++) {
    			File entry = new File(directory, list[i]);
    			System.out.println("\tremoving entry " + entry);
    			if (entry.isDirectory()) {
    				if (!removeDirectory(entry))
    					return false;
    				}
    			else {
    				if (!entry.delete())
    					return false;
    			}
    		}
    	}
    	
    	return directory.delete();
    }

	public static Game[] getGames(int week) {
    	ArrayList<Game> games = new ArrayList<Game>();
    	
    	try {
    		Document scheduleXml = getXmlDocument("http://api.sportsdatallc.org/nfl-t1/2012/REG/schedule.xml?api_key=" + API_KEY, tmpDir, "schedule.xml");
			
			System.out.println("Root element :" + scheduleXml.getDocumentElement().getNodeName());
			NodeList nList = scheduleXml.getElementsByTagName("week");
			System.out.println("-----------------------");
			
			Node currentWeek = getCurrentWeek(week, nList);
			nList = currentWeek.getChildNodes();
			for (int i = 0; i < nList.getLength(); i++) {
			   Node node = nList.item(i);
			   if (node.getNodeType() == Node.ELEMENT_NODE) {
				   String status = getNodeAttr("status", node);
				   if (status.contentEquals("closed")) {
					   Game game = new Game();
					   game.setHomeId(getNodeAttr("home", node));
					   game.setAwayId(getNodeAttr("away", node));
					   //Load Game Statistics
					   Document statisticsXml = getXmlDocument("http://api.sportsdatallc.org/nfl-t1/2012/REG/1/" + game.getAwayId() + "/" + game.getHomeId() + "/statistics.xml?api_key=" + API_KEY, tmpDir, "statistics_" + week + "_" + game.getAwayId() + "_" + game.getHomeId() + ".xml");
					   NodeList teamNodes = statisticsXml.getElementsByTagName("team");
					   //Set Home Team Name
					   game.setHomeName(getTeamName(teamNodes, game.getHomeId()));
					   //Set Away Team Name
					   game.setAwayName(getTeamName(teamNodes, game.getAwayId()));
					   //Calculate Home Team Defense Points
					   game.setHomeDefensePoints(getDefensePoints(teamNodes, game.getHomeId(), game.getAwayId()));
					   //Calculate Away Team Defense Points
					   game.setAwayDefensePoints(getDefensePoints(teamNodes, game.getAwayId(), game.getHomeId()));
					   //Set player points
					   game.setPlayerPoints(getPlayerPoints(statisticsXml));
					   games.add(game);
				   }
			   }
			}
	    } catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return games.toArray(new Game[games.size()]);
	}

	private static PlayerPoints[] getPlayerPoints(Document statisticsXml) {
		ArrayList<PlayerPoints> players = new ArrayList<PlayerPoints>();
		
		//7.1.1 System shall award each offensive position (quarterback, running back, wide receiver, and tight end) six points for rushing, receiving, and throwing touchdown.
		players = addPlayerPoints(players, statisticsXml, "rushing", "td", 6, 1);
		players = addPlayerPoints(players, statisticsXml, "receiving", "td", 6, 1);
		players = addPlayerPoints(players, statisticsXml, "passing", "td", 6, 1);
		//7.1.2 System shall award quarterback one point for each 25 yards of passing.
		players = addPlayerPoints(players, statisticsXml, "passing", "yds", 1, 25);
		//7.1.3 System shall award each offensive position (quarterback, running back, wide receiver, and tight end) one point for each 15 yards of rushing.
		players = addPlayerPoints(players, statisticsXml, "rushing", "yds", 1, 15);
		//7.1.4 System shall subtract two points from quarterback for each interception.
		players = addPlayerPoints(players, statisticsXml, "passing", "int", -2, 1);
		//7.1.5 System shall subtract two points from player for each fumble.
		players = addPlayerPoints(players, statisticsXml, "fumbles", "fum", -2, 1);
		//7.1.6 System shall award kicker one point for PAT kick.
		players = addPlayerPoints(players, statisticsXml, "K", "extra_point", "made", 1, 1);
		//7.1.7 System shall award kicker three points for field goal.
		players = addPlayerPoints(players, statisticsXml, "field_goal", "made", 3, 1);
		
		return players.toArray(new PlayerPoints[players.size()]);
	}

	private static ArrayList<PlayerPoints> addPlayerPoints(ArrayList<PlayerPoints> players, Document xml,
			String position, String tagName, String attribute, int multiplier, int divisor) {

		NodeList nodes = xml.getElementsByTagName(tagName);
		for (int n = 0; n < nodes.getLength(); n++) {
		   Node node = nodes.item(n);
		   if (node.getNodeType() == Node.ELEMENT_NODE) {
			   NodeList playerNodes = ((Element)node).getElementsByTagName("player");
			   for (int p = 0; p < playerNodes.getLength(); p++) {
				   Node playerNode = playerNodes.item(p);
				   if (node.getNodeType() == Node.ELEMENT_NODE) {
						   if (position.equals("") || position.equals(getNodeAttr(position, playerNode))) {
							   String id = getNodeAttr("id", playerNode); 
							   int points = Integer.parseInt(getNodeAttr(attribute, playerNode)) * multiplier / divisor;
							   boolean found = false;
							   for (PlayerPoints player : players) {
								   if (player.getPlayerId().equals(id)) {
									   player.setPoints(player.getPoints() + points);
									   found = true;
								   }
							   }
							   if(!found) {
								   players.add(new PlayerPoints(id, points));
							   }
						   }
				   }
			   }
		   }
		}
		
		return players;
	}

	private static ArrayList<PlayerPoints> addPlayerPoints(ArrayList<PlayerPoints> players, Document xml,
			String tagName, String attribute, int multiplier, int divisor) {
		return addPlayerPoints(players, xml, "", tagName, attribute, multiplier, divisor);
	}

	private static String getTeamName(NodeList teamNodes, String teamId) {
		String teamName = "";
		
		for (int i = 0; i < teamNodes.getLength(); i++) {
			   Node node = teamNodes.item(i);
			   if (node.getNodeType() == Node.ELEMENT_NODE) {
				   String id = getNodeAttr("id", node); 
				   if (id.contentEquals(teamId)) {
					   teamName = getNodeAttr("name", node);
				   }
			   }
		}
		
		return teamName;
	}
	
	private static BigDecimal getDefensePoints(NodeList teamNodes, String teamId, String opponentId) {
		BigDecimal points = new BigDecimal(0);
		
		for (int i = 0; i < teamNodes.getLength(); i++) {
			   Node node = teamNodes.item(i);
			   if (node.getNodeType() == Node.ELEMENT_NODE) {
				   String id = getNodeAttr("id", node); 
				   if (id.contentEquals(teamId)) {
					   //SRS 7.1.8: System shall award team defense two points for fumble recovery, interception, and safety.
					   Node defense = ((Element)node).getElementsByTagName("defense").item(0);
					   int fumble_recoveries = Integer.parseInt(getNodeAttr("fum_rec", defense));
					   int interceptions = Integer.parseInt(getNodeAttr("int", defense));
					   int safeties = Integer.parseInt(getNodeAttr("sfty", defense));
					   points = points.add(new BigDecimal((fumble_recoveries + interceptions + safeties) * 2));
					   //SRS 7.1.9: System shall award team defense one point for sack.
					   points = points.add(new BigDecimal(getNodeAttr("sack", defense)));
					   //SRS 7.1.10: System shall award team defense six points for defensive touchdown or kick return touchdown.
					   Node touchdowns = ((Element)node).getElementsByTagName("touchdowns").item(0);
					   int fumble_touchdowns = Integer.parseInt(getNodeAttr("fum_ret", touchdowns));
					   int interception_touchdowns = Integer.parseInt(getNodeAttr("int", touchdowns));
					   int kick_return_touchdowns = Integer.parseInt(getNodeAttr("kick_ret", touchdowns));
					   points = points.add(new BigDecimal((fumble_touchdowns + interception_touchdowns + kick_return_touchdowns) * 6));
				   } else if (id.contentEquals(opponentId)) {
					   //SRS 7.1.11: System shall award points for opposition scoring.
					   int opponentScore = Integer.parseInt(getNodeAttr("points", node));
					   //SRS 7.1.11.1: System shall award team defense 10 points for zero points allowed.
					   if (opponentScore == 0) points = points.add(new BigDecimal(10));
					   //SRS 7.1.11.2: System shall award team defense seven points for one to six points allowed.
					   else if (opponentScore <= 6) points = points.add(new BigDecimal(7));
					   //SRS 7.1.11.3: System shall award team defense four points for seven to 13 points allowed.
					   else if (opponentScore <= 13) points = points.add(new BigDecimal(4));
					   //SRS 7.1.11.4: System shall award team defense one point for 14 to 20 points allowed.
					   else if (opponentScore <= 20) points = points.add(new BigDecimal(1));
					   //SRS 7.1.11.5: System shall award team defense zero points for 21 to 27 points allowed.
					   else if (opponentScore <= 27) points = points.add(new BigDecimal(0));
					   //SRS 7.1.11.6: System shall subtract one point from team defense for 28 to 34 points allowed.
					   else if (opponentScore <= 34) points = points.subtract(new BigDecimal(1));
					   //SRS 7.1.11.7: System shall subtract four points from team defense for 35 or more points allowed.
					   else points = points.subtract(new BigDecimal(4));
				   }
			   }
		}
		
		return points;
	}

	private static Node getCurrentWeek(int week, NodeList nList) {
		Node currentWeek = null;
		
		for (int i = 0; i < nList.getLength(); i++) {
		   Node node = nList.item(i);
		   if (node.getNodeType() == Node.ELEMENT_NODE) {
			   int nodeWeek = Integer.parseInt(getNodeAttr("week", node));
			   if (nodeWeek == week) {
				   currentWeek = node;
				   break;
			   }
		   }
		}
		
		return currentWeek;
	}
}
