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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.inouelab.coopqa.web.client.CQAService;
import org.inouelab.coopqa.web.shared.JobNotFinishedException;
import org.inouelab.coopqa.web.shared.ServerErrorException;
import org.inouelab.coopqa.web.shared.WebResult;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CQAServiceImpl extends RemoteServiceServlet
				implements CQAService {
	private ExecutorService executor;
	private File tmpDir;
	private File retDir;
	private String solarPath;

	@Override
	public WebResult getResult(String id) throws JobNotFinishedException, ServerErrorException {
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
				throw new ServerErrorException("Unexpected error occured when trying to read the error file.");
			}
			finally {
				if (scan != null)
					scan.close();
			}
			
			throw new ServerErrorException(ret);
		}
		
		// Unable to locate the job
		if (!tmpFile.exists() && !resultFile.exists())
			throw new ServerErrorException("Job does not exist. If you have just submitted the job, please try again in a few minutes.");
		
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
			throw new ServerErrorException("Unable to read the result file. Please try again later.");
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new ServerErrorException("Invalid file format. Contact the administrator.");
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerErrorException("Invalid file format. Contact the administrator.");
		}
		
		return result;
	}

	@Override
	public String submitTextJob(String queryString, String kbString)
			throws ServerErrorException {
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
			
			return submitFileJob(queryFile.getName(), kbFile.getName());			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ServerErrorException("IOException on server's side: " + e.getMessage());
		}
	}
	
	@Override
	public String submitFileJob(String queryFile, String kbFile)
			throws ServerErrorException {	
		System.out.println("Job submission: " + queryFile + " and " + kbFile);

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
			throw new ServerErrorException("Unable to create temp files for the job");
		}
		mainFile.delete(); // delete the main file

		// Submit the job
		executor.submit(new CQARunnable(queryFile, kbFile, resultID, tmpDir, retDir, solarPath));
		
		// Return the result ID
		return resultID;
	}
	
	public static String nextFilename() {
		return  (UUID.randomUUID().toString());
	}

	@Override
	public void destroy() {
		executor.shutdown();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		ServletContext context = config.getServletContext();
		
		executor = Executors.newFixedThreadPool(100);
		System.out.println("Initialized executor with a pool of 100");
		
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
