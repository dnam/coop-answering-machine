package org.inouelab.coopqa.operators;

import java.util.Iterator;

import org.inouelab.coopqa.Env;
import org.inouelab.coopqa.base.*;

/**
 * This operator class provide access to all possible operators.<br />
 * <br/>
 * List of Operators:<br />
 * <ul>
 * <li>Operator.AI : Anti-instantiation operator</li>
 * <li>Operator.DC : Dropping Condition operator</li>
 * <li>Operator.GR : Goal Replacement operator</li>
 * </ul>
 * The operators could be differentiated with {@link #getType()}
 * <br />
 * 
 * @author Nam Dang
 */
public class Operator {
	/** Type of DC operator. 
	 * @see Operator#DC */
	public static final int DC_t = 0;
	/** Type of AI operator. 
	 * @see Operator#AI */
	public static final int AI_t = 1;
	/** Type of GR operator. 
	 * @see Operator#GR */
	public static final int GR_t = 2;
	
	// List of operator objects for external access
	/** Anti-instantiation operator */
	public Operator AI; 
	/** Dropping condition operator	*/
	public Operator DC;
	/** Goal replacement operator */
	public Operator GR;
	
	
	// A global storage for all generated queries
	protected QuerySet 	globalSet;
	protected Env 		env;
	
	private boolean		rootClass;
	// Track the time in this operator
	private long		operatorTime;
	
	/**
	 * @param env the environment object
	 */
	public Operator(Env env) {
		this(true, env);
	}

	/**
	 * A constructor for the operator class. 
	 * <code>rootClass</code> differentiates if this is a generalized class,
	 * or a sub-class of <code>Operator</code>.<br />
	 * <br/>
	 * If you plan to extends <code>Operator</code>, make sure
	 * to set <code>init</code> as <b>false</b> in your constructor.
	 * @param rootClass if <i>true</i>, we initialize the sub-operators,
	 * 			otherwise, we skip them.
	 * @param env the {@link Env} environment object
	 */
	protected Operator(boolean rootClass, Env env) {
		this.env = env;
		this.operatorTime = 0;
		this.rootClass = rootClass;
		
		if (rootClass) {
			globalSet = new QuerySet();
			AI = new OperatorAI(env);
			DC = new OperatorDC(env);
			GR = new OperatorGR(env);
			
			// Set the corresponding gloabl set
			AI.globalSet = this.globalSet;
			DC.globalSet = this.globalSet;
			GR.globalSet = this.globalSet;
			
		}
	}
	
	/**
	 * Returns a set of queries after performing
	 * a generalization operation upon every queries
	 * given in inSet
	 * @param inputSet the input set of queries
	 * @return the result set
	 * @see Operator#AI
	 * @see Operator#DC
	 * @see Operator#GR
	 */
	public final QuerySet run(QuerySet inputSet) {
		// if this is called from a non-root class
		if (rootClass)
			throw new UnsupportedOperationException();
		
		long beforeTime = System.nanoTime();
		
		Iterator<Query> it = inputSet.iterator();
		QuerySet retSet = new QuerySet();
		
		while (it.hasNext()) {
			// skip skipped query
			Query query = it.next();
			if (query.isSkipped())
				continue;
			
			QuerySet set = perform(query);
			if (set != null)
				retSet.addAll(set);
		}
		
		if (!retSet.isEmpty()) {
			// Add the children
			inputSet.addChild(retSet);
			
			// Set the parents and operation
			retSet.setParent(inputSet);
			
			// Set corresponding operators
			retSet.addAllOps(inputSet);
			int type = getType(); // get the type of the operator
			switch(type) {
			case DC_t:
				retSet.addOperator(DC_t);
				break;
			case AI_t:
				retSet.addOperator(AI_t);
				break;
			case GR_t:
				retSet.addOperator(GR_t);
				break;
			}
		}
		else
			retSet = null;
		
		// Record time
		operatorTime += (System.nanoTime() - beforeTime);
		
		return retSet;
	}
	
	/**
	 * @return the time spent in this operator
	 */
	public double getTime() {
		return ((double) operatorTime) * (1.0e-9);
	}
	
	/**
	 * Reset the operator to run generalization
	 * operations again
	 */
	 public final void reset() {
		 globalSet.clear();
		 operatorTime = 0;
	 }
	 
	/**
	 * Performs generalization upon a single query
	 * @param query the query to process
	 */
	QuerySet perform(Query query) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * @return the type of the operator
	 * @see Operator#AI_t
	 * @see Operator#DC_t
	 * @see Operator#GR_t
	 */
	public int getType() {
		throw new UnsupportedOperationException();
	}
}
