package org.inouelab.coopqa.solar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

import org.inouelab.coopqa.Env;
import org.inouelab.coopqa.base.*;

/**
 * A connector to connects with SOLAR. <br />
 * This connector will perfrom the following task: <br />
 * <ul>
 * <li>Converts the {@link KnowledgeBase} AND {@link Query} into <b>TPTP</b>
 * file (temp file)</li>
 * <li>Execute <b>SOLAR</b> upon this file and the result is specified to
 * another tmp file.</li>
 * <li>Parse back the answers in the result file and returns</li>
 * <ul>
 * 
 * @author Nam
 * 
 */
public class SolarConnector {
	private String SOLARPATH; // solar path
	private int maxTimePerCycle; // max: 2 minutes running time
	private Env env;
	private long lastRunTime;
	private File tmpDir;
	private static int SOLAR_DEFAULT_CODE = 902; // check with SOLAR's ExitCode
	private static int LINUX_SOLAR_CODE = 134; // check with SOLAR's ExitCode
	
	class StreamGobler extends Thread {
		InputStream is;
		
		StreamGobler(InputStream is) {
			this.is = is;
		}
		
		@Override
		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				
				while(br.readLine() != null);
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This connector will perfrom the following task: <br />
	 * <ul>
	 * <li>Converts the {@link KnowledgeBase} AND {@link Query} into <b>TPTP</b>
	 * file (temp file)</li>
	 * <li>Execute <b>SOLAR</b> upon this file and the result is specified to
	 * another tmp file.</li>
	 * <li>Parse back the answers in the result file and returns</li>
	 * <ul>
	 * 
	 * @param env
	 *            the environment of the env
	 * @param solarPath
	 *            the path to solar
	 * @param tmpDir
	 *            the temporary directory for the env
	 */
	public SolarConnector(Env env, String solarPath, File tmpDir) {
		this.maxTimePerCycle = env.getMaxTimePerCycle(); // default: 2
		this.env = env;
		this.lastRunTime = 0;

		File solarFile = new File(solarPath);
		if (!solarFile.exists() || !solarFile.isFile())
			throw new IllegalArgumentException("Invalid SOLAR path");
		SOLARPATH = solarPath;

		if (tmpDir != null && (!tmpDir.exists() || !tmpDir.isDirectory()))
			throw new IllegalArgumentException("Invalid Temp Dir");
		this.tmpDir = tmpDir;
	}

	/**
	 * Sets the SOLAR path for the connector
	 * 
	 * @param solarPath the new path to SOLAR
	 * @throws IllegalArgumentException  if the path is invalid
	 */
	public void setPath(String solarPath) {
		SOLARPATH = solarPath;
		if (!checkSOLAR())
			throw new IllegalArgumentException("Invalid SOLAR path");
	}

	/**
	 * Set the maximum running time for solar at each iteration
	 * 
	 * @param maxTimePerCycle  the new time limi in minutes
	 */
	public void setRunTime(int maxTimePerCycle) {
		if (maxTimePerCycle <= 0)
			throw new IllegalArgumentException("Invalid time limit value");
		this.maxTimePerCycle = maxTimePerCycle;
	}

	public Env getEnv() {
		return env;
	}

	/**
	 * Generates a TPTP file for SOLAR.<br />
	 * The TPTP file will consists of the formulae in the {@link KnowledgeBase}
	 * and multiple top-clauses from the given {@link QuerySet}
	 * 
	 * @param querySet
	 *            the set of {@link Query} to create top clauses
	 * @return the path to the created file
	 * @throws IOException
	 *             if IO error occurs
	 * @see KnowledgeBase#writeToFile(PrintWriter)
	 * @see Query#toTopClause()
	 */
	private File makeTPTP(Set<Query> querySet) throws IOException {
		File tmpFile = File.createTempFile("SOLAR_TPTP", ".cqa", tmpDir);

		PrintWriter out = new PrintWriter(new FileWriter(tmpFile));

		// Write the knowlege base to file
		env.kb().writeToFile(out);

		// Print an empty line
		out.println();

		// Print the queries and their PFs to the file
		Iterator<Query> it = querySet.iterator();
		while (it.hasNext()) {
			out.println(it.next().toTopClause());
		}

		out.close();

		return tmpFile;
	}

