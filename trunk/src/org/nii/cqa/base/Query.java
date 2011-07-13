/**
 * @author Maheen Bakhtyar
 * Description: The Query Class 
 */
package org.nii.cqa.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.text.Segment;

public class Query {
	private Vector<Literal> litVector;
	private Map<Integer, Integer> idCountMap;
	private Map<Integer, Vector<Literal>> idLitMap;
	private Vector<Integer> segVector; // segment vector
	

	public Query() {
		litVector = new Vector<Literal>();
		
		// For AI operators
		idCountMap = new HashMap<Integer, Integer>();
		idLitMap = new HashMap<Integer, Vector<Literal>>();
	}
	

	/**
	 * @param literal
	 * add a literal to the query
	 */
	public void add(Literal literal) {	
		if (litVector.contains(literal)) // Remove duplicates
			return;
		
		litVector.add(literal);
		Collections.sort(litVector);
		
		// Update the countVarMap and constSet
		int n = literal.countParams(); // no. of params
		for (int i = 0; i < n; i++) {
			int id = literal.getParamAt(i);

			Integer cnt = idCountMap.get(id);
			cnt = (cnt == null)? 1 : cnt + 1;
			idCountMap.put(id, cnt);
			
			Vector<Literal> litMapping = idLitMap.get(id);
			if (litMapping == null)
				litMapping = new Vector<Literal>();
			
			litMapping.add(literal);
			idLitMap.put(id, litMapping);
		}
	}

	/**
	 * @param literal to be iterated
	 * @return: Iterator to iterate the literal
	 */
	public Iterator<Literal> iterator() {
		return litVector.iterator();
	}

	private void swap(Vector<Literal> litVector2, int i, int j) {
		Literal tmp = litVector2.get(i);
		litVector2.set(i, litVector2.get(j));
		litVector2.set(j, tmp);
	}
	
	/**
	 * Returns true if the two queries are equivalent
	 * Returns false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Query))
			return false;
		
		if (obj == this)
			return true;
		
		Query other = (Query) obj;
		
		// First they are of the same size?
		if (this.litVector.size() != other.litVector.size())
			return false;
		
		// Checking predicate fingerprint
		for (int i = 0; i < litVector.size(); i++) {
			Literal thisLit = this.litVector.get(i);
			Literal otherLit = other.litVector.get(i);
			if (thisLit.isNegative() != otherLit.isNegative())
				return false;
			if (thisLit.getID() != otherLit.getID())
				return false;
		}
		
		if (this.segVector == null)
			this.buildSegment();
		if (other.segVector == null)
			other.buildSegment();
		
		if (this.segVector == null || other.segVector == null) {
			return false;
		}
		
		// Do they have the same segment vector
		if (this.segVector.size() != other.segVector.size())
			return false;
		
		for (int i = 0; i < this.segVector.size(); i++) {
			if (this.segVector.get(i) != other.segVector.get(i))
				return false;
		}
					
		// Storing substitution rule THETA
		Map<Integer, Integer> theta = new HashMap<Integer, Integer>();
		
		// Caching substitution rule and swapping at each step
		Vector<Map<Integer, Integer>> thetaCache = new Vector<Map<Integer, Integer>>();
		Vector<Integer> swapCache = new Vector<Integer>();
		
		// Initialize the cache vectors
		for (int i = 0; i < litVector.size(); i++) {
			thetaCache.add(null);
			swapCache.add(null);
		}
		
		// Now we check segment by segment		
		int i = 0, segIdx = 0;
		boolean fallBack = false;
		while (segIdx < segVector.size()) {
			int begin = (segIdx == 0)? 0 : segVector.get(segIdx-1);
			int end = segVector.get(segIdx);
			
			while (i < end && i >= begin) { // iterate within the segment
				Literal thisLit = this.litVector.get(i);
				
				// Check against [other-i... other-(end-1)] for forwarding case
				// or other-j+1... other-(end-1) for falling back case
				boolean succeed = false;
				int beginIdx = (fallBack)? swapCache.get(i) + 1 : begin;
				
				// Restore the previous state before the fall back
				if (fallBack) {
					swap(other.litVector, i, swapCache.get(i));
					theta = thetaCache.get(i);
				}
				
				for (int j = beginIdx; j < end; j++) {
					Literal otherLit = other.litVector.get(j);
					
					// copy the current theta into a cache
					// as the isEquivalent will change theta
					Map<Integer, Integer> tmpTheta = new HashMap<Integer, Integer>();
					tmpTheta.putAll(theta);
					
					if (thisLit.isEquivalent(otherLit, theta)) {
						// swapping i <--> j
						swap(other.litVector, i, j);
						
						// Store the swap
						swapCache.set(i, j); // set the swap of other at i by j
						
						// Store the old theta into the cache
						thetaCache.set(i, tmpTheta);
						
						// set the succeed bit
						succeed = true;
						break;
					}
					
					// otherwise, try matching with the next literal
				}
				
				if (succeed) { // successful
					i++;
					fallBack = false;
				}
				else { // failed substitution. Fall back to a previous one
					thetaCache.set(i, null); // clear the memory for theta cache
					swapCache.set(i, null); // clear the memory of the swap cache
					
					i--;
					fallBack = true;
				}
			}
			
			if (i < 0)
				return false; // TOTAL FAILURE
			
			if (i < begin) // fall back to the previous segment
				segIdx--;
			
			if (i >= end)
				segIdx++;
		}

		return true;
	}
	
	/**
	 * Returns a hash value for the current query
	 * Hash value = sum of literals (negativity taken into account)
	 * This hashcode() returns a consistent value for an object
	 * If Q1 and Q2 are equivalent, Q1.hashcode() == Q2.hashcode();
	 */
	@Override
	public int hashCode() {
		int sum = 0;
		Iterator<Literal> it = litVector.iterator();
		while (it.hasNext()) {
			Literal lit = it.next();
			sum += lit.getID() * ((lit.isNegative()? -1 : 1));
		}
		
		return sum;
	}

