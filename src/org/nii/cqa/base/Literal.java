/**
 * @author Maheen Bakhtyar
 * Description: Stores the unique ID, literal being negative or not and the parameters of the literal.  
 * Contains methods to access literal details and fetching the name of the literal from the symbol table based on the ID. 
 */

package org.nii.cqa.base;

import java.util.Map;
import java.util.Vector;

public class Literal implements Comparable<Literal> {
	private int id;
	private Boolean neg;
	private Vector<Integer> params;

	// constructors
	public Literal() {
		params = new Vector<Integer>();
	}

	// methods
	public void setID(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	public void setNegative(boolean neg) {
		this.neg = neg;
	}

	public boolean isNegative() {
		return neg;
	}

	public void setMultiParams(Vector<Integer> params) {
		this.params.addAll(params);
	}

	public void setParamAt(int i, int value) {
		this.params.add(i, value);
	}
	
	/**
	 * @return Vector<Integer> a copy of the integer vectors in the literal
	 */
	@SuppressWarnings("unchecked")
	public Vector<Integer> getAllParams() {
		return (Vector<Integer>) this.params.clone();
	}

	public int getParamAt(int i) {

		return this.params.elementAt(i);
	}

	// returns the corresponding String name of the literal
	public String toString() {

		return SymTable.getSym(this.id);
	}

	public int countParams() {

		return this.params.size();
	}

	public void toTPTP(String q) {
		// to convert to TPTP format
	}

	/**
	 * Compares to literal. This is a relaxed comparison
	 */
	@Override
	public int compareTo(Literal other) {
		return (this.id - other.id);
	}
	
	
	/**
	 * @author Nam Dang
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
	 * @return true if the two literals are equivalent
	 */
	public boolean isEquivalent(Literal other, Map<Integer, Integer> theta) {
		if (this.id != other.id)
			return false;
		
		if (theta == null)
			throw new NullPointerException("theta cannot be null");
		
		// assertion. for debugging
		assert this.params.size() == other.params.size();
		
		int n = this.params.size();
		for (int i = 0; i < n; i++) {
			int elem1 = this.params.get(i);
			int elem2 = other.params.get(i);
			
			SymType type1 = SymTable.getTypeID(elem1);
			SymType type2 = SymTable.getTypeID(elem2);
			
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
				theta.put(elem1, elem2);
			}
			else if (someVar != elem2)
				return false;
		}
		
		return true;
	}
}
