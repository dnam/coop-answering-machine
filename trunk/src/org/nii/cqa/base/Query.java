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

public class Query {
	private Vector<Literal> litVector;
	Map<Integer, Integer> idCountMap;
	Map<Integer, Vector<Literal>> idLitMap;

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
		Iterator<Literal> it1 = this.litVector.iterator();
		Iterator<Literal> it2 = other.litVector.iterator();
		Literal l1 = null;
		Literal l2 = null;
		Map<Integer, Integer> theta = new HashMap<Integer, Integer>();
		
		while (it1.hasNext() && it2.hasNext()) {
			l1 = it1.next();
			l2 = it2.next();
			// checks if two literals are equivalent
			if (!l1.isEquivalent(l2, theta))
				return false;
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
				str.append("&");
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
}
