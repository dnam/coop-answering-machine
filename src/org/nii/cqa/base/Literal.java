/**
 * Author: Maheen Bakhtyar
 * Description: Stores the unique ID, literal being negative or not and the parameters of the literal.  
 * Contains methods to access literal details and fetching the name of the literal from the symbol table based on the ID. 
 */

package org.nii.cqa.base;

import java.util.Vector;

public class Literal {
	private int id;
	private Boolean neg;
	private Vector<Integer> params = new Vector<Integer>();
	
	//constructors
	public Literal() {
	}
	
	public Literal(int i, Boolean n, Vector v) {
		id = i;
		neg = n;
		params = v;
	}

	//methods
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}



	public void setNegative(Boolean neg) {
		this.neg = neg;
	}

	public Boolean isNegative() {
		return neg;
	}




	public void setParams(Vector<Integer> params) {
		this.params = params;
	}

	public Vector<Integer> getParams() {
		return params;
	}



	//returns the corresponding String name of the literal
	public String toString() {

		return SymTable.getSym(this.id);
	}


	public void toTPTP(String q) {
		// to convert to TPTP format
	}

}
