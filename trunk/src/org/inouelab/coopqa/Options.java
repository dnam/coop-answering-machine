package org.inouelab.coopqa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Parsing the argument from the shell and set appropriate values for the
 * environment
 * @author Nam Dang
 * @see Env
 *
 */
public class Options {
	private static final String SOLAR_DEFAULT = "solar2-build310.jar";
	private static final String SETTING_FILE = "coopqa.ini";
	private static final String SOLARPATH = "solar2-build310.jar";
	private static final String TMPDIR = "tmpDir";
	private static final String CYCLESIZE = "cycleSize";
	private static final String CYCLETIME = "cycleTime";
	

	/**
	 * This <code>Options</code> object will parse command-line
	 * arguments and pass it to the environment. It also initializes
	 * the {@link Env} object.
	 * @param env the current environment
	 * @see Env
	 */
	public Options(Env env) {
		this.env = env;
		this.queryFile = null;
		this.kbFile = null;
		this.outputFile = null;
		this.tmpDir = null;
		this.solar = SOLAR_DEFAULT;
		this.cycleSize = 20;
		this.maxTimePerCycle = 2;
		this.savedSettings = false;
		this.queryLimit = Integer.MAX_VALUE;
		this.depthLimit = Integer.MAX_VALUE;

		// Load the coopqa.ini file if saved before
		try {
			FileInputStream in = new FileInputStream(SETTING_FILE);
			Properties props = new Properties();
			props.load(in);

			if ((this.solar = props.getProperty(SOLARPATH)) == null)
				this.solar = SOLAR_DEFAULT;

			this.tmpDir = props.getProperty(TMPDIR);
			
			String cycleStr = props.getProperty(CYCLESIZE);
			if (cycleStr == null || (this.cycleSize = Integer.parseInt(props.getProperty(CYCLESIZE))) == 0)
				this.cycleSize = 20;
			
			String cycleTimeStr = props.getProperty(CYCLETIME);
			if (cycleTimeStr == null || (this.maxTimePerCycle = Integer.parseInt(cycleTimeStr)) <= 0)
				this.maxTimePerCycle = 2;
			
			in.close();

		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			return;
		}
	}
	
	public void printHelp() {
		PrintStream out = System.out;
	    out.println("Usage: COOPQA [OPTION]... -kb KBFILE -q QUERYFILE");
	    out.println("  -solar [PATH] Path of SOLAR's executable JAR file");
	    out.println("  -c  N         Number of queries per cycle to process");
	    out.println("  -l  N         Limit the number of output queries*");
	    out.println("  -d  N		 Limit the depth of the search tree");
	    out.println("  -o  [FILE]    Output the result to a file");
	    out.println("  -t  [PATH]    Specify the temporary folder");
	    out.println("  -s            Save the above settings** to coopqa.ini");
	    out.println("  -ct N         Maximum number of minutes for solving a cycle");
	    out.println();
	    out.println("NOTE:");
	    out.println("* The number of generated queries will not be exactly N");
	    out.println("  because queries are generated in batch. The queryLimit will");
	    out.println("  be used as a bounder so that when the number of generated");
	    out.println("  queries is above N, the execution stops.");
	    out.println();
	    out.println("** The number of cycle will not be saved. The default value");
	    out.println("  is 20 and the user should specify it again if they'd like");
	    out.println();
	}

	/**
	 * Save the options to a file when user specified '-s' paramenter
	 */
	private void save() {
		if (!savedSettings)
			return;
		
		try {
			FileOutputStream out = new FileOutputStream(SETTING_FILE);
			Properties props = new Properties();
			props.put(SOLARPATH, SOLARPATH);
			if (tmpDir != null)
				props.put(TMPDIR, tmpDir);
			props.put(CYCLESIZE, cycleSize + "");
			props.put(CYCLETIME, maxTimePerCycle + "");
			
			// Store comments
			props.store(out, "CoopQA settings saved at " + getDateTime());
			
			out.close();
			
		} catch (FileNotFoundException e) {
			System.err.println("Unable to save the settings");
			return;
		} catch (IOException e) {
			System.err.println("Error while trying to write to the setting file");
			return;
		}
	}
	
