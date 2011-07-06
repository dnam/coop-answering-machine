/*
 * @author: Maheem
 * Description:
 */
package org.nii.cqa.base;

import java.util.Vector;

public class Query implements Comparable {
	private Vector<Literal> query;

	public Query() {
		query = new Vector<Literal>();
	}

	public void add(Literal literal) {
		query.add(literal);
	}

	// @author: Nam Dang
	// Converts the query into a string. Now is just a fake class
	public String toString() {
		return "this query has " + query.size() + " literals";
	}

	/*
	 * Returns 0 if the two queries are equal
	 */
	public int compareTo(Query otherQuery) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof Query)
			return compareTo((Query) o);
		return 1;
	}

}
