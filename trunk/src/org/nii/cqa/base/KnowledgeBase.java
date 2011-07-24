package org.nii.cqa.base;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;

import org.nii.cqa.parser.KBParser;

public class KnowledgeBase {
	private static KnowledgeBase KB = null;
	Vector<Formula> formulaList; // List of formulae
	Vector<Rule> setSHRR; // list of SHRRs
	
	public KnowledgeBase() {
		formulaList = new Vector<Formula>();
		setSHRR = new Vector<Rule>();
	}
	
	public static KnowledgeBase get() throws IllegalAccessException {
		if (KB == null)
			throw new IllegalAccessException("KB uninitialized");
		return KB;
	}
	
	public static void initKB(String filePath) throws Exception {
		KB = new KnowledgeBase();
		KnowledgeBase newKB = parse(filePath);
		KB.formulaList = newKB.formulaList;
		KB.setSHRR = newKB.setSHRR;
	}
	
	public static KnowledgeBase parse(String filePath) throws Exception {
		KnowledgeBase newKb = new KnowledgeBase();
		
		KBParser kbp = new KBParser(new FileReader(filePath));
		KnowledgeBase kb = (KnowledgeBase) kbp.parse().value;
		
		newKb.clear();
		
		newKb.formulaList.addAll(kb.formulaList);
		newKb.setSHRR.addAll(kb.setSHRR);
		
		return newKb;
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
	
	public void clear() {
		formulaList.clear();
		setSHRR.clear();
	}
	
	public String toTPTP() {
		String TPTP = "";
		String lang = "cnf";
		String name = "c" + 1;
		String role = "axiom";
		Vector<Formula> formulae = this.formulaList;
		Vector<Rule> shrr = this.setSHRR;
		for(int i = 0; i < formulae.size(); i++)
		{
			TPTP += lang + "(" + name + ", " + role + ", " + formulae.get(i) + ")";
		}
		TPTP = TPTP.replace("&", ",");
		
		
//		formulaList.clear();
//		setSHRR.clear();
		return TPTP;
	}
}
