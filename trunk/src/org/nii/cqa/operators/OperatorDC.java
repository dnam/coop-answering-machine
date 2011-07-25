/**
 * @author Nam Dang
 * A class with a static method to perform Dropping Condition operator
 * This class is only visible to the package Operators
 */
package org.nii.cqa.operators;

import org.nii.cqa.base.*;
import java.util.*;

class OperatorDC extends Operator {
	
	
	/**
	 * Returns the set of queries after applying
	 * DC on each literal of q
	 * @param q the query to drop
	 * @return set of queries
	 */
	@Override
	QuerySet perform(Query query) {
		QuerySet retSet = new QuerySet();
		
		int n = query.size();
		for (int i = 0; i < n; i++)
			retSet.add(query.dropAt(i));
		
		return retSet;
	}

}
