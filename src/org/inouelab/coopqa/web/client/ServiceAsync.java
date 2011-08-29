package org.inouelab.coopqa.web.client;

import org.inouelab.coopqa.web.shared.JobNotFinishedException;
import org.inouelab.coopqa.web.shared.ServerErrorException;
import org.inouelab.coopqa.web.shared.WebResult;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Asynchronous interface for {@link Service}
 * @author Nam Dang
 * @see Service
 */
public interface ServiceAsync {
	/**
	 * Submits a CoopQA job with two files
	 * @param queryFileName the query file name (provided by the server after submission)
	 * @param kbFileName the knowledge base file name (also provided by the sever)
	 * @return the String of the Job ID
	 * @throws ServerErrorException if any error occurs
	 * @param callback the asynchronous callback class
	 * @see Service#submitFileJob(String, String)
	 */
	void submitFileJob(String queryFileName, String kbFileName,	AsyncCallback<String> callback);
	
	/**
	 * Submits a CoopQA job with the input as Strings
	 * @param queryString the String for query
	 * @param kbString the string for the knowledge base
	 * @return the String of the job ID
	 * @throws ServerErrorException if any error occurs during communication
	 * @param callback the asynchronous callback class
	 * @see Service#submitTextJob(String, String)
	 */
	void submitTextJob(String queryString, String kbString,	AsyncCallback<String> callback);
	
	/**
	 * Obtains a result of the given job id
	 * @param id the id of the job. Provided by the server
	 * @return the WebResult object
	 * @throws JobNotFinishedException if the job is still in processing
	 * @throws ServerErrorException if any other error occurs
	 * @see Service#getResult(String)
	 */
	void getResult(String id, AsyncCallback<WebResult> callback);

	/**
	 * Removes a file
	 * @param fileName the file to remove. Must be a valid one
	 * @param callback the asynchronous callback class
	 * @see Service#removeFile(String)
	 */
	void removeFile(String fileName, AsyncCallback<Void> callback);
}
