/**
 * @author Maheen Bakhtyar
 * Description: Stores the unique ID, literal being negative or not and the parameters of the literal.  
 * Contains methods to access literal details and fetching the name of the literal from the symbol table based on the ID. 
 */

package org.nii.cqa.base;

import java.util.Vector;

public class Literal {
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

}
