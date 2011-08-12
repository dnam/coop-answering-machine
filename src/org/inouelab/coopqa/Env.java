package org.inouelab.coopqa;

import java.io.File;

import org.inouelab.coopqa.base.KnowledgeBase;
import org.inouelab.coopqa.base.SymTable;
import org.inouelab.coopqa.operators.Operator;
import org.inouelab.coopqa.solar.SolarConnector;
import org.inouelab.coopqa.solar.SolarWorker;

/**
 * Environment Object for a Task
 * @author Nam
 *
 */
public class Env {
	
	public Env() {
		symTab = new SymTable();
		op = Operator.create(this);
		initialized = false;
		queryFile = null;
		kbFile = null;
		tmpDir = null;
		cycleSize = 20;
		limitVal = Integer.MAX_VALUE;
	}

	public SolarConnector con() {
		if (!initialized)
			throw new IllegalAccessError("Unitialized Environment");
		
		return connector;
	}
	
	public String getKbFile() {
		return kbFile;
	}
	
	public int getLimitval() {
		return limitVal;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public String getQueryFile() {
		return queryFile;
	}
	
	public String getTmpDir() {
		return tmpDir;
	}
	
	public void init() throws Exception {
		if (kbFile == null)
			throw new IllegalAccessError("No Knowledge Base file");
		
		try {
		kb = KnowledgeBase.parse(kbFile, this);
		}
		catch (Exception e) {
			throw new Exception("Unable to parse the knowlege base file");
		}

		// Setting up solar
		File tmpDirFile = null;
		if (tmpDir != null)
			tmpDirFile = new File(tmpDir);
		
		connector = new SolarConnector(this, solarPath, tmpDirFile);
		worker = new SolarWorker(connector, cycleSize);
		
		initialized = true;
	}
	
	public KnowledgeBase kb() {
		if (!initialized)
			throw new IllegalAccessError("Unitialized Environment");
		
		return kb;
	}
	
	public Operator op() {
		return op;
	}
	
	public void setCycleSize(int cycleSize) {
		this.cycleSize = cycleSize;
	}

	public void setKbFile(String kbFile) {
		this.kbFile = kbFile;
	}	
	
	public void setLimitVal(int limitVal) {
		this.limitVal = limitVal;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public void setQueryFile(String queryFile) {
		this.queryFile = queryFile;
	}

	public void setSolarPath(String solarPath) {
		this.solarPath = solarPath;
	}

	public void setTmpDir(String tmpDir) {
		this.tmpDir = tmpDir;
	}

	public SymTable symTab() {
		return symTab;
	}
	
	public SolarWorker worker() {
		if (!initialized)
			throw new IllegalAccessError("Unitialized Environment");
		
		return worker;
	}
	
	private SolarConnector connector; // the solar connector
	private int cycleSize; // cycle size: number of queries to solve in a cycle
	private boolean initialized; // is this object initialized or not?
	private KnowledgeBase kb; // knowledge base
	
	private int limitVal; // limiting number of queries
	
	private Operator op; // operator object
	private String solarPath; // path to SOLAR
	private SymTable symTab; // symbol table
	
	private String tmpDir; // temporarily directory
	private SolarWorker worker; // worker
	
	private String kbFile; // path to knowledge base file
	private String outputFile; // output the result to a text file
	private String queryFile; // the query file
}
