/**
 * @author Nam Dang
 * Description: This operator class provide access to all possible operators.
 * 				Operators could be access in the form: Operator.[OP's acronym]
 */
package org.nii.cqa.operators;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.nii.cqa.base.*;

public abstract class Operator {
	// List of operator objects for external access
	public static final Operator AI = new OperatorAI();
	public static final Operator DC = new OperatorDC();
	public static final Operator GR = new OperatorGR();
	
	// A global storage for all generated queries
	protected static final Set<Query> globalSet = new HashSet<Query>();
	
	
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
		
		// Add the children
		inputSet.addChild(retSet);
		
		// Set the parents and operation
		retSet.setParent(inputSet);
		
		// Set corresponding operators
		retSet.addAllOps(inputSet);
		int type = getType(); // get the type of the operator
		switch(type) {
		case 0:
			retSet.addOperator(DC);
			break;
		case 1:
			retSet.addOperator(AI);
			break;
		case 2:
			retSet.addOperator(GR);
			break;
		}
		
		return retSet;
	}
	
	
	/**
	 * Resets the shared set of all generated queries
	 */
	 public void reset() {
		 globalSet.clear();
	 }
	/**
	 * This method is overriden in sub-classes
	 */
	abstract QuerySet perform(Query query);
	
	/**
	 * Returns the type: 0= DC, 1=AI, 2=GR
	 */
	abstract int getType();
}
