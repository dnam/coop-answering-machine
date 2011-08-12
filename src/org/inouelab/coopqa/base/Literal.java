/**
 * @author Maheen Bakhtyar
 * Description: Stores the unique ID, literal being negative or not and the parameters of the literal.  
 * Contains methods to access literal details and fetching the name of the literal from the symbol table based on the ID. 
 */

package org.inouelab.coopqa.base;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.inouelab.coopqa.web.shared.WebLiteral;

public class Literal implements Comparable<Literal>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 87L;
	private int id;
	private boolean neg;
	private Vector<Integer> params;
	private Integer hashval;
	private CoopQAJob job;

	// constructors
	public Literal(CoopQAJob job) {
		this.params = new Vector<Integer>();
		this.hashval = null;
		this.job = job;
	}

	/**
	 * Sets the id of the literal's predicate
	 * @param id the id of predicate
	 */
	public void setID(int id) {
		this.id = id;
		hashval = null;
	}

	public void setNegative(boolean neg) {
		this.neg = neg;
		hashval = null;
	}

	/**
	 * Sets multiple parameters of the literal
	 * @param params the list of params
	 */
	public void setMultiParams(Vector<Integer> params) {
		this.params.addAll(params);
		hashval = null;
	}

	/**
	 * Sets a parameter at a specified index
	 * @param i the index
	 * @param value the new parameter id
	 */
	public void setParamAt(int i, int value) {
		this.params.set(i, value);
		hashval = null;
	}
	
	public int getID() {
		return id;
	}

	public boolean isNegative() {
		return neg;
	}
	
	public int getParamAt(int i) {
		return this.params.get(i);
	}

	/**
	 * Gets all variables from the literal
	 * @return a list of all variables
	 */
	public Vector<Integer> getAllVars() {
		Vector<Integer> vars = new Vector<Integer>();
		for(int i = 0; i < this.params.size(); i++)
		{
			if(job.symTab().getTypeID(params.get(i)) == (SymType.VARIABLE))
				vars.add(params.get(i));
			
		}
		return vars;
	}

	/**
	 * @return the number of parameters
	 */
	public int paramSize() {
		return this.params.size();
	}

	@Override
	public Literal clone() {
		Literal l = new Literal(job);
		l.id = this.id;
		l.neg = this.neg;
		l.params.addAll(this.params);
		l.hashval = this.hashval;
		
		return l;
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append((neg)? "-" : "" );
		str.append(job.symTab().getSym(id) + "(");
		for (int i = 0; i < params.size(); i++) {
			str.append(job.symTab().getSym(params.get(i)));
			if (i + 1 < params.size())
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
		str.append(job.symTab().getSym(id) + "(");
		
		for (int i = 0; i < params.size(); i++) {
			str.append(job.symTab().getSym(params.get(i)));
			if (i + 1 < params.size())
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
	 * @return the hash value of the literal
	 */
	private int computeHash() {
		int result = 11 * id + ((neg)? -7:5);
		
		for (int i = 0; i < params.size(); i++) {
			int param = params.get(i);
			if (job.symTab().getTypeID(param) == SymType.CONSTANT)
				result = 37 * result + params.get(i);
		}
		
		return result;
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
			SymType pType = job.symTab().getTypeID(p);
			SymType qType = job.symTab().getTypeID(q);
			
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
			SymType pType = job.symTab().getTypeID(p);
			SymType qType = job.symTab().getTypeID(q);
			
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
			
			SymType type1 = job.symTab().getTypeID(elem1);
			SymType type2 = job.symTab().getTypeID(elem2);
			
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
	 * @param other the other literal the check against
	 * @param theta the substitution
	 * @return true if the current literal subsume the others,
	 * 			false otherwise
	 */
	public boolean subsume(Literal other, Map<Integer, Integer> theta) {
		if (this.neg != other.neg || this.id != other.id)
			return false;
		
		// check size
		if (this.params.size() != other.params.size())
			throw new IllegalStateException("invalid input: two literals" +
					" must be of the same size");
		
		if (theta == null)
			throw new IllegalArgumentException("theta cannot be null");
		
		// Main code		
		Map<Integer, Integer> localTheta = new HashMap<Integer, Integer>(theta);
		
		int n = this.params.size();
		for (int i = 0; i < n; i++) {
			int elem1 = this.params.get(i);
			int elem2 = other.params.get(i);
			
			SymType type1 = job.symTab().getTypeID(elem1);
			SymType type2 = job.symTab().getTypeID(elem2);
			
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
	
	public WebLiteral webConvert() {
		WebLiteral webLit = new WebLiteral();
		webLit.setPred(job.symTab().getSym(id));
		webLit.setNegative(neg);
		
		for (int i = 0; i < params.size(); i++)
			webLit.add(job.symTab().getSym(params.get(i)));
		
		return webLit;
	}
}
