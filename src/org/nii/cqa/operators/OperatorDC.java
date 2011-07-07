/**
 * @author Nam Dang
 * A class with a static method to perform Dropping Condition operator
 */
package org.nii.cqa.operators;

import org.nii.cqa.base.*;
import java.util.*;

public class OperatorDC {
	/**
	 * Returns a set of queries after performing
	 * DC upon every queries given in inSet
	 * @param inSet the input set of queries
	 */
	public static Set<Query> doOp(Set<Query> inSet) {
		Iterator<Query> it = inSet.iterator();
		Set<Query> retSet = new TreeSet<Query>();
		
		while (it.hasNext()) {
			retSet.addAll(dropQuery(it.next()));
		}

		return retSet;
	}
	
	/**
	 * Returns the set of queries after applying
	 * DC on each literal of q
	 * @param q the query to drop
	 * @return a set of queries
	 */
	public static Set<Query> dropQuery(Query q) {
		Set<Query> retSet = new TreeSet<Query>();
		
		int n = q.size();
		for (int i = 0; i < n; i++)
			retSet.add(q.dropAt(i));
		
		return retSet;
	}

}
