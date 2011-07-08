/**
 * @author Nam Dang
 * Description: This operator class provide access to all possible operators.
 * 				Operators could be access in the form: Operator.[OP's acronym]
 */
package org.nii.cqa.operators;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.nii.cqa.base.Query;

public abstract class Operator {
	// List of operator objects for external access
	public static final Operator AI = new OperatorAI();
	public static final Operator DC = new OperatorDC();
	
	
	/**
	 * Returns a set of queries after performing
	 * DC upon every queries given in inSet
	 * @param inSet the input set of queries
	 */
	public Set<Query> run(Set<Query> inputSet) {
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
	abstract Set<Query> perform(Query query);

}
