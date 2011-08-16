package org.inouelab.coopqa.base;

import java.util.Iterator;
import java.util.Vector;

/**
 * An object representing a clause in a knowledge base
 * @see KnowledgeBase
 * @see Formula
 */
public class Clause extends Formula {
	private Vector<Literal> litVector; // A clause is a disjuction of literals
	
	public Clause() {
		super(true);
		litVector = new Vector<Literal>();
	}
	
	/**
	 * adds a literal to the clause
	 * @param literal the new {@link Literal}
	 */
	public void add(Literal literal) {	
		if (litVector.contains(literal)) // Remove duplicates
			return;
		
		litVector.add(literal);
	}
	
	/**
	 * @return Iterator to iterate the literal list
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
