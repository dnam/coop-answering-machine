package org.inouelab.coopqa.web.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.inouelab.coopqa.web.client.Service;
import org.inouelab.coopqa.web.shared.JobNotFinishedException;
import org.inouelab.coopqa.web.shared.ServerErrorException;
import org.inouelab.coopqa.web.shared.WebResult;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 * @see Service
 */
public class ServiceImpl extends RemoteServiceServlet
				implements Service {
	
	private static final long serialVersionUID = -7245865837979234374L;
	private ExecutorService executor;
	private File tmpDir;
	private File retDir;
	private String solarPath;
	private BlockingQueue<RunnableSolver> threadQueue;

	/**
	 * Cleans up finished jobs to release system resources
	 */
	private void jobCleanUp() {
		for (int i = 0; i < threadQueue.size(); i++) {
			Thread thrd = threadQueue.poll();
			if(!thrd.isAlive()) {
				try {
					thrd.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else {
				try {
					thrd.join(10); // wait at most 10 miliseconds
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public WebResult getResult(String id) throws JobNotFinishedException, ServerErrorException {
		jobCleanUp();
		
		id = id.replace("/", "").replace("\\", "").replace(".", "");
		
		// Test if tmp file exists or not
		File tmpFile = new File(retDir, id + ".tmp");		
		File resultFile = new File(retDir, id);
		File errorFile = new File(retDir, id + ".error");
		
		// if error file exists
		if (errorFile.exists()) {
			String ret = "";
			Scanner scan = null;
			try {
				scan = new Scanner(new FileReader(errorFile));
				while (scan.hasNextLine()) {
					ret += scan.nextLine();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
				throw new ServerErrorException("unexpected error occured when trying to read the error file.");
			}
			finally {
				if (scan != null)
					scan.close();
			}
			
			throw new ServerErrorException(ret);
		}
		
		// Unable to locate the job
		if (!tmpFile.exists() && !resultFile.exists())
			throw new ServerErrorException("job does not exist. If you have just submitted the job, please try again in a few minutes.");
		
		// Tmp File exists but result file does not: unfinished job
		if (tmpFile.exists() && !resultFile.exists())
			throw new JobNotFinishedException();
		
		// Read the file
		FileInputStream fin;
		WebResult result = null;
		try {
			fin = new FileInputStream(new File(retDir, id));
			ObjectInputStream ois = new ObjectInputStream(fin);
			result = (WebResult) ois.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new ServerErrorException("unable to read the result file. Please try again later.");
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new ServerErrorException("invalid file format. Contact the administrator.");
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerErrorException("invalid file format. Contact the administrator.");
		}
		
		// Invalidate a session
		HttpSession session = this.getThreadLocalRequest().getSession(true);
		session.invalidate();
		
		return result;
	}
	
	/**
	 * Method to perform job submission.
	 * Used by {@link #submitFileJob(String, String, int)} and {@link #submitTextJob(String, String, int)}
	 * @param queryFile the query file
	 * @param kbFile the knowledge base file
	 * @return the job id
	 * @throws ServerErrorException if error occurs
	 */
	private String submitJob(String queryFile, String kbFile, int depthLimit) throws ServerErrorException {
		jobCleanUp();
		
		if (depthLimit <= 0)
			depthLimit = Integer.MAX_VALUE;
		
		// Invalidate a session
		HttpSession session = this.getThreadLocalRequest().getSession(true);
		session.invalidate();
				
		// Generate a result file
		String resultID; 
		File mainFile, tmpFile;

		try {
			do {
				resultID = nextFilename();
				mainFile = new File(retDir, resultID);
				tmpFile = new File(retDir, resultID + ".tmp");
				System.out.println( mainFile.getCanonicalPath());
			} while (!tmpFile.createNewFile() || !mainFile.createNewFile());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerErrorException("unable to create temp files for the job");
		}
		mainFile.delete(); // delete the main file

		// Create a new job
		RunnableSolver thisJob = new RunnableSolver(queryFile, kbFile, resultID, tmpDir, retDir, solarPath, depthLimit);
		threadQueue.offer(thisJob);
		
		// Execute the job
		executor.submit(thisJob);
				
		// Return the result ID
		return resultID;
	}

	@Override
	public String submitTextJob(String queryString, String kbString, int depthLimit)
			throws ServerErrorException {
		jobCleanUp();
		
		// Invalidate a session
		HttpSession session = this.getThreadLocalRequest().getSession(true);
		session.invalidate();
				
		if (queryString == null || kbString == null)
			throw new ServerErrorException("invalid input");
		
		if (queryString.length() == 0)
			throw new ServerErrorException("empty query");
		// Save the query and the knowledge base to tmp files
		try {
			File queryFile = File.createTempFile("text-", ".cqa", tmpDir);
			File kbFile = File.createTempFile("text-", ".cqa", tmpDir);
			
			if (!queryFile.exists() || !kbFile.exists())
				throw new ServerErrorException("Unable to create temporary file(s)");
			
			BufferedWriter out = new BufferedWriter(new FileWriter(queryFile));
			out.write(queryString);
			out.close();
			
			out = new BufferedWriter(new FileWriter(kbFile));
			out.write(kbString);
			out.close();
			
			return submitJob(queryFile.getName(), kbFile.getName(), depthLimit);			
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerErrorException("IOException on server's side: " + e.getMessage());
		}

		
	}
	
	@Override
	public String submitFileJob(String queryFileName, String kbFileName, int depthLimit)
			throws ServerErrorException {	
		jobCleanUp();
		
		if (queryFileName == null || kbFileName == null)
			throw new ServerErrorException("Please upload the files first");
		
		// First checking the validity of the submission
		HttpSession session = this.getThreadLocalRequest().getSession(true);
		session.invalidate();
		
		return submitJob(queryFileName, kbFileName, depthLimit);			
	}
	
	@Override
	public void removeFile(String fileName)
			throws ServerErrorException {
		jobCleanUp();
		
		// First checking the validity of the submission
		HttpSession session = this.getThreadLocalRequest().getSession(true);
		session.invalidate();
		
		if(fileName == null)	
			(new File(tmpDir, fileName)).delete();
	}
	
	/**
	 * Generates a random file name
	 * @return the next unique file name
	 */
	public static String nextFilename() {
		return  (UUID.randomUUID().toString());
	}

	@Override
	public void destroy() {
		executor.shutdown();
	}

	/**
	 * Intializes the environment:
	 * - Create a threadpool for  background processing of jobs
	 * - Setting up temp directory and result directory
	 * - Setting up SOLAR
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		ServletContext context = config.getServletContext();
		
		executor = Executors.newFixedThreadPool(100);
		System.out.println("Initialized thread executor with a pool of 100");
		
		String tmpDirPath = context.getInitParameter("tmpDir");
		if (tmpDirPath == null)
			throw new ServletException("Cannot read context setting for 'tmpDir'. Check web.xml");
		
		tmpDir = new File(tmpDirPath);
		
		if (!tmpDir.exists() || !tmpDir.isDirectory()) {
			throw new ServletException("Temp Dir at " + tmpDirPath + " does not exist");
		}
		
		String retDirPath = context.getInitParameter("retDir");
		if (retDirPath == null)
			throw new ServletException("Cannot read context setting for 'retDir'. Check web.xml");
		
		retDir = new File(retDirPath);
		if (!retDir.exists() || !retDir.isDirectory())
			throw new ServletException("Result Dir at " + retDir + " does not exist");
			
		solarPath = context.getInitParameter("solarPath");
		if (solarPath == null)
			throw new ServletException("Cannot read context setting for 'solarPath'. Check web.xml");
		
		File solarFile = new File(solarPath);
		if (!solarFile.exists() || !solarFile.isFile())
			throw new ServletException("Location for SOLAR at " + solarPath + " does not exist");
		
		try {
			System.out.println("TmpDir: " + tmpDir.getCanonicalPath());
			System.out.println("RetDir: " + retDir.getCanonicalPath());
			System.out.println("SolarPath: " + solarPath);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		}
		
		// Storing a list of threads
		threadQueue = new LinkedBlockingQueue<RunnableSolver>();
		
	}
}
