
package org.inouelab.coopqa.base;

import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Vector;

import org.inouelab.coopqa.Env;
import org.inouelab.coopqa.parser.KBParser;
import org.inouelab.coopqa.solar.SolarConnector;

/**
 * The class representing a knowledge base.
 * A knowledge base contains a set of {@link Formula}
 * A {@link Formula} is either a:
 * - {@link Rule}
 * - or a {@link Clause}
 */
public class KnowledgeBase {
	private Vector<Formula> formulaList; // List of formulae
	private Vector<Rule> setSHRR; // list of SHRRs
	
	public KnowledgeBase() {
		this.formulaList = new Vector<Formula>();
		this.setSHRR = new Vector<Rule>();
	}
	
	/**
	 * A static method to parse a knowledge base from a given input file
	 * with respect to a given environment (symbol table, etc).
	 * @param inputFile the input file for the knowledge base
	 * @param env the {@link Env} object
	 * @throws Exception if parsing error occurs (IO or syntax)
	 * @see KBParser
	 */
	public static KnowledgeBase parse(String inputFile, Env env) throws Exception {
		KBParser kbParser = new KBParser(new FileReader(inputFile), env);
		return (KnowledgeBase) kbParser.parse().value;
	}
	
	
	/**
	 * @param formu a new formula to be added
	 * @see Formula
	 */
	public void add(Formula formu) {
		formulaList.add(formu);
		if (formu.isRule() && formu.getRule().isSHRR())
			setSHRR.add(formu.getRule());
	}
	
	/**
	 * @return an {@link Iterator} of the list of
	 * 			single-headed range-restricted rules (SHRR)
	 * @see Rule
	 */
	public Iterator<Rule> iteratorSHRR() {
		return setSHRR.iterator();
	}
	
	@Override
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
	
	/**
	 * Converts the current knowledge base into TPTP format
	 * for execution in SOLAR.
	 * @return a string representing the KB in TPTP format
	 * @see SolarConnector
	 */
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
	
	/**
	 * Writes the current knowledge base to a file.
	 * This is better than {@link #toTPTP()} in terms of
	 * memory usageth a large KB
	 * @param writer a {@link PrintWriter} object to write to
	 * @see #toTPTP()
	 */
	public void writeToFile(PrintWriter writer) {
		for(int i = 0; i < formulaList.size(); i++)
		{
			writer.print("cnf(c" + i + ", axiom, [");
			writer.print(formulaList.get(i).toTPTP());
			writer.println("]).");
		}
	}
}
