
package org.inouelab.coopqa;

import java.io.File;

import org.inouelab.coopqa.base.KnowledgeBase;
import org.inouelab.coopqa.base.SymTable;
import org.inouelab.coopqa.operators.Operator;
import org.inouelab.coopqa.solar.SolarConnector;
import org.inouelab.coopqa.solar.SolarWorker;

/**
 * Provides a container for environment information.
 * This class is used by many other classes throughout the
 * system because it provides facility to access to:
 * - A shared knowledge base of queries
 * - The universal symbol table of a job
 * - Doesn't support multi-tasking: meaning you could not
 * share the same environment for two different threads (except
 * worker thread)
 * @author Nam Dang 
 */
public class Env {
	
	/**
	 * Constructor that:
	 * - Creates a {@link SymTable} object
	 * - Creates an {@link Operator} object
	 */
	public Env() {
		symTab = new SymTable();
		op = new Operator(this);
		initialized = false;
		maxTimePerCycle = 2;
		cycleSize = 20;
		depthLimit = Integer.MAX_VALUE;
		queryLimit = Integer.MAX_VALUE;
	}

	/**
	 * @return the {@link SolarConnector} object
	 * 			of the current execution environment
	 */
	public SolarConnector con() {
		if (!initialized)
			throw new IllegalAccessError("Unitialized Environment");
		
		return connector;
	}
	
	/**
	 * @return the <code>String</code> of the path to
	 * 			the knowledge base file
	 */
	public String getKbFile() {
		return kbFile;
	}
	
	/**
	 * @return the limit of the number of the queries to generate
	 * @see Solver#run(Env)
	 */
	public int getLimitval() {
		return queryLimit;
	}

	/**
	 * @return the <code>String</code> of the path of the ouput file
	 */
	public String getOutputFile() {
		return outputFile;
	}
	
	/**
	 * @return the <code>String</code> path of the query file
	 */
	public String getQueryFile() {
		return queryFile;
	}
	
	/**
	 * @return the <code>String</code> path of the temporary
	 * 			directory for the execution
	 */
	public String getTmpDir() {
		return tmpDir;
	}
	
	public void setDepth(int depth) {
		if (depth <= 0)
			throw new IllegalArgumentException("depthLimit must be a positive value");
		this.depthLimit = depth;
	}
	
	public int getDepth() {
		return depthLimit;
	}
	
	/**
	 * Initializes the environment. This method must be called
	 * before using this object.
	 * @throws Exception if the environment cannot be initialized
	 * @see KnowledgeBase#parse(String, Env)
	 * @see SolarConnector#SolarConnector(Env, String, File)
	 * @see SolarWorker#SolarWorker(SolarConnector, int)
	 */
	public void init() throws Exception {
		if (kbFile == null)
			throw new IllegalAccessError("No Knowledge Base file");
		
		try {
			kb = KnowledgeBase.parse(kbFile, this);
		}
		catch (Exception e) {
			throw new Exception("Unable to parse the knowlege base file");
		}
		System.out.println("INIT: Knowldge Base initialized");

		// Setting up solar
		File tmpDirFile = null;
		if (tmpDir != null)
			tmpDirFile = new File(tmpDir);
		
		connector = new SolarConnector(this, solarPath, tmpDirFile);
		worker = new SolarWorker(connector, cycleSize);
		
		if (!connector.checkSOLAR()) {
			throw new IllegalAccessError("Cannot execute SOLAR. Please check the path again");
		}
		else
			System.out.println("INIT: SOlAR CONNECTED");
		
		initialized = true;
	}
	
	/**
	 * @return the {@link KnowledgeBase} object of the environtment
	 * @throws IllegalAccessError if this {@link Env} object is not initialized
	 */
	public KnowledgeBase kb() {
		if (!initialized)
			throw new IllegalAccessError("Unitialized Environment");
		
		return kb;
	}
	
	/**
	 * @return the {@link Operator} object of this environment
	 */
	public Operator op() {
		return op;
	}
	
	/**
	 * @param cycleSize set the number of queries to process each cycle
	 * @see SolarWorker
	 */
	public void setCycleSize(int cycleSize) {
		this.cycleSize = cycleSize;
	}

	public void setKbFile(String kbFile) {
		this.kbFile = kbFile;
	}	
	
	public void setQueryLimit(int queryLimit) {
		this.queryLimit = queryLimit;
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
	
	/**
	 * @return the {@link SolarWorker} object of the environment
	 */
	public SolarWorker worker() {
		if (!initialized)
			throw new IllegalAccessError("Unitialized Environment");
		
		return worker;
	}
	
	/**
	 * @param maxTimePerCycle the maximum time to spent for solving
	 * 			done in SOLAR
	 * @see SolarConnector#run(java.util.Set)
	 */
	public void setMaxTimePerCycle(int maxTimePerCycle) {
		this.maxTimePerCycle = maxTimePerCycle;
	}

	public int getMaxTimePerCycle() {
		return maxTimePerCycle;
	}

	// Primary attribute
	private boolean initialized; // is this object initialized or not?
	private KnowledgeBase kb; // knowledge base
	private int queryLimit; // limiting number of queries
	private SymTable symTab; // symbol table
	private int depthLimit;
	
	// Operator
	private Operator op; // operator object	
	
	// SOLAR
	private SolarWorker worker; // worker

	private SolarConnector connector; // the solar connector
	private int cycleSize; // cycle size: number of queries to solve in a cycle
	private int maxTimePerCycle; // max time spent upon a cycle
	private String solarPath; // path to SOLAR
	private String tmpDir; // temporarily directory	
	
	// Files
	private String kbFile; // path to knowledge base file
	private String outputFile; // output the result to a text file
	private String queryFile; // the query file
}