	/**
	 * @param inputFile the input file
	 * @return a String of resulted consequences
	 * @throws IOException if IO error occurs
	 */
	private Vector<String> execute(File inputFile) throws IOException {
		File tmpOutputFile = File.createTempFile("solar_temp", ".tmp", tmpDir);
		tmpOutputFile.deleteOnExit();

		String[] cmdArgs = {"java", "-jar", 
							SOLARPATH, // solar's jar file
							"-t", maxTimePerCycle + "m", // maximum execution time
							"-o", tmpOutputFile.getCanonicalPath(), // output file 
							inputFile.getCanonicalPath()};
		
		ProcessBuilder pb = new ProcessBuilder(cmdArgs);

		long before = System.nanoTime();
		Process solar = pb.start();
		
		// We must exhaust the output or deadlock
		StreamGobler outputGobler = new StreamGobler(solar.getInputStream());
		StreamGobler errorGobler = new StreamGobler(solar.getErrorStream());
		
		// Kick start the gobler
		outputGobler.start();
		errorGobler.start();
		
		// Wait for the gobler to finish
		try {
			errorGobler.join();
			outputGobler.join();
		} catch (InterruptedException e) {
			throw new IOException(e.getMessage());
		}

		int exitCode = 999;
		try {
			exitCode = solar.waitFor();
		} catch (InterruptedException e) {
			throw new IOException("SOLAR is interrupted");
		}
		
		// Destroy the process to release resources
		solar.destroy();

		lastRunTime = System.nanoTime() - before;

		if (exitCode == 0 || exitCode == 100 || exitCode == 101
				|| exitCode == 102) {
			Vector<String> strVector = new Vector<String>();
			BufferedReader br = new BufferedReader(
					new FileReader(tmpOutputFile));
			String line;
			while ((line = br.readLine()) != null)
				strVector.add(line);

			br.close();
			tmpOutputFile.delete();

			return strVector;
		}

		tmpOutputFile.delete();

		String errorMsg = "SOLAR's related error: ";
		switch (exitCode) {
		case 900:
			errorMsg += ("PARSE ERROR");
			break;
		case 901:
			errorMsg += ("FILE NOT FOUND");
			break;
		case 902:
			errorMsg += ("OPTION ERROR");
			break;
		default:
			errorMsg += ("UNKNOWN ERROR. Exit code: " + exitCode);
			break;
		}

		// throw the exception
		throw new IOException(errorMsg);
	}

	/**
	 * Answer is a list of integers (representing constants)
	 * 
	 * @param querySet set of Queries
	 * @return a map <Query ID, List< Answer>
	 */
	public AnswerMap run(Set<Query> querySet) throws Exception {
		try {
			Vector<String> retVector = null;

			File tptpFile = makeTPTP(querySet);
			retVector = execute(tptpFile);
			tptpFile.delete();

			// Cannot be empty
			if (retVector == null)
				throw new Exception(
						"Unknow error occurred in SolarConnector.run()");

			AnswerMap retMap = new AnswerMap(env);
			for (int i = 0; i < retVector.size(); i++) {
				String line = retVector.get(i).replace("conseq([+ans", "").replace("]).", "");

				String idStr = line.replaceAll("\\(.*\\)", "");
				int id = Integer.parseInt(idStr);

				List<List<Integer>> queryAnsList = retMap.get(id);
				if (queryAnsList == null) {
					queryAnsList = new Vector<List<Integer>>();
					retMap.put(id, queryAnsList);
				}
				

				String ans = line.replaceFirst("\\d+", "").replaceAll("[()]", "");
				StringTokenizer token = new StringTokenizer(ans, " ,");
				Vector<Integer> ansList = new Vector<Integer>();
				while (token.hasMoreTokens()) {
					ansList.add(env.symTab().getID(token.nextToken()));
				}
				queryAnsList.add(ansList);
			}

			retMap.setTime(lastRunTime);
			return retMap;
		} catch (Exception e) {
			throw new Exception("Error in SolarConnector.run(): "
					+ e.getMessage());
		}
	}
	
	/**
	 * Check if we can execute SOLAR upon a given set.<br />
	 * This is done by passing an invalid argument to SOLAR, which then
	 * returns OPTION_ERROR status code (= 902 as of now). <br />
	 * If SOLAR is changed, there is no guarantee that this mechanism will work. <br />
	 * <b>Tested with solar2-build310.jar</b>
	 * @return <i>true</i> if SOLAR works, <i>false</i> otherwise
	 */
	public boolean checkSOLAR() {
		String[] cmdArgs = {"java", "-jar", SOLARPATH};
		
		int okExitCode = SOLAR_DEFAULT_CODE;
		
		String currentOS = System.getProperty("os.name", "generic").toLowerCase();
		
		// Fix for linux. Somehow it returns a diferrent code 
		if (currentOS.indexOf("linux") > -1)
			okExitCode = LINUX_SOLAR_CODE;
		else if (currentOS.indexOf("windows") < 0) {
			System.out.println("WARNING: Skip checking for SOLAR in this OS. " +
					"Please verify SOLAR by yourself.");
			return true;
		}
		
		Process solar;
		try {
			ProcessBuilder pb = new ProcessBuilder(cmdArgs);
			solar = pb.start();

			// We must exhaust the output or deadlock
			StreamGobler outputGobler = new StreamGobler(solar.getInputStream());
			StreamGobler errorGobler = new StreamGobler(solar.getErrorStream());
			
			// Kick start the gobler
			errorGobler.start();
			outputGobler.start();
			
			int exitCode = solar.waitFor();
			
			// Wait for the gobler to finish
			outputGobler.join();
			errorGobler.join();
			
			if (exitCode != okExitCode)
				return false;
			
			return true;
			
		} catch (InterruptedException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}
}
