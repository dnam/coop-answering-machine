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
	
	public static final int DC_t = 0;
	public static final int AI_t = 1;
	public static final int GR_t = 2;
	
	// A global storage for all generated queries
	protected static final QuerySet globalSet = new QuerySet();
	
	
	/**
	 * Returns a set of queries after performing
	 * DC upon every queries given in inSet
	 * @param inSet the input set of queries
	 */
	public QuerySet run(QuerySet inputSet) {
		Iterator<Query> it = inputSet.iterator();
		QuerySet retSet = new QuerySet();
		
		while (it.hasNext()) {
			QuerySet set = perform(it.next());
			if (set != null)
				retSet.addAll(set);
		}
		
		if (retSet.isEmpty())
			return null;
		
		// Add the children
		inputSet.addChild(retSet);
		
		// Set the parents and operation
		retSet.setParent(inputSet);
		
		// Set corresponding operators
		retSet.addAllOps(inputSet);
		int type = getType(); // get the type of the operator
		switch(type) {
		case DC_t:
			retSet.addOperator(DC);
			break;
		case AI_t:
			retSet.addOperator(AI);
			break;
		case GR_t:
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
	 * Performs the operation upon a single query
	 */
	abstract QuerySet perform(Query query);
	
	/**
	 * @return the type: 0= DC, 1=AI, 2=GR
	 */
	public abstract int getType();
}
