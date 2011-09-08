package org.inouelab.coopqa.base;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.inouelab.coopqa.Env;
import org.inouelab.coopqa.web.shared.WebLiteral;

/**
 * A class representing a <code>literal</code> in first-order logic.
 * Stores the unique ID, literal being negative or not 
 * and the parameters of the literal.<br />  
 * Contains methods to access literal details and fetching
 * the name of the literal from the symbol table based on the ID.
 * 
 * @author Maheen Bakhtyar
 * @author Nam Dang
 *  
 * @see Query
 * @see Clause
 * @see Rule
 */
public class Literal implements Comparable<Literal>, Serializable {
	
	/**
	 * An alternative comparator for {@link Collections#sort(List, Comparator)}
	 * which only differentiates two parameters if they are both constants
	 * @author Nam Dang
	 */
	public static class AltComp implements Comparator<Literal> {
		private Env env;
		
		/**
		 * Constructor
		 * @param env the environment object
		 */
		public AltComp(Env env) {
			this.env = env;
		}
		
		@Override
		public int compare(Literal l1, Literal l2) {
			int pred1 = l1.getPred()* (l1.isNegative()? -1: 1);
			int pred2 = l2.getPred()* (l2.isNegative()? -1: 1);
			
			if (pred1 != pred2)
				return (pred1 - pred2);
			
			int n = l1.size();
			for (int i = 0; i < n; i++) {
				int param1 = l1.getParamAt(i);
				int param2 = l2.getParamAt(i);
				
				// Get the type
				SymType pType = env.symTab().getTypeID(param1);
				SymType qType = env.symTab().getTypeID(param2);
				
				// if they are of the same type
				if (pType == qType) {
					// skip if we have a pair of variables
					if (pType == SymType.VARIABLE)
						continue;

					// Constant
					if (param1 == param2)
						continue;
					
					return (param1 - param2);
				}
				
				// ignore they are of different types			
			}
			
			return 0;
		}		
	}
	
	public Literal(Env env, Vector<Integer> paramList) {
		this.params = new int[paramList.size()];
		for (int i = 0; i < paramList.size(); i++)
			this.params[i] = paramList.get(i);
		
		this.hashval = null;
		this.env = env;
	}
	
	public Literal(Env env, int[] params) {
		this.params = new int[params.length];
		System.arraycopy(params, 0, this.params, 0, params.length);
		
		this.hashval = null;
		this.env = env;
	}

	/**
	 * Sets the pred of the literal's predicate
	 * @param pred the pred of predicate
	 */
	public void setPred(int pred) {
		this.pred = pred;
		hashval = null;
	}

	/**
	 * @param neg <code>true</code> if the literal is negative
	 * 			  <code>false</code> otherwise
	 */
	public void setNegative(boolean neg) {
		this.neg = neg;
		hashval = null;
	}	

	public boolean isNegative() {
		return neg;
	}
	
	/**
	 * Sets a parameter at a specified index
	 * @param i the index
	 * @param value the new parameter pred
	 */
	public void setParamAt(int i, int value) {
		params[i] = value;
		hashval = null;
	}
	
	/**
	 * @return the literal's predicate
	 */
	public int getPred() {
		return pred;
	}

	/**
	 * @param i the position to read the parameter
	 * @return the parameter at i-th position
	 */
	public int getParamAt(int i) {
		return params[i];
	}
	
	/**
	 * @return the size of the parameters
	 */
	public int size() {
		return params.length;
	}

	/**
	 * Gets all variables from the literal
	 * @return a list of all variables
	 */
	public Vector<Integer> getAllVars() {
		Vector<Integer> vars = new Vector<Integer>();
		for(int i = 0; i < params.length; i++)
		{
			if(env.symTab().getTypeID(params[i]) == (SymType.VARIABLE))
				vars.add(params[i]);
			
		}
		return vars;
	}

	/**
	 * @return the number of parameters
	 */
	public int paramSize() {
		return params.length;
	}

	@Override
	public Literal clone() {
		Literal l = new Literal(this.env, this.params);
		l.pred = this.pred;
		l.neg = this.neg;
		l.hashval = this.hashval;
		
		return l;
	}
	