	/**
	 * description: prints the query
	 */
	public void printQ() {
		System.out.println("Query:");
		Iterator<Literal> it = litVector.iterator();
		while (it.hasNext()) {
			Literal l = it.next();

			System.out.println("literal " + l.getID() + "(" + l.getAllParams()
					+ ")");
		}
		System.out.println("---------------------------------------");
	}

	/**
	 * @author Nam Dang description: Converts the query into a string. Now is
	 *         just a fake class
	 */
	public String toString() {
		Iterator<Literal> it = litVector.iterator();
		StringBuilder str = new StringBuilder();
		while (it.hasNext()) {
			str.append(it.next());
			if (it.hasNext())
				str.append(" & ");
		}

		return str.toString();
	}

	/**
	 * @return the size of the internal query
	 */
	public int size() {
		return litVector.size();
	}

	/********* Dropping condition ****************/
	/**
	 * @param i
	 *            the index to drop the literal. Assume that i < query.size()
	 *            index starts from 0 to (size - 1);
	 * @return a new query by dropping the specified literal
	 */
	public Query dropAt(int i) {
		assert i < litVector.size();

		Query q = this.clone();
		q.litVector.remove(i);
		
		return q;
	}
	
	/********* Anti-Instantiation ****************/
	public Query clone() {
		Query q = new Query();
		for (int i = 0; i < this.litVector.size(); i++) {
			q.add(this.litVector.get(i));
		}
		
		return q;
	}
	
	/**
	 * Builds a vectors containing segments of the query
	 * a segment = a set of equal literals (defined in Literal.compareTo() 
	 * segVector stores the index after the end (end + 1) of each segment (we can get the
	 * beginning by looking at the end of the previous segment)
	 */
	public void buildSegment() {
		if (litVector.size() == 0) // nothing to build
			return;
		
		segVector = new Vector<Integer>();

		int begin = 0, end = 1;
		while (end < litVector.size()) {
			// if we get to the next segment
			if (litVector.get(begin).compareTo(litVector.get(end)) != 0) {
				segVector.add(end);
				begin = end;
			}
			end++;
		}
		
		segVector.add(end);
	}
	
	
	/**
	 * Returns a set of possible constants and variables for
	 * Anti-instantiation
	 */
	public Set<Integer> extractSet() {
		Set<Integer> retSet = new HashSet<Integer>();
		
		Iterator<Integer> it = idCountMap.keySet().iterator();
		while(it.hasNext()) {
			int id = it.next();
			if (SymTable.getTypeID(id) == SymType.CONSTANT 
					||	idCountMap.get(id) > 1)
				retSet.add(id);
		}
		
		return retSet;
	}
	
	/**
	 * Return a set of queries by replacing a 
	 * constant and/or variable with a new one
	 * @param id the id of the const/var to be replaced
	 * @param newVar the id of the new variable
	 * @return a new set of queries contaning newVar
	 */
	public Set<Query> replace(int id, int newVar) {
		Set<Query> retSet = new HashSet<Query>();
		int repCnt = idCountMap.get(id); // the id should exists in the map
		
		// if the variable occurs only twice, we only make one replacement
		if (SymTable.getTypeID(id) == SymType.VARIABLE && repCnt == 2)
			repCnt = 1;
		
		Vector<Literal> litMapping = idLitMap.get(id);
		for (int i = 0; i < litMapping.size() && repCnt > 0; i++) {
				
			Literal l = litMapping.get(i);
			Query cloneQuery = this.clone(); // make a clone
				
			for (int j = 0; j < l.countParams() && repCnt > 0; 
						j++) {
				if (l.getParamAt(j) == id) {
					Literal newLiteral = l.clone(); // clone the literal
					newLiteral.setParamAt(j, newVar); // set new variable to the clone
					
					// Create a query
					Query newQuery = cloneQuery.clone();
					newQuery.litVector.remove(l);
					newQuery.add(newLiteral); // add the modified literal
					
					// Add to the result set
					retSet.add(newQuery);
					
					repCnt--; // decrease the counter
				}
			}
		}
	
		
		return retSet;
	}
	
	/************ Rule Matching ****************/
	
	/**
	 * matches the current query with the left side of
	 * the given rule.
	 * returns a set of resulted queries after substitution
	 */
	public Set<Query> tryMatch(Rule rule) {
		// Firts locate the matching
		
		return null;
	}
}
