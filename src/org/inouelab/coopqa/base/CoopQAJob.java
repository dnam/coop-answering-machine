package org.inouelab.coopqa.base;

import java.io.File;

import org.inouelab.coopqa.operators.Operator;
import org.inouelab.coopqa.solar.SolarConnector;
import org.inouelab.coopqa.solar.SolarWorker;

/**
 * Provides the wrapper for everything in CoopQA
 * @author Nam
 *
 */
public class CoopQAJob {
	private SymTable symTab;
	private KnowledgeBase kb;
	private SolarWorker worker;
	private SolarConnector connector;
	private Operator op;
	private boolean initialized;
	
	public CoopQAJob() {
		symTab = new SymTable();
		op = Operator.create(this);
		initialized = false;
	}
	
	public void init(String kbPath, String solarPath, String tmpDir) 
				throws IllegalArgumentException, Exception {
		kb = KnowledgeBase.parse(kbPath, this);

		// Setting up solar
		connector = new SolarConnector(this, solarPath, tmpDir);
		worker = new SolarWorker(connector);
		
		initialized = true;
	}

	public void init(String kbPath, String solarPath, File tmpDir)
			throws IllegalArgumentException, Exception {
		kb = KnowledgeBase.parse(kbPath, this);

		// Setting up solar
		connector = new SolarConnector(this, solarPath, tmpDir);
		worker = new SolarWorker(connector);

		initialized = true;
	}

	public SymTable symTab() {
		return symTab;
	}
	
	public KnowledgeBase kb() {
		if (!initialized)
			throw new IllegalAccessError("Unitialized job");
		
		return kb;
	}
	
	public Operator op() {
		return op;
	}
	
	public SolarConnector con() {
		if (!initialized)
			throw new IllegalAccessError("Unitialized job");
		
		return connector;
	}
	
	public SolarWorker worker() {
		if (!initialized)
			throw new IllegalAccessError("Unitialized job");
		
		return worker;
	}
	
}
