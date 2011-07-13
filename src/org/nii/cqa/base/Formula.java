/**
 * Description: a fomula is a clause or is a rule
 */
package org.nii.cqa.base;

public abstract class Formula {
	private final boolean isClause;
	
	protected Formula(boolean val) {
		isClause = val;
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
}
