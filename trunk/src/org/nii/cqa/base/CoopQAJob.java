package org.nii.cqa.base;

import org.nii.cqa.operators.Operator;
import org.nii.cqa.solar.SolarConnector;
import org.nii.cqa.solar.SolarWorker;

/**
 * Provides the wrapper for everything in CoopQA
 * @author Nam
 *
 */
public class CoopQAJob {
	protected SymTable symTab;
	protected KnowledgeBase kb;
	protected SolarWorker worker;
	protected SolarConnector connector;
	protected Operator op;
	
	public CoopQAJob() {
		symTab = new SymTable();
		op = Operator.create(this);
		connector = new SolarConnector(this);
		worker = new SolarWorker(connector);
		kb = null;
	}
	
	public void init(String path) throws Exception {
		kb = KnowledgeBase.parse(path, this);		
	}
	
	public SymTable symTab() {
		return symTab;
	}
	
	public KnowledgeBase kb() {
		if (kb == null)
			throw new IllegalAccessError("Unitialized job");
		
		return kb;
	}
	
	public Operator op() {
		if (kb == null)
			throw new IllegalAccessError("Unitialized job");
		
		return op;
	}
	
	public SolarConnector con() {
		if (kb == null)
			throw new IllegalAccessError("Unitialized job");
		
		return connector;
	}
	
	public SolarWorker worker() {
		if (kb == null)
			throw new IllegalAccessError("Unitialized job");
		
		return worker;
	}
	
}
