/**
 * @author Maheen Bakhtyar
 * Description: The Query Class 
 */
package org.nii.cqa.base;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

public class Query implements Comparable<Query> {
	private PriorityQueue<Literal> sortedLiterals;

	public Query() {
		sortedLiterals = new PriorityQueue<Literal>();
	}

	/**
	 * @param literal to be added
	 * Description: add a literal to the query
	 */
	public void add(Literal literal) {
		sortedLiterals.add(literal);
	}

	/**
	 * @param literal to be iterated
	 * @return: Iterator to iterate the literal
	 */
	public Iterator<Literal> iterator() {
		return sortedLiterals.iterator();
	}

	
	/**
	 * @param compares if two Queries are duplicate
	 * @return the rank of query
	 * description: return 0 means duplicate queries
	 * 				 negative value means otherQuery(parameter query) is ranked higher
	 * 				 positive value means the this query is ranked higher
	 */
	@Override
	public int compareTo(Query otherQ) { 

		int rank = 0;
		boolean eqFlag = true;

		this.printQ();
		otherQ.printQ();

		if (this.sortedLiterals.size() == otherQ.sortedLiterals.size()) 		// checks if # of literals same
		{
			Iterator<Literal> it1 = this.sortedLiterals.iterator();
			Iterator<Literal> it2 = otherQ.sortedLiterals.iterator();
			Literal l1 = null;
			Literal l2 = null;
			while (it1.hasNext()) 
			{
				l1 = it1.next();
				l2 = it2.next();
				Map<Integer, Integer> theta = new HashMap<Integer, Integer>();
				if (l1.isEquivalent(l2, theta)) 								// checks if two literals area equivalent
				{
					eqFlag = true;

				} else {
					eqFlag = false;
					break;
				}
			}

			if (!eqFlag) 														// checks when Queries are not equivalent
			{
				rank = 2; 
				rank = l1.exactCompareTo(l2);
			}
		} else if (this.sortedLiterals.size() < otherQ.sortedLiterals.size()) // checks if # of literals in q1 < q2
		{
			rank = -1;
		} else if (this.sortedLiterals.size() > otherQ.sortedLiterals.size()) // checks if # of literals in q1 > q2
		{
			rank = 1;
		}

		return rank;
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
	 * @author Nam Dang
	 * description: Converts the query into a string. Now is just a fake class
	 */
	public String toString() {
		return "this query has " + sortedLiterals.size() + " literals";
	}

	/**
	 * @return the size of the internal query
	 */
	public int size() {
		return sortedLiterals.size();
	}

	/** 
	 * @param i the index to drop the literal. Assume that i < query.size()
	 * 			index starts from 0 to (size - 1);
	 * @return a new query by dropping the specified literal
	 */
	public Query dropAt(int i) {
		assert i < sortedLiterals.size();
		Query q = new Query();
		q.sortedLiterals.addAll(this.sortedLiterals);
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
