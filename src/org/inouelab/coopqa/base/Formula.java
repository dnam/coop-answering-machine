
package org.inouelab.coopqa.base;

/**
 * A wrapper object for a formula object in
 * a knowledge base.
 * A fomula is either a clause or is a rule.
 * @see KnowledgeBase
 * @see Clause
 * @see Rule
 */
public abstract class Formula {
	private final boolean isClause;
	
	/**
	 * Constructor of a formula.
	 * This constructor also determines whether
	 * this Formula is a {@link Clause} or a {@link Rule}
	 * @param isClause	<i>true</i> if this formula is a {@link Clause}
	 * 				   	<i>false</i> if this formula is a {@link Rule}
	 * @see Rule#Rule()
	 * @see Clause#Clause()
	 */
	protected Formula(boolean isClause) {
		this.isClause = isClause;
	}
	
	
	public boolean isClause() {
		return isClause;
	}
	
	public boolean isRule() {
		return (!isClause);
	}
	
	public Clause getClause() {
		if (!isClause)
			return null;
		return (Clause) this;
	}
	
	public Rule getRule() {
		if (isClause)
			return null;
		
		return (Rule) this;
	}
	
	public abstract String toString();
	
	public abstract String toTPTP();
}
