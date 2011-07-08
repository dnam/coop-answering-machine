/**
 * @author Nam Dang
 * A class with a static method to perform Dropping Condition operator
 */
package org.nii.cqa.operators;

import org.nii.cqa.base.*;
import java.util.*;

public class OperatorDC extends Operator {
	
	
	/**
	 * Returns the set of queries after applying
	 * DC on each literal of q
	 * @param q the query to drop
	 * @return set of queries
	 */
	private static Set<Query> perform(Query query) {
		Set<Query> retSet = new TreeSet<Query>();
		
		int n = query.size();
		for (int i = 0; i < n; i++)
			retSet.add(query.dropAt(i));
		
		return retSet;
	}

}
