package org.inouelab.coopqa.web.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import org.inouelab.coopqa.Env;
import org.inouelab.coopqa.Solver;
import org.inouelab.coopqa.base.Result;
import org.inouelab.coopqa.web.shared.WebResult;

/**
 * The class for the background thread that performs the
 * solving in the server side
 * @author Nam Dang
 *
 */
class CQARunnable implements Runnable {
	private String queryFile;
	private String kbFile;
	private String resultFile;
	private Env env;
	private File retDir;
	
	public CQARunnable(String queryFile, String kbFile, String resultFile, 
			File tmpDir, File retDir, String solarPath) {
		try {
			this.queryFile = (new File(tmpDir, queryFile)).getCanonicalPath();
		} catch (IOException e) {
			this.queryFile = null;
			makeErrorFile(resultFile, "Unable to locate query file");
			return;
		}
		
		try {
			this.kbFile = (new File(tmpDir, kbFile)).getCanonicalPath();
		} catch (IOException e) {
			this.kbFile = null;
			makeErrorFile(resultFile, "Unable to locate kb file");
			return;
		}
		
		this.resultFile = resultFile;
		this.retDir = retDir;

		// Setting the environment
		this.env = new Env();		
		
		try {
			env.setTmpDir(tmpDir.getCanonicalPath());
		} catch (IOException e) {
			makeErrorFile(resultFile, "Unable to locate temporary directory");
			return;
		}
		env.setSolarPath(solarPath);
		System.out.println("Query file: " + this.queryFile);
		System.out.println("KB file: " + this.kbFile);
		env.setQueryFile(this.queryFile);
		env.setKbFile(this.kbFile);
		try {
			env.init();
		} catch (Exception e) {
			System.out.println("Unable to initialize");
			makeErrorFile(resultFile, e.getMessage());
			return;
		}
	}

	@Override
	public void run() {	
		Result result;
		try {
			result = Solver.run(env);
			
			FileOutputStream fos = new FileOutputStream(new File(retDir, resultFile + ".tmp"));
			ObjectOutputStream oos = new ObjectOutputStream(fos);			
			WebResult set = result.webConvert();
			oos.writeObject(set);
			fos.close();
			
			File retFile = new File(retDir, resultFile);
			retFile.delete(); // delete the file
			
			File tmpFile = new File (retDir, resultFile + ".tmp");
			tmpFile.renameTo(retFile);
		} catch (Exception e) {
			e.printStackTrace();
			makeErrorFile(resultFile, e.getMessage());
		}
	}
	
	public void clean() {
		if (queryFile != null)
			(new File(queryFile)).delete();
		
		if (kbFile != null)
			(new File(kbFile)).delete();
	}
	
	public void makeErrorFile(String resultFile, String errorMsg)  {
		// Delete the result file and tmp file
		(new File(retDir, resultFile)).delete();		
		(new File (retDir, resultFile + ".tmp")).delete();
		
		// Create an error file
		FileWriter file;
		try {
			file = new FileWriter(new File(retDir, resultFile + ".error"));
			PrintWriter out  = new PrintWriter(file);
			
			out.write(errorMsg);
			file.close();
		} catch (IOException e) {
			System.err.println("IO exception under CQARunnable: " + e.getMessage());
		}
		finally {
			clean();
		}
		
	}

}
