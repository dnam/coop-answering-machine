/**
 * @author Nam Dang
 * A class with a static method to perform Dropping Condition operator
 * This class is only visible to the package Operators
 */
package org.inouelab.coopqa.operators;

import org.inouelab.coopqa.Env;
import org.inouelab.coopqa.SemanticSettings;
import org.inouelab.coopqa.base.*;

/**
 * Dropping Condition Operator
 * @author Nam Dang
 *
 */
final class OperatorDC extends Operator {	
	protected OperatorDC(Env job) {
		super(false, job);
	}
	
	/**
	 * Returns the set of queries after applying
	 * DC on each literal of q
	 * @param query the query to drop
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
			Query newQuery = query.dropAt(i);
			
			if (SemanticSettings.enable && newQuery.isFiltered())
				continue;
			
			if (globalSet.add(newQuery)) { // new query
				retSet.add(newQuery);
			}
			else {
				newQuery.setSkipped(true);
				retSet.add(newQuery);
			}
		}
		
		return retSet;
	}

	@Override
	public int getType() {
		return DC_t;
	}

}