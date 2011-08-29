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
public interface CQAService extends RemoteService {
	String submitFileJob(String queryFile, String kbFile) throws ServerErrorException;
	String submitTextJob(String queryString, String kbString) throws ServerErrorException;
	WebResult getResult(String id) throws JobNotFinishedException, ServerErrorException;
}