	/**
	 * Performs the intialization upon the current environment
	 * @throws IllegalArgumentException
	 * @throws Exception
	 * @see Env#init()
	 */
	private void initEnv() throws IllegalArgumentException, Exception {
		env.setKbFile(kbFile);
		env.setQueryFile(queryFile);
		env.setOutputFile(outputFile);
		env.setCycleSize(cycleSize);
		env.setSolarPath(solar);
		env.setTmpDir(tmpDir);
		env.setQueryLimit(queryLimit);
		env.setDepth(depthLimit);
		env.setMaxTimePerCycle(maxTimePerCycle);
		
		env.init();
	}
	
	/**
	 * @param args list of user-input arguments received in main
	 * @throws IllegalArgumentException if the arguments are invalid
	 * @throws Exception if we are unable to initialize the environment
	 * @see Env#init()
	 */
	public void init(String[] args) throws IllegalArgumentException, Exception {
		for (int i = 0; i < args.length; i++) {
			String op = args[i];
			String opVal = (i + 1 < args.length) ? args[i + 1] : null;

			if (op.equals("-q")) { // query file
				if (!isValidFileName(opVal))
					throw new IllegalArgumentException(
							"Invalid query file name");

				queryFile = opVal;
				i++;
			} else if (op.equals("-kb")) {
				if (!isValidFileName(opVal))
					throw new IllegalArgumentException(
							"Invalid knowledge base name");

				kbFile = opVal;
				i++;
			} else if (op.equals("-solar")) {
				if (!isValidFileName(opVal))
					throw new IllegalArgumentException("Input the solar path");

				solar = opVal;
				i++;
			} else if (op.equals("-c")) {
				if (opVal == null || (cycleSize = Integer.parseInt(opVal)) <= 0) {
					throw new IllegalArgumentException(
							"Number of queries per round must be positive");
				}
				i++;
			} else if (op.equals("-o")) {
				if (!isValidFileName(opVal))
					throw new IllegalArgumentException(
							"Invalid output file name");

				outputFile = opVal;
				i++;
			} else if (op.equals("-t")) {
				if (!isValidFileName(opVal))
					throw new IllegalArgumentException("Invalid temp path");
				i++;
			} else if (op.equals("-s")) {
				savedSettings = true;
			} else if (op.equals("-l")) {
				if (opVal == null || (queryLimit = Integer.parseInt(opVal)) <= 0)
					throw new IllegalArgumentException("Limit must be a positive value");
				i++;
			} else if (op.equals("-ct")) {
				if (opVal == null || (maxTimePerCycle = Integer.parseInt(opVal)) <= 0)
					throw new IllegalArgumentException("Time queryLimit must be a postive value");
				i++;
			} else if (op.equals("-d")) {
				System.out.println("hello: " + opVal);
				if (opVal == null || (depthLimit = Integer.parseInt(opVal)) <= 0)
					throw new IllegalArgumentException("Depth queryLimit must be a positive value");
				i++;
			}
			else
				throw new IllegalArgumentException("Invalid syntax");
		}
		
		// At least we need the input file
		if (kbFile == null)
			throw new IllegalArgumentException("Must specify the knowledge base file");
		if (queryFile == null)
			throw new IllegalArgumentException("Must specify the query file");

		// Set up the environment
		initEnv();
		
		// Save the environment
		save();
	}
	
	/**
	 * @return a spring representing the current time
	 */
	private static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

	private static boolean isValidFileName(String name) {
		if (name == null)
			return false;

		File f = new File(name);
		try {
			f.getCanonicalFile();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * For testing only. Do not use it in the final implementation
	 * 
	 * @param kbFile
	 * @param queryFile
	 * @throws Exception 
	 * @throws IllegalArgumentException 
	 */
	public void initWithInputs(String kbFile, String queryFile) throws IllegalArgumentException, Exception {
		this.kbFile = kbFile;
		this.queryFile = queryFile;
		initEnv();
	}
	
	private Env 		env;
	private String 		queryFile;
	private String 		kbFile;
	private String 		solar;
	private String 		outputFile;
	private String 		tmpDir;
	private boolean 	savedSettings;
	private int			cycleSize;
	private int 		maxTimePerCycle;
	private int 		queryLimit;
	private int			depthLimit;
}
