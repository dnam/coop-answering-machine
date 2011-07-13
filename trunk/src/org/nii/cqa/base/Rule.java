package org.nii.cqa.base;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class Rule extends Formula {
	private Vector<Literal> leftSide;
	private Vector<Literal> rightSide; 
	private int trackSHRR; // tracking if the rule is SHRR
	
	public Rule() {
		super(false); // not a clause
		leftSide = new Vector<Literal>();
		rightSide = new Vector<Literal>();
		trackSHRR = -1; // unchecked
	}
	
	public void addLeft(Literal lit) {
		if (leftSide.contains(lit)) // Remove duplicates
			return;
		
		trackSHRR = -1; // unchecked
		leftSide.add(lit);
	}
	
	public void addRight(Literal lit) {
		if(rightSide.contains(lit))
			return;
		
		rightSide.add(lit);
	}
	
	private void checkSHRR() {
		if (trackSHRR != -1)
			return;
		
		if (rightSide.size() != 1) {
			trackSHRR = 0;
			return;
		}
		
		Set<Integer> setVarRight = new HashSet<Integer>();
		Set<Integer> setVarLeft = new HashSet<Integer>();
		
		for (int i = 0; i < leftSide.size(); i++) {
			Literal lit = leftSide.get(i);
			for (int j = 0; j < lit.countParams(); j++) {
				int id = lit.getParamAt(j);
				if (SymTable.getTypeID(id) == SymType.VARIABLE)
					setVarLeft.add(id);
			}
		}
		
		for (int i = 0; i < rightSide.size(); i++) {
			Literal lit = rightSide.get(i);
			for (int j = 0; j < lit.countParams(); j++) {
				int id = lit.getParamAt(j);
				if (SymTable.getTypeID(id) == SymType.VARIABLE)
					setVarLeft.add(id);
			}
		}
		
		if (setVarLeft.size() == 0 && setVarRight.size() == 0) { // ground formula
			trackSHRR = 1;
			return;
		}
		
		// check the two sets of variables
		trackSHRR = (setVarLeft.contains(setVarRight))? 0 : 1;
		return;
	}
	
	public boolean isSHRR() {
		if (trackSHRR == -1)
			checkSHRR();
		
		return (trackSHRR == 1);
	}
	
}
