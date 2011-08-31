package org.inouelab.coopqa.web.client;

import org.inouelab.coopqa.web.shared.JobNotFinishedException;
import org.inouelab.coopqa.web.shared.ServerErrorException;
import org.inouelab.coopqa.web.shared.WebResult;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("service")
public interface Service extends RemoteService {
	/**
	 * Submits a CoopQA job with two files
	 * @param queryFileName the query file name (provided by the server after submission)
	 * @param kbFileName the knowledge base file name (also provided by the sever)
	 * @return the String of the Job ID
	 * @throws ServerErrorException if any error occurs
	 */
	String submitFileJob(String queryFileName, String kbFileName, int depthLimit) throws ServerErrorException;
	
	/**
	 * Submits a CoopQA job with the input as Strings
	 * @param queryString the String for query
	 * @param kbString the string for the knowledge base
	 * @return the String of the job ID
	 * @throws ServerErrorException if any error occurs during communication
	 */
	String submitTextJob(String queryString, String kbString, int depthLimit) throws ServerErrorException;
	
	/**
	 * Obtains a result of the given job id
	 * @param id the id of the job. Provided by the server
	 * @return the WebResult object
	 * @throws JobNotFinishedException if the job is still in processing
	 * @throws ServerErrorException if any other error occurs
	 */
	WebResult getResult(String id) throws JobNotFinishedException, ServerErrorException;

	/**
	 * Removes a file
	 * @param fileName the file to remove. Must be a valid one
	 * @throws ServerErrorException if the file is not valid
	 */
	void removeFile(String fileName) throws ServerErrorException;
}
