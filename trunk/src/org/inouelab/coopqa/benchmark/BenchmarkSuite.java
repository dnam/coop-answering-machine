package org.inouelab.coopqa.benchmark;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import org.inouelab.coopqa.Env;
import org.inouelab.coopqa.GenOp;
import org.inouelab.coopqa.base.*;

public class BenchmarkSuite {
	private static final String SOLAR_DEFAULT = "solar2-build310.jar";
	
	private static final String illPred = "ill"; // ill (patient, disease)
	private static final String treatPred = "treat"; // treat(patient, disease)
	private static final String patient_name = "patient_";
	private static final String disease_name = "disease_";
	private static final String med_name = "med_";
	
	private Random disease_rand;
	private Random med_rand;
	private Env env;
	private KnowledgeBase kb;
	private Query query;

	public BenchmarkSuite() {		
		this.kb = null;
		this.query = null;
		this.env = new Env();
		this.env.setSolarPath(SOLAR_DEFAULT);
	
		this.disease_rand = new Random(System.currentTimeMillis());
		this.med_rand = new Random(System.currentTimeMillis());
	}
	
	/**
	 * 
	 * @param patientSize the number of patients
	 * @param diseaseSize the number of diseases
	 * @param medSize the number of medicines (should <= diseaseSize)
	 * @param ruleLength the length of the rulel (should <= diseaseSize)
	 * @param queryLength the length of the query (should >= the ruleLength and <= diseaseSize)
	 */
	public void init(int patientSize, int diseaseSize, int medSize, int ruleLength, int queryLength) {
		kb = new KnowledgeBase();
		
		int illPredID = env.symTab().addSymbol(illPred, SymType.PREDICATE);
		int treatPredID = env.symTab().addSymbol(treatPred, SymType.PREDICATE);
		
		int varID = env.symTab().addSymbol("PatientX", SymType.VARIABLE);
		
		// Add the medicines to the knowledge base
		int[] medicines = new int[medSize];
		for (int i = 0; i < medSize; i++)
			medicines[i] = env.symTab().addSymbol(med_name + i, SymType.CONSTANT);
		
		// Assign each disease to a medicine
		Integer[] diseases = new Integer[diseaseSize];
		for (int i = 0; i < diseaseSize; i++)
			diseases[i]  = env.symTab().addSymbol(disease_name + i, SymType.CONSTANT);
		
		// Assign each patient to a <code>ruleLength</code> diseases
		for (int i = 0; i < patientSize; i++) {
			int patient_id = env.symTab().addSymbol(patient_name + i, SymType.CONSTANT);
			HashSet<Integer> diseaseSet = new HashSet<Integer>();
			
			for (int j = 0; j < ruleLength; j++) {
				int disease_id = diseases[disease_rand.nextInt(diseaseSize)];
				if (diseaseSet.contains(disease_id)) // avoid duplication
					continue;
				diseaseSet.add(disease_id);
				
				Literal lit = new Literal(env, new int[] {patient_id, disease_id});
				lit.setPred(illPredID);
				
				Clause clause = new Clause();
				clause.add(lit);
				
				kb.add(clause);
			}
		}
		
		// For each set of <code>ruleLength</code> diseases, we assign
		// them to a medication
		CombiGenerator<Integer> comb = new CombiGenerator<Integer>(Arrays.asList(diseases), ruleLength);
		while(comb.hasNext()) {
			Rule rule = new Rule();
			
			// Left hand side
			Iterator<Integer> it = comb.next().iterator();
			while (it.hasNext()) {
				int disease_id = it.next();
				Literal lit = new Literal(env, new int[] {varID, disease_id});
				lit.setPred(illPredID);
				
				rule.addLeft(lit);
			}
			
			// Right hand side
			int med_id = medicines[med_rand.nextInt(medicines.length)];
			Literal lit = new Literal(env, new int[] {varID, med_id});
			lit.setPred(treatPredID);
			rule.addRight(lit);
			
			// Add the rule to the KB
			kb.add(rule);
		}
		
		// QUery
		int queryVar = env.symTab().addSymbol("X", SymType.VARIABLE);
		HashSet<Integer> diseaseSet = new HashSet<Integer>();
		query = new Query(env);
		for (int i = 0; i < queryLength; i++) {
			int disease_id = diseases[disease_rand.nextInt(diseases.length)];
			if (diseaseSet.contains(disease_id))
				continue;
			diseaseSet.add(disease_id);
			
			Literal lit = new Literal(env, new int[] {queryVar, disease_id});
			lit.setPred(illPredID);
			
			query.add(lit);
		}
		
		System.out.println("Knowledge base size: " + kb.size());
		System.out.println("Query size: " + query.size());
		
		// Setting the environment
		env.setKB(kb);
		env.setQuery(query);
	}
	
	/**
	 * Launch the test
	 * @return the SOLAR time
	 */
	public double launch() {
		if (query == null || kb == null) {
			throw new IllegalAccessError("Please initialize first");
		}
		
		try {
			env.initNoFiles();
			
			// The root of the tree
			Result ret = GenOp.runNoSOLAR(env);
			
			return ret.getSolarTime();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public static void main(String args[]) {
		BenchmarkSuite benchmark = new BenchmarkSuite();
		
		int patientSize = 20;
		int diseaseSize = 10;
		int medSize = 10;
		int ruleLength = 5;
		int queryLength = 10;
		
		long before = System.nanoTime();
		
		benchmark.init(patientSize, diseaseSize, medSize, ruleLength, queryLength);
		double solarTime = benchmark.launch();
		
		long execTime = System.nanoTime() - before;
		
		double time = execTime / 1000000000D;
		System.out.println("Total exec time: " + time);
		System.out.println("SOLAR time: " + solarTime);
	}
}
