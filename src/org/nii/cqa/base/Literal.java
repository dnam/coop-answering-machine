/**
 * @author Maheen Bakhtyar
 * Description: Stores the unique ID, literal being negative or not and the parameters of the literal.  
 * Contains methods to access literal details and fetching the name of the literal from the symbol table based on the ID. 
 */

package org.nii.cqa.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Literal implements Comparable<Literal> {
	private int id;
	private boolean neg;
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
		this.params.set(i, value);
	}
	
	/**
	 * @return Vector<Integer> a copy of the integer vectors in the literal
	 */
	@SuppressWarnings("unchecked")
	public Vector<Integer> getAllParams() {
		return (Vector<Integer>) this.params.clone();
	}

	public Vector<Integer> getAllVars() {
		Vector<Integer> vars = new Vector<Integer>();
		for(int i = 0; i < this.params.size(); i++)
		{
			if(SymTable.getTypeID(params.get(i)) == (SymType.VARIABLE))
				vars.add(params.get(i));
			
		}
		return vars;
	}
	
	public int getParamAt(int i) {

		return this.params.get(i);
	}

	// returns the corresponding String name of the literal
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append((neg)? "-" : "" );
		str.append(SymTable.getSym(id) + "(");
		for (int i = 0; i < params.size(); i++) {
			str.append(SymTable.getSym(params.get(i)));
			if (i + 1 < params.size())
				str.append(",");
		}
		str.append(")");

		return str.toString();
	}

	public int countParams() {
		return this.params.size();
	}

	public Literal clone() {
		Literal l = new Literal();
		l.id = this.id;
		l.neg = this.neg;
		l.params.addAll(this.params);
		
		return l;
	}
	
	public String toNegTPTP() {
		StringBuilder str = new StringBuilder();
		str.append((!neg)? "-" : "" );
		str.append(SymTable.getSym(id) + "(");
		for (int i = 0; i < params.size(); i++) {
			str.append(SymTable.getSym(params.get(i)));
			if (i + 1 < params.size())
				str.append(", ");
		}
		str.append(")");

		return str.toString();
	}
	
	/**
	 * Compares two literal without regards to variable
	 * 
	 * @param other the other literal to compare against with
	 * @return positive if this literal is ranked higher
	 * 		   negative if other is ranked higher
	 * @see toCompare()
	 */
	@Override
	public int compareTo(Literal other) {
		int thisID = (this.neg)? -this.id : this.id;
		int otherID = (other.neg)? -other.id : other.id;
		if (thisID != otherID)
			return (thisID - otherID);		
		
		// Now they are of the same predicate
		int n = this.params.size();
		for (int i = 0; i < n; i++) {
			int p = this.params.get(i);
			int q = other.params.get(i);
			
			// Get the type
			SymType pType = SymTable.getTypeID(p);
			SymType qType = SymTable.getTypeID(q);
			
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
		
		int thisID = (this.neg)? -this.id : this.id;
		int otherID = (other.neg)? -other.id : other.id;
		if (thisID != otherID)
			return false;		
		
		// Now they are of the same predicate
		int n = this.params.size();
		for (int i = 0; i < n; i++) {
			int p = this.params.get(i);
			int q = other.params.get(i);
			
			// Get the type
			SymType pType = SymTable.getTypeID(p);
			SymType qType = SymTable.getTypeID(q);
			
			// if they are of the same type
			if (pType == qType && (p == q))
				continue;
			else
				return false;
		}
		
		return true;
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
	 * @return 0 or a positive value if the two are equivalent
	 * 			-1 if not
	 */
	public boolean isEquivalent(Literal other, Map<Integer, Integer> theta) {
		if (this.neg != other.neg || this.id != other.id)
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
	
	public boolean subsume(Literal other, Map<Integer, Integer> theta) {
		if (this.neg != other.neg || this.id != other.id)
			return false;
		
		if (theta == null)
			throw new NullPointerException("theta cannot be null");

		Map<Integer, Integer> localTheta = new HashMap<Integer, Integer>(theta);
		
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
				if (elem1 != elem2) // constant mismatching
					return false;
				continue; // matched constant
			}
			
			// Var vs. var. Check substitution rule that var1 -> someVar
			// if someVar != var2 (elem2) -> return false
			// if someVar == var2 -> continue to the next element
			// if no rule yet -> add rule var1->var2, continue
			Integer someVar = localTheta.get(elem1);
			if (someVar == null) { // no rules yet				
				localTheta.put(elem1, elem2); // add a new rule
			}
			else if (!someVar.equals(elem2))
				return false;
		}
		
		theta.putAll(localTheta);		
		return true;
	}
}
