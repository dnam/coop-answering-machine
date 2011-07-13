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
		StringBuilder str = new StringBuilder();
		int n = formulaList.size();
		for (int i = 0; i < n; i++) {
			str.append(formulaList.get(i));
			if (i < n-1)
				str.append(", ");
		}

		return str.toString();
	}
}
