/**
 * @author Nam Dang
 * Description: This operator class provide access to all possible operators.
 * 				Operators could be access in the form: Operator.[OP's acronym]
 */
package org.nii.cqa.operators;

import java.util.Iterator;

import org.nii.cqa.base.*;

public abstract class Operator {
	// List of operator objects for external access
	public static final Operator AI = new OperatorAI();
	public static final Operator DC = new OperatorDC();
	public static final Operator GR = new OperatorGR();
	
	
	/**
	 * Returns a set of queries after performing
	 * DC upon every queries given in inSet
	 * @param inSet the input set of queries
	 */
	public QuerySet run(QuerySet inputSet) {
		Iterator<Query> it = inputSet.iterator();
		QuerySet retSet = new QuerySet();
		
		while (it.hasNext()) {
			retSet.addAll(perform(it.next()));
		}

		return retSet;
	}
	
	/**
	 * This method is overriden in sub-classes
	 */
	abstract QuerySet perform(Query query);

}
