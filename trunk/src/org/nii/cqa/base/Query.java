/**
 * @author Maheen Bakhtyar
 * Description: The Query Class 
 */
package org.nii.cqa.base;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Query implements Comparable<Query> {
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

	/**
	 * @param compares
	 *            if two Queries are duplicate
	 * @return the rank of query description: return 0 means duplicate queries
	 *         negative value means otherQuery(parameter query) is ranked higher
	 *         positive value means the this query is ranked higher
	 */
	@Override
	public int compareTo(Query otherQ) {

		int rank = 0;
		boolean eqFlag = true;

//		this.printQ();
//		otherQ.printQ();

		// checks if # of literals same
		if (this.sortedLiterals.size() == otherQ.sortedLiterals.size()) {
			Iterator<Literal> it1 = this.sortedLiterals.iterator();
			Iterator<Literal> it2 = otherQ.sortedLiterals.iterator();
			Literal l1 = null;
			Literal l2 = null;
			Map<Integer, Integer> theta = new HashMap<Integer, Integer>();
			while (it1.hasNext()) {
				l1 = it1.next();
				l2 = it2.next();
				
				// checks if two literals area equivalent
				if (l1.isEquivalent(l2, theta)) {
					eqFlag = true;

				} else {
					eqFlag = false;
					break;
				}
			}
			
			// TODO: Remove later. For debgugging
//			System.out.println("Substitution rules: ");
//			Set<Integer> keySet = theta.keySet();
//			for (int i : keySet) {
//				System.out.println("[ " + SymTable.getSym(i) + ", "
//						+ SymTable.getSym(theta.get(i)) + "] ");
//			}

			if (!eqFlag) // checks when Queries are not equivalent
			{				rank = 2;
				while(it1.hasNext() && (rank = l1.exactCompareTo(l2)) == 0) {
					l1 = it1.next();
					l2 = it2.next();
				}
				
				
				// In this case we have like: q1 = [q1-1][q-shared] q2=[q2-1][q-shared]
				// q1-1 and q2-1 are equivalent: there is a substitution
				// however, when that substitution applied to q-shared they are not equivalent
				// thus, just give them a random rank. Still toCompare and equals() are
				// consistent
				if (rank == 0)
					rank = 1;
			}
		} else if (this.sortedLiterals.size() < otherQ.sortedLiterals.size()) {
			rank = -1; // checks if # of literals in q1 < q2
		} else if (this.sortedLiterals.size() > otherQ.sortedLiterals.size()) {
			rank = 1; // checks if # of literals in q1 > q2
		}

		return rank;
	}
	
	public boolean equals(Query other) {
		return (compareTo(other) == 0);
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
