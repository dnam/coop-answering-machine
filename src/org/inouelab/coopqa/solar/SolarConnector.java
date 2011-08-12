package org.inouelab.coopqa.solar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import org.inouelab.coopqa.Env;
import org.inouelab.coopqa.base.*;

public class SolarConnector {
	private String SOLARPATH; // solar path
	private int maxRunTime = 2; // max: 2 minutes running time
	private Env job;
	private long lastRunTime;
	private File tmpDir;
	private static double BILLION = ((double) (1000))*((double) (1000)) * (double) (1000);
	
	public SolarConnector(Env job,  String solarPath, File tmpDir) {
		this.maxRunTime = 0;
		this.job = job;
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
	 * Sets the path for the connection
	 * @param path the new path
	 * @throws IllegalArgumentException if the path is invalid
	 */
	public void setPath(String path) {
		File checkFile = new File(path);
		if (!checkFile.exists())
			throw new IllegalArgumentException("Invalid SOLAR path");
		
		SOLARPATH = path;
	}
	
	/**
	 * Set the maximum running time for solar at each iteration
	 * @param newTime the new time in minutes
	 */
	public void setRunTime(int newTime) {
		maxRunTime = newTime;
	}
	
	public Env getJob() {
		return job;
	}
	
	/**
	 * Generates a TPTP file for SOLAR
	 * @return the path to the created file
	 * @throws IOException 
	 * @throws IllegalAccessException 
	 */
	private String makeTPTP(Set<Query> querySet) 
		throws IOException, IllegalAccessException {
		File tmpFile = File.createTempFile("SOLAR_TPTP", ".cqa", tmpDir);
		tmpFile.deleteOnExit();
		
		PrintWriter out = new PrintWriter(new FileWriter(tmpFile));
		
		// Write the knowlege base to file
		job.kb().writeToFile(out);
		
		// Print an empty line
		out.println();
		
		// Print the queries and their PFs to the file
		Iterator<Query> it = querySet.iterator();
		while(it.hasNext()) {
			out.println(it.next().toTopClause());
		}
		
		out.close();
				
		return tmpFile.getCanonicalPath();
	}
	
	/**
	 * @param inputPath the input file
	 * @return a string of resulted consequences
	 * @throws IOException
	 */
	private Vector<String> execute(String inputPath) throws IOException {
		File tmpOutputFile = File.createTempFile("solar_temp", ".tmp", tmpDir);
		tmpOutputFile.deleteOnExit();
		
		String cmd = "java -jar " + SOLARPATH + 
		" -t " + maxRunTime + "m -o \"" 
		+ tmpOutputFile.getAbsolutePath() + "\" " + "\"" + inputPath + "\"";
		
		long before = System.nanoTime();
		Process solar = Runtime.getRuntime().exec(cmd);
		
		int exitCode = 999;
		try {
			exitCode = solar.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new IOException("Unable to run SOLAR");
		}
		
		lastRunTime = System.nanoTime() - before;
		
		if (exitCode == 0 || exitCode == 100 || exitCode == 101 || exitCode == 102) {
			Vector<String> strVector = new Vector<String>();
			BufferedReader br = new BufferedReader(new FileReader(tmpOutputFile));
			String line;
			while((line = br.readLine()) != null)
				strVector.add(line);
			
			br.close();
			
			return strVector;
		}
		
		System.err.println("SOLAR returns an error: ");
		switch(exitCode) {
		case 900:
			System.err.println("PARSE ERROR");
			break;
		case 901:
			System.err.println("FILE NOT FOUND");
			break;
		case 902:
			System.err.println("OPTION ERROR");
			break;
		default:
			System.err.println("UNKNOWN ERROR");
			break;
		}				
		
		return null;
	}
	
	
	/**
	 * Answer is a list of integers (representing constants)
	 * @param querySet set of Queries
	 * @return a map <Query ID, List< Answer>
	 */
	public AnswerMap run(Set<Query> querySet) {
		Vector<String> retVector = null;
		try {
			String tptpInput = makeTPTP(querySet);
			retVector = execute(tptpInput);

			(new File(tptpInput)).delete();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Empty :-)
		if (retVector == null)
			return null;

		AnswerMap retMap = new AnswerMap(job);
		for (int i = 0; i < retVector.size(); i++) {
			String line = retVector.get(i);
			line = line.replace("conseq([+ans", "").replace("]).", "");

			String idStr = line.replaceAll("\\(.*\\)", "");
			int id = Integer.parseInt(idStr);

			List<List<Integer>> queryAnsList = retMap.get(id);
			if (queryAnsList == null) {
				queryAnsList = new Vector<List<Integer>>();
				retMap.put(id, queryAnsList);
			}

			String ans = line.replaceAll("[[0-9]+\\(\\)]", "");
			StringTokenizer token = new StringTokenizer(ans, " ,");
			Vector<Integer> ansList = new Vector<Integer>();
			while (token.hasMoreTokens()) {
				ansList.add(job.symTab().getID(token.nextToken()));
			}
			queryAnsList.add(ansList);
		}

		retMap.setTime(((double) lastRunTime)/ BILLION);
		return retMap;
	}
	
//	public static void main(String args[]) throws Exception {
//		KnowledgeBase.init("../CQA/lib/gen_kb.txt");
//		
//		Query q = Query.parse("../CQA/lib/gen_query.txt");
//		Set<Query> qSet = new HashSet<Query>(); 
//		qSet.add(q);
//		qSet.add(Query.parse("../CQA/lib/gen_query2.txt"));
//		
//		Map<Integer, List<List<Integer>>> ansList = get().run(qSet);
//		Iterator<Integer> it = ansList.keySet().iterator();
//		while(it.hasNext()) {
//			int id = it.next();
//			System.out.println("Query ID: " + id);
//			System.out.println(ansList.get(id));
//		}
//	}
}
