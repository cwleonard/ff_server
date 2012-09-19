package edu.psu.sweng.ff.dao;

import java.util.HashMap;
import java.util.Map;

import edu.psu.sweng.ff.common.Member;

public class MemberDAO {

	private static Map<Integer, Member> idToMemberMap = new HashMap<Integer, Member>();

	private static Map<String, Member> userNameToMemberMap = new HashMap<String, Member>();

	private static Map<String, Member> tokenToMemberMap = new HashMap<String, Member>();

	private static int nextId = 1;
	
	static {
		
		Member tester = new Member();
		tester.setEmail("test@test.com");
		tester.setFirstName("Test");
		tester.setLastName("Tester");
		tester.setHideEmail(false);
		tester.setId(0);
		tester.setMobileNumber("555-555-5555");
		tester.setAccessToken("1111-2222-3333");
		tester.setPassword("password");
		tester.setUserName("test");
		
		idToMemberMap.put(tester.getId(), tester);
		userNameToMemberMap.put(tester.getUserName(), tester);
		tokenToMemberMap.put(tester.getAccessToken(), tester);
		
	}
	
	public String authenticateUser(String u, String p) {
		
		Member m = loadByUserName(u);
		if (m == null) {
			return null;
		} else {
			if (m.getPasswordHash().equals(p)) {
				return m.getAccessToken();
			} else {
				return null;
			}
		}
		
	}
	
	public Member loadById(int id) {
		return idToMemberMap.get(id);
	}
	
	public Member loadByUserName(String un) {
		return userNameToMemberMap.get(un);
	}
	
	public Member loadByToken(String t) {
		return tokenToMemberMap.get(t);
	}
	
	public void store(Member m) {
		
		Member r = idToMemberMap.remove(m.getId());
		if (r != null) {
			userNameToMemberMap.remove(r.getUserName());
			tokenToMemberMap.remove(r.getAccessToken());
		}
		
		idToMemberMap.put(m.getId(), m);
		userNameToMemberMap.put(m.getUserName(), m);
		tokenToMemberMap.put(m.getAccessToken(), m);
		
	}
	
	public int nextMemberId() {
		return nextId++;
	}
	
}
