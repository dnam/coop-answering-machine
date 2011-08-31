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
	 * Submits a CoopQA job with the input as fikes
	 * @param queryFileName the file name of the query file returned by the server
	 * @param kbFileName the knowledge base file name returned by the server
	 * @param callback the asynchronous callback class
	 * @throws ServerErrorException if any error occurs during communication
	 * @see Service#submitTextJob(String, String, int)
	 */
	void submitFileJob(String queryFileName, String kbFileName, int depthLimit,
			AsyncCallback<String> callback);
	
	/**
	 * Submits a CoopQA job with the input as Strings
	 * @param queryString the String for query
	 * @param kbString the string for the knowledge base
	 * @param callback the asynchronous callback class
	 * @throws ServerErrorException if any error occurs during communication
	 * @see Service#submitTextJob(String, String, int)
	 */
	void submitTextJob(String queryString, String kbString,	int depthLimit, AsyncCallback<String> callback);
	
	/**
	 * Obtains a result of the given job id
	 * @param id the id of the job. Provided by the server
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
