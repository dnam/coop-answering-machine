package org.inouelab.coopqa.web.client;

import org.inouelab.coopqa.web.shared.WebResult;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CQAServiceAsync {
	void submitFileJob(String queryFile, String kbFile,	AsyncCallback<String> callback);
	void submitTextJob(String queryString, String kbString,	AsyncCallback<String> callback);
	void getResult(String id, AsyncCallback<WebResult> callback);
}
