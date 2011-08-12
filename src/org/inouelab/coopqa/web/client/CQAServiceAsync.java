package org.inouelab.coopqa.web.client;

import org.inouelab.coopqa.web.shared.WebResult;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CQAServiceAsync {
	void submitJob(String queryFile, String kbFile,	AsyncCallback<String> callback);

	void getResult(String id, AsyncCallback<WebResult> callback);

}
