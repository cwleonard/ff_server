package edu.psu.sweng.ff.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import edu.psu.sweng.ff.common.Player;
import edu.psu.sweng.ff.common.PlayerSource;

public class PlayerDAO extends BaseDAO implements PlayerSource {

	public List<Player> getByType(String type) {

		//TODO: make this real
		List<Player> lp = new ArrayList<Player>();
		Player p = new Player();
		p.setId(UUID.randomUUID().toString());
		lp.add(p);
		return lp;

	}

	public List<Player> getByType(String... types) {

		// TODO Auto-generated method stub
		List<Player> lp = new ArrayList<Player>();
		Player p = new Player();
		p.setId(UUID.randomUUID().toString());
		lp.add(p);
		return lp;
		
	}
	
	public Player getById(String id) {
		
		return new Player();
		
	}

}
