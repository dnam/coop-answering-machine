package org.nii.cqa.base;

import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Iterator;
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
	
	/**
	 * @return the static knowleget base (universally accessible)
	 * @throws IllegalAccessException
	 */
	public static KnowledgeBase get() throws IllegalAccessException {
		if (KB == null)
			throw new IllegalAccessException("KB uninitialized");
		return KB;
	}
	
	/**
	 * @param inputFile the input file for the knowledgebase
	 * @throws Exception if parsing error occurs
	 */
	public static void init(String inputFile) throws Exception {
		KB = new KnowledgeBase();

		KBParser kbParser = new KBParser(new FileReader(inputFile));
		KnowledgeBase kb = (KnowledgeBase) kbParser.parse().value;		
		
		KB.formulaList = kb.formulaList;
		KB.setSHRR = kb.setSHRR;
	}
	
	/**
	 * @param formu adds a formula
	 */
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
		StringBuilder str = new StringBuilder();
		for(int i = 0; i < formulaList.size(); i++)
		{
			str.append("cnf(c" + i + ", axiom, [");
			str.append(formulaList.get(i).toTPTP());
			str.append("]).\n");
		}
		
		return str.toString();
	}
	
	public void writeToFile(PrintWriter writer) {
		for(int i = 0; i < formulaList.size(); i++)
		{
			writer.print("cnf(c" + i + ", axiom, [");
			writer.print(formulaList.get(i).toTPTP());
			writer.println("]).");
		}
	}
}
