package org.nii.cqa.operators;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.nii.cqa.base.Query;

public abstract class Operator {
	/**
	 * Returns a set of queries after performing
	 * DC upon every queries given in inSet
	 * @param inSet the input set of queries
	 */
	public static Set<Query> run(Set<Query> inputSet) {
		Iterator<Query> it = inputSet.iterator();
		Set<Query> retSet = new TreeSet<Query>();
		
		while (it.hasNext()) {
			retSet.addAll(perform(it.next()));
		}

		return retSet;
	}
	
	/**
	 * This method is overriden in sub-classes
	 */
	private static Set<Query> perform(Query query) {
		// do nothing
		return null;
	}

}
