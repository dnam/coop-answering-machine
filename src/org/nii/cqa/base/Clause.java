package org.nii.cqa.base;

import java.util.Iterator;
import java.util.Vector;

public class Clause extends Formula {
	// A clause is a disjuction of literals
	private Vector<Literal> litVector;
	
	public Clause() {
		super(true);
		litVector = new Vector<Literal>();
	}
	
	/**
	 * @param literal the new literal
	 * adds a literal to the clause
	 */
	public void add(Literal literal) {	
		if (litVector.contains(literal)) // Remove duplicates
			return;
		
		litVector.add(literal);
	}
	
	/**
	 * @return Iterator to iterate the literal
	 */
	public Iterator<Literal> iterator() {
		return litVector.iterator();
	}
	
	@Override
	public String toString() {
		Iterator<Literal> it = litVector.iterator();
		StringBuilder str = new StringBuilder();
		while (it.hasNext()) {
			str.append(it.next());
			if (it.hasNext())
				str.append(" | ");
		}

		return str.toString();
	}

	@Override
	public String toTPTP() {
		Iterator<Literal> it = litVector.iterator();
		StringBuilder str = new StringBuilder();
		while (it.hasNext()) {
			str.append(it.next());
			if (it.hasNext())
				str.append(", ");
		}

		return str.toString();
	}
}
