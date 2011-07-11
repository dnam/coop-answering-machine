/**
 * @author Maheen Bakhtyar
 * Description: The Query Class 
 */
package org.nii.cqa.base;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

public class Query {
	private PriorityQueue<Literal> sortedLiterals;

	public Query() {
		sortedLiterals = new PriorityQueue<Literal>();
	}

	/**
	 * @param literal
	 *            to be added Description: add a literal to the query
	 */
	public void add(Literal literal) {
		sortedLiterals.add(literal);
	}

	/**
	 * @param literal
	 *            to be iterated
	 * @return: Iterator to iterate the literal
	 */
	public Iterator<Literal> iterator() {
		return sortedLiterals.iterator();
	}

//	/**
//	 * @param compares
//	 *            if two Queries are duplicate
//	 * @return the rank of query description: return 0 means duplicate queries
//	 *         negative value means otherQuery(parameter query) is ranked higher
//	 *         positive value means the this query is ranked higher
//	 *         The comparison is defined as follow:
//	 *         For two queries (i >= 0)
//	 *         Q1 = L0 & L1 &... & Li & Li+1 &... & Ln
//	 *         Q2 = M0 & M1 &... & Mi & Mi+1 &....& Mn
//	 *         such that:
//	 *         there exists a THETA and THOR such that
//	 *         (L0 ... Li) (THETA) = M1...Mi
//	 *         (M0 ... Mi) (THOR) = L1 ... Li
//	 *         (two way substitution)
//	 *         And
//	 *         (THETA) Li+1 != Mi+1
//	 *         or
//	 *         (THOR) Mi+1 != Li+1
//	 *         Then Q1 is ranked agaist Q2 as:
//	 *         	Li+1.exactCompareTo(Mi+1)
//	 *         
//	 */
//	public int compareTo(Query otherQ) {
//		boolean eqFlag = true;
//		
//		Iterator<Literal> it1 = this.sortedLiterals.iterator();
//		Iterator<Literal> it2 = otherQ.sortedLiterals.iterator();
//		Literal l1 = null;
//		Literal l2 = null;
//		Map<Integer, Integer> theta = new HashMap<Integer, Integer>();
//		
//		while (it1.hasNext() && it2.hasNext()) {
//			l1 = it1.next();
//			l2 = it2.next();
//			// checks if two literals area equivalent
//			if (!l1.isEquivalent(l2, theta)) {
//				eqFlag = false;
//				break;
//			}
//		}
//		
//		// if the current pair of literas is mismatched
//		if(it1.hasNext() && it2.hasNext())
//			return l1.exactCompareTo(l2);
//		
//		
//		// this query has more literals
//		if (it1.hasNext())
//			return 1;
//		
//		// the other query has more literals
//		if (it2.hasNext())
//			return -1;
//		
//		// the number of literals are the same
//		if (eqFlag)
//			return 0;
//		
//		return l1.exactCompareTo(l2);	
//	}
//	
	
	/**
	 * Returns true if the two queries are equivalent
	 * Returns false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Query))
			return false;
		
		Query other = (Query) obj;
		Iterator<Literal> it1 = this.sortedLiterals.iterator();
		Iterator<Literal> it2 = other.sortedLiterals.iterator();
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
		Iterator<Literal> it = sortedLiterals.iterator();
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
		Iterator<Literal> it = sortedLiterals.iterator();
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
		Iterator<Literal> it = sortedLiterals.iterator();
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
		return sortedLiterals.size();
	}

	/**
	 * @param i
	 *            the index to drop the literal. Assume that i < query.size()
	 *            index starts from 0 to (size - 1);
	 * @return a new query by dropping the specified literal
	 */
	public Query dropAt(int i) {
		assert i < sortedLiterals.size();

		Query q = new Query();
		q.sortedLiterals.addAll(this.sortedLiterals);

		// Remove the specified condition
		Iterator<Literal> it = q.sortedLiterals.iterator();
		Literal l = it.next();
		while (i > 0) {
			l = it.next();
			i--;
		}
		boolean b = q.sortedLiterals.remove(l);
		assert b;

		return q;
	}

}
