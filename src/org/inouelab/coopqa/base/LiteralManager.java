package org.inouelab.coopqa.base;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class LiteralManager {
	private class Pair {
		private int x, y;
		public Pair(int x, int y) {
			this.x = x; this.y = y;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Pair))
				return false;
			Pair other = (Pair) obj;		
			
			return (this.x == other.x && this.y == other.y);
		}
		
		@Override
		public int hashCode() {
			return (97*x + 143*y);
		}
	}
	private Map<Literal, Integer> idMap;
	private Map<Integer, Literal> formMap;
	
	private Map<Integer, Set<Integer>> hashedMap;
	private Map<Pair, Map<Integer, Integer> > thetaMap;
	private int cnt; 
	
	public LiteralManager() {
		cnt = 0;
		idMap 		= 	new HashMap<Literal, Integer>();
		formMap		= 	new HashMap<Integer, Literal>();
		hashedMap 	= 	new HashMap<Integer, Set<Integer>>();
		thetaMap 	= 	new HashMap<Pair, Map<Integer, Integer> >();
	}
	
	public Literal add(Literal newLiteral) {
		Integer id = idMap.get(newLiteral);
		if (id != null)
			return formMap.get(id);
		
		id = cnt++;
		idMap.put(newLiteral, id);
		formMap.put(id, newLiteral);
		
		// Group all literals with the same hash together
		Set<Integer> set = hashedMap.get(newLiteral.hashCode());
		if (set == null) {
			set = new HashSet<Integer>();
			hashedMap.put(newLiteral.hashCode(), set);
		}
		
		// Create a theta map of this theta against other literals
		// that are equivalent
		Iterator<Integer> it = set.iterator();
		while(it.hasNext()) {
			Integer otherID = it.next();
			Literal other = getLiteral(otherID);
			Map<Integer, Integer> theta = newLiteral.getSubRule(other);
			if (theta != null) {
				thetaMap.put(new Pair(id, otherID), theta);
			}
			
			theta = other.getSubRule(newLiteral);
			if (theta != null) {
				thetaMap.put(new Pair(id, otherID), theta);
			}
		}
		
		Map<Integer, Integer> theta = newLiteral.getSubRule(newLiteral);
		thetaMap.put(new Pair(id, id), theta);
		
		return newLiteral;
	}
	
	public Literal getLiteral(int id) {
		return formMap.get(id);
	}
	
	public int getID(Literal literal) {
		return idMap.get(literal);
	}
}