	public Literal substitute(Map<Integer, Integer> theta) {
		int[] newParams = new int[this.params.length];
		for (int i = 0; i < this.params.length; i++) {
			int thisParam = this.params[i];
			if (theta.containsKey(thisParam))
				newParams[i] = theta.get(this.params[i]);
			else
				newParams[i] = thisParam;
		}
		
		Literal newLit = new Literal(this.env, newParams);
		newLit.pred = this.pred;
		newLit.neg = this.neg;
		
		return newLit;		
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append((neg)? "-" : "" );
		str.append(env.symTab().getSym(pred) + "(");
		for (int i = 0; i < params.length; i++) {
			str.append(env.symTab().getSym(params[i]));
			if (i + 1 < params.length)
				str.append(",");
		}
		str.append(")");

		return str.toString();
	}
	
	/**
	 * @return the string of the negated literal
	 */
	public String toNegatedString() {
		StringBuilder str = new StringBuilder();
		
		str.append((!neg)? "-" : "" );
		str.append(env.symTab().getSym(pred) + "(");
		
		for (int i = 0; i < params.length; i++) {
			str.append(env.symTab().getSym(params[i]));
			if (i + 1 < params.length)
				str.append(", ");
		}
		str.append(")");

		return str.toString();
	}
	
	
	@Override
	public int hashCode() {
		if (hashval == null)
			hashval = computeHash();
		
		return hashval;
	}
	
	/**
	 * Computes the hash value of this literal
	 * @return the hash value
	 */
	private int computeHash() {
		int result = 11 * pred + ((neg)? -7:5);
		
		for (int i = 0; i < params.length; i++) {
			int param = params[i];
			if (env.symTab().getTypeID(param) == SymType.CONSTANT)
				result = 37 * result + params[i];
		}
		
		return result;
	}
	
