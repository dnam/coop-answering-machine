package org.nii.cqa.base;

import java.util.Iterator;
import java.util.Vector;

public class KnowledgeBase {
	Vector<Formula> formulaList; // List of formulae
	Vector<Rule> setSHRR; // list of SHRRs
	
	public KnowledgeBase() {
		formulaList = new Vector<Formula>();
		setSHRR = new Vector<Rule>();
	}
	
	public void add(Formula formu) {
		formulaList.add(formu);
		if (formu.isRule() && formu.getRule().isSHRR())
			setSHRR.add(formu.getRule());
	}
	
	public Iterator<Rule> iteratorSHRR() {
		return setSHRR.iterator();
	}
	
	public String toString() {
		String str = "Number of clauses: " + formulaList.size();
		return str;
	}
}
