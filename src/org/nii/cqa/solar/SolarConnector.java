package org.nii.cqa.solar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import org.nii.cqa.base.*;

import org.nii.cqa.base.KnowledgeBase;

public class SolarConnector {
	private String SOLARPATH; // solar path
	private File tmpDir; // tmp directory
	private int runCnt = 0;
	private int runTime = 2; // max: 2 minutes running time
	private CoopQAJob job;
	
	public SolarConnector(CoopQAJob job) {
		this.SOLARPATH = "C:\\Users\\Nam\\workspace\\CQA\\lib\\solar2-build310.jar"; // solar path
		this.tmpDir = new File("/tmp");
		this.runCnt = 0;
		this.runTime = 0;
		this.job = job;
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
	
	private File getTmpDir() {
		if (tmpDir != null)
			return tmpDir;
		
		tmpDir = new File("/tmp");
		
		if (tmpDir.exists())
			return tmpDir;
		
		if (!tmpDir.mkdir())
			throw new IllegalAccessError("Unable to create a tmp directory");
		
		return tmpDir;
	}
	
	/**
	 * Set the maximum running time for solar at each iteration
	 * @param newTime the new time in minutes
	 */
	public void setRunTime(int newTime) {
		runTime = newTime;
	}
	
	public CoopQAJob getJob() {
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
		File tmpDir = getTmpDir();
		
		String newFilePath = tmpDir.getPath() + "/inputSOLAR" + runCnt++;
		PrintWriter out = new PrintWriter(new FileWriter(newFilePath));
		
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
				
		return newFilePath;
	}
	
	/**
	 * @param inputPath the input file
	 * @return a string of resulted consequences
	 * @throws IOException
	 */
	private Vector<String> execute(String inputPath) throws IOException {
		String tmpOutput = inputPath + ".tmp";
		
		Process solar = Runtime.getRuntime().exec("java -jar " + SOLARPATH + 
				" -t " + runTime + "m -o " 
				+ tmpOutput + " " + inputPath);
		
		int exitCode = 999;
		try {
			exitCode = solar.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (exitCode == 0 || exitCode == 100 || exitCode == 101 || exitCode == 102) {
			Vector<String> strVector = new Vector<String>();
			BufferedReader br = new BufferedReader(new FileReader(tmpOutput));
			String line;
			while((line = br.readLine()) != null)
				strVector.add(line);
			
			br.close();
			
			// Delete the tmp file
			File tmpFile = new File(tmpOutput);
			tmpFile.delete();
			
			return strVector;
		}
		
		// Delete the tmp file
		File tmpFile = new File(tmpOutput);
		tmpFile.delete();
		
		System.out.println("SOLAR returns an error: ");
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
	public Map<Integer, List<List<Integer>>> run(Set<Query> querySet) {
		Vector<String> retVector = null;
		try {
			String tptpInput = makeTPTP(querySet);
			retVector = execute(tptpInput);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Empty :-)
		if (retVector == null)
			return null;
		
		Map<Integer, List<List<Integer>>> retMap = 
			new HashMap<Integer, List<List<Integer>>>();
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
			while(token.hasMoreTokens()) {
				ansList.add(job.symTab().getID(token.nextToken()));
			}
			queryAnsList.add(ansList);
		}
		
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
