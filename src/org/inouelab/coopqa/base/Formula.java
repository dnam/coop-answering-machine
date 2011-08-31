
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
	
	/**
	 * @return <code>true</code> if this Formula is a clause
	 */
	public final boolean isClause() {
		return isClause;
	}
	
	/**
	 * @return <code>true</code> is this Formula is a clause
	 */
	public final boolean isRule() {
		return (!isClause);
	}
	
	/**
	 * @return the {@link Clause} object corresponding to this Formula <br/>
	 * 			<code>null</code> if this is not a Clause object
	 * @see Clause
	 */
	public final Clause getClause() {
		if (!isClause)
			return null;
		return (Clause) this;
	}
	
	/**
	 * @return a {@link Rule} object coressponding to this formula <br />
	 * 			<code>null</code> if this Formula is not a Rule
	 * @see Rule
	 */
	public final Rule getRule() {
		if (isClause)
			return null;
		
		return (Rule) this;
	}
	
	@Override
	public abstract String toString();
	
	/**
	 * @return a String in TPTP syntax of this Formula
	 */
	public abstract String toTPTP();
}