	/**
	 * Compares two literal without regards to variable
	 * 
	 * @param other the other literal to compare against with
	 * @return positive if this literal is ranked higher
	 * 		   negative if other is ranked higher
	 */
	@Override
	public int compareTo(Literal other) {
		int thisID = (this.neg)? -this.pred : this.pred;
		int otherID = (other.neg)? -other.pred : other.pred;
		if (thisID != otherID)
			return (thisID - otherID);		
		
		// Now they are of the same predicate
		int n = this.params.length;
		for (int i = 0; i < n; i++) {
			int p = this.params[i];
			int q = other.params[i];
			
			// Get the type
			SymType pType = env.symTab().getTypeID(p);
			SymType qType = env.symTab().getTypeID(q);
			
			// if they are of the same type
			if (pType == qType) {
				// skip if we have a pair of variables
				if (pType == SymType.VARIABLE)
					continue;

				// Constant
				if (p == q)
					continue;
				
				return (p - q);
			}
			
			// they are of different type
			if (pType == SymType.VARIABLE) // qType == CONSTANT
				return -1;
			else // pType == constant
				return 1;
		}
		
		return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Literal))
			return false;
		
		Literal other = (Literal) obj;
		
		int thisID = (this.neg)? -this.pred : this.pred;
		int otherID = (other.neg)? -other.pred : other.pred;
		if (thisID != otherID)
			return false;		
		
		// Now they are of the same predicate
		int n = this.params.length;
		for (int i = 0; i < n; i++) {
			int p = this.params[i];
			int q = other.params[i];
			
			// Get the type
			SymType pType = env.symTab().getTypeID(p);
			SymType qType = env.symTab().getTypeID(q);
			
			// if they are of the same type
			if (pType == qType && (p == q))
				continue;
			else
				return false;
		}
		
		return true;
	}
	
	
	/**
	 * @param other the other literal to compare with
	 * @param theta store the substitution rule for the query
	 * 			q1 (which this object belongs to) and q2 (which
	 * 				the "other" object belongs to)
	 * 		such that (q1)(theta) = q2
	 * Note that NEW SUBSTITUTION RULES will be added to THETA
	 * if we detect new substitution.
	 * Example:
	 * 
	 * Map<Integer, Integer> theta = new HashMap<..,..>();
	 * lq1-i: literal of query 1 at index i
	 * lq2-i: literal of query 2 at index i
	 * theta: replacement for subquery of q1 to subquery of q2
	 * for (each i)
	 * 	if (!lq1-i.isEquivalent(lq2-i, theta))
	 *     return false;
	 * return true;
	 * 
	 * @return 0 or a positive value if the two are equivalent
	 * 			-1 if not
	 * @see Query#equals(Object)
	 */
	public boolean isEquivalent(Literal other, Map<Integer, Integer> theta) {
		if (this.neg != other.neg || this.pred != other.pred)
			return false;
		
		if (theta == null)
			throw new NullPointerException("theta cannot be null");

		// assertion. for debugging
		assert this.params.length == other.params.length;
		
		for (int i = 0; i < this.params.length; i++) {
			int elem1 = this.params[i];
			int elem2 = other.params[i];
			
			SymType type1 = env.symTab().getTypeID(elem1);
			SymType type2 = env.symTab().getTypeID(elem2);
			
			if (type1 != type2) // different types 
				return false;
			
			if (type1 == SymType.CONSTANT) {
				if (elem1 != elem2) // constant mis
					return false;
				continue; // matched constant
			}
			
			// Var vs. var. Check substitution rule that var1 -> someVar
			// if someVar != var2 (elem2) -> return false
			// if someVar == var2 -> continue to the next element
			// if no rule yet -> add rule var1->var2, continue
			Integer someVar = theta.get(elem1);
			if (someVar == null) { // no rules yet
				// check the oposite direction: if there is any rule
				// that someOtherVar -> someVar
				// avoid case such as: theta[X/Y, U/Y] 
				// we cannot reverse it into inverse_theta[Y/X, Y/U]
				if (theta.containsValue(elem2))
					return false;
				
				// Otherwise, we have new rule
				theta.put(elem1, elem2);
			}
			else if (!someVar.equals(elem2))
				return false;
		}
		
		return true;
	}
	
	/**
	 * If this literal subsumes the other, 
	 * @param other the other literal to check against
	 * @return returns the subsumption rule
	 */
	public Map<Integer, Integer> getSubRule(Literal other) {
		if (this.neg != other.neg || this.pred != other.pred)
			return null;
		
		Map<Integer, Integer> newRules = new HashMap<Integer, Integer>();
		int n = this.params.length;
		for (int i = 0; i < n; i++) {
			int elem1 = this.params[i];
			int elem2 = other.params[i];
			
			SymType type1 = env.symTab().getTypeID(elem1);
			
			if (type1 == SymType.CONSTANT) {
				if (elem1 != elem2) // constant mismatched
					return null;
				continue; // matched constant
			}
			
			// Otherwise, we have new rule
			newRules.put(elem1, elem2);
		}
		
		return newRules;
	}
	
	/**
	 * Check if there is a theta so that (this) (theta) = other
	 * @param other the other literal the check against
	 * @param theta the substitution
	 * @return true if the current literal subsume the others,
	 * 			false otherwise
	 * @see Query#isSubsumedBy(java.util.List)
	 */
	public boolean subsume(Literal other, Map<Integer, Integer> theta) {
		if (this.neg != other.neg || this.pred != other.pred)
			return false;
		
		// check size
		if (this.params.length != other.params.length)
			throw new IllegalStateException("invalid input: two literals" +
					" must be of the same size");
		
		if (theta == null)
			throw new IllegalArgumentException("theta cannot be null");
		
		// Main code		
		Map<Integer, Integer> localTheta = new HashMap<Integer, Integer>(theta);
		
		int n = this.params.length;
		for (int i = 0; i < n; i++) {
			int thisElem = this.params[i];
			int otherElem = other.params[i];
			
			SymType thisType = env.symTab().getTypeID(thisElem);
			
			if (thisType == SymType.CONSTANT) {
				if (thisElem != otherElem) // constant mismatching
					return false;
				
				continue; // matched constant
			}

			// thisElem: a variable
			// otherElem: a constant or a variable
			// Check substitution rule that thisElem -> otherElem
			Integer someVar = localTheta.get(thisElem);
			if (someVar == null) { // no rules yet				
				localTheta.put(thisElem, otherElem); // add a new rule
			}
			else if (!someVar.equals(otherElem))
				return false;
		}
		
		theta.putAll(localTheta);
		
		return true;
	}
	
	public WebLiteral webConvert() {
		// Convert the params into an array of String
		String[] webParams = new String[params.length];
		for (int i = 0; i < params.length; i++)
			webParams[i] = env.symTab().getSym(params[i]);
		
		WebLiteral webLit = new WebLiteral(webParams);
		webLit.setPred(env.symTab().getSym(pred));
		webLit.setNegative(neg);
		
		return webLit;
	}
	
	private int 		pred;
	private boolean 	neg;
	private int[] 		params;
	private Integer 	hashval;
	private Env 		env;
	
	private static final long serialVersionUID = 87L;
}
