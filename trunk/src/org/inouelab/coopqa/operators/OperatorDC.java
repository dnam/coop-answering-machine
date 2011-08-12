/**
 * @author Nam Dang
 * A class with a static method to perform Dropping Condition operator
 * This class is only visible to the package Operators
 */
package org.inouelab.coopqa.operators;

import org.inouelab.coopqa.Env;
import org.inouelab.coopqa.base.*;

class OperatorDC extends Operator {	
	protected OperatorDC(Env job) {
		super(false, job);
	}
	
	/**
	 * Returns the set of queries after applying
	 * DC on each literal of q
	 * @param q the query to drop
	 * @return set of queries
	 */
	@Override
	QuerySet perform(Query query) {
		QuerySet retSet = new QuerySet();
		
		if (query.isSkipped())
			return retSet;
		
		int n = query.size();
		
		if (n <= 1)
			return null;
		
		for (int i = 0; i < n; i++) {
			Query q = query.dropAt(i);
			if (!globalSet.contains(q)) {
				retSet.add(q);
				globalSet.add(q);
			}
			else if (!retSet.contains(q)) { // do not add twice
				q.setSkipped(true);
				retSet.add(q);
			}
		}
		
		return retSet;
	}

	@Override
	public int getType() {
		return DC_t;
	}

}
