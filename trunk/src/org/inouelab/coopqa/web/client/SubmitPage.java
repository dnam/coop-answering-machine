package org.inouelab.coopqa.web.client;

import gwtupload.client.IUploadStatus;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.UploadedInfo;
import gwtupload.client.SingleUploader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SubmitPage extends Composite   {
	private static CoopQAUiBinder uiBinder = GWT.create(CoopQAUiBinder.class);
	private final CQAServiceAsync cqaSrv = GWT.create(CQAService.class);
	private static final String sampleKB = "ill(pete, cough), \n" +
										"ill(marry, flu),\n" +
										"treat(pete, medi),\n" +
										"ill(X, cough) -> treat(X, medi)";
	private static final String sampleQuery = "ill(X, flu) & ill(X, cough)";

	@UiField
	SubmitButton submitButton;
	@UiField
	FlexTable formTable;

	@UiField
	Label errorLabel;
	@UiField
	FlowPanel successPanel;
	@UiField
	Hyperlink successLink;
	
	@UiField
	TextArea kbBox;
	@UiField
	TextArea queryBox;
	
	@UiField
	VerticalPanel textSubmit;
	@UiField
	VerticalPanel fileSubmit;
	
	@UiField
	Hyperlink switchLink;
	
	private final Button 		queryResetButton;
	private final Button 		kbResetButton;
	private SingleUploader 		queryUploader;
	private SingleUploader 		kbUploader;
	private Timer 				autohideTimer;
	private String 				queryPath;
	private String				kbPath;

	interface CoopQAUiBinder extends UiBinder<Widget, SubmitPage> {
	}

	public SubmitPage() {
		initWidget(uiBinder.createAndBindUi(this));
		switchMode(false);
		
		kbBox.setText(sampleKB);
		queryBox.setText(sampleQuery);
		
		queryResetButton = new Button("Reset");
		kbResetButton = new Button("Reset");
		
		Window.setTitle("Cooperative Query Answering - Job Submission");

		// Hide the success panel
		successPanel.setVisible(false);

		errorLabel.setVisible(false);
		errorLabel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				errorLabel.setVisible(false);
			}
		});
		
		queryResetButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				removeQueryFile();
			}
		});
		
		queryUploader = new SingleUploader();
		queryUploader.addOnFinishUploadHandler(new IUploader.OnFinishUploaderHandler() {

					@Override
					public void onFinish(IUploader uploader) {
						IUploadStatus.Status status = uploader.getStatus();
						switch (status) {
						case SUCCESS:
							UploadedInfo info = uploader.getServerInfo();
							queryPath = info.message;

							final FlowPanel flowPan = new FlowPanel();
							flowPan.add(new Label(info.name + " successfully uploaded"));							
							flowPan.add(queryResetButton);
							
							formTable.setWidget(0, 1, flowPan);
							if (kbPath != null)
								submitButton.setEnabled(true);
							break;
						case CANCELED:
							showError("Upload canceled");
							break;
						default:
							showError("Unexpected error when trying to upload");
							break;
						}
					}
				});
		queryUploader.setEnabled(true);
		queryUploader.setAutoSubmit(true);
		queryUploader.setSize("100%", "100%");
		queryUploader.setServletPath("CoopQA/upload");

		kbResetButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				removeKBFile();
			}
		});
		
		kbUploader = new SingleUploader();
		kbUploader.addOnFinishUploadHandler(new IUploader.OnFinishUploaderHandler() {

					@Override
					public void onFinish(IUploader uploader) {
						IUploadStatus.Status status = uploader.getStatus();
						switch (status) {
						case SUCCESS:
							UploadedInfo info = uploader.getServerInfo();
							kbPath = info.message;
							
							final FlowPanel flowPan = new FlowPanel();
							flowPan.add(new Label(info.name + " successfully uploaded"));
							flowPan.add(kbResetButton);
							
							formTable.setWidget(1, 1, flowPan);
							if (queryPath != null)
								submitButton.setEnabled(true);
							break;
						case CANCELED:
							showError("Upload canceled");
							break;
						default:
							showError("Unexpected error when trying to upload");
							break;
						}
					}
				});
		kbUploader.setAutoSubmit(true);
		kbUploader.setServletPath("CoopQA/upload");

		// Add form table
		formTable.setWidget(0, 0, new Label("CQA Query File: "));
		formTable.setWidget(0, 1, queryUploader);
		formTable.setWidget(1, 0, new Label("CQA Knowledge base File: "));
		formTable.setWidget(1, 1, kbUploader);
		formTable.getCellFormatter().setWidth(0, 0, "40%");
		formTable.getCellFormatter().setWidth(0, 1, "60%");
		formTable.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		formTable.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		// Further options

		// Timer for error message
		autohideTimer = new Timer() {
			@Override
			public void run() {
				errorLabel.setVisible(false);
				this.cancel();
			}

		};
		
		// Submit handler
		submitButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (History.getToken().equals("file")) {
					submitButton.setEnabled(false);
					queryResetButton.setVisible(false);
					kbResetButton.setVisible(false);
					submitFile();
				}
				else
					submitText();
			}
		});
	}
	
	public void submitText() {
		String queryString = queryBox.getText();
		String kbString = kbBox.getText();
		
		if (kbString.length() == 0) {
			showError("Please enter the knowledge base");
			kbBox.setFocus(true);
			return;
		}
		
		if (queryString.length() == 0) {
			showError("Please enter the query");
			queryBox.setFocus(true);
			return;
		}
		
		cqaSrv.submitTextJob(queryString, kbString, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				showError(caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				submitButton.setEnabled(false);
				kbBox.setEnabled(false);
				queryBox.setEnabled(false);
				successPanel.setVisible(true);
				switchLink.setVisible(false);
				successLink.setTargetHistoryToken(result);
			}
		});		
	}
	
	public void submitFile() {
		if (!checkFormData())
			return;
		
		cqaSrv.submitFileJob(queryPath, kbPath, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				submitButton.setEnabled(false);
				successPanel.setVisible(true);
				switchLink.setVisible(false);
				successLink.setTargetHistoryToken(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				showError(caught.getMessage());
				submitButton.setEnabled(true);
			}
		});
	}
	
	public void switchMode(boolean newFileMod) {
		if (newFileMod) { // change to file mode
			fileSubmit.setVisible(true);
			textSubmit.setVisible(false);
			switchLink.setText("Switch to Text Mode");
			switchLink.setTargetHistoryToken("");
			submitButton.setEnabled(false);
		}
		else { // change to text mode			
			removeSubmittedFiles();
			fileSubmit.setVisible(false);
			textSubmit.setVisible(true);
			switchLink.setText("Switch to File Mode");
			switchLink.setTargetHistoryToken("file");
			submitButton.setEnabled(true);
		}
	}

	// Perform data check on the form
	public boolean checkFormData() {
		if (queryPath == null) {
			showError("Query file not uploaded yet");
			return false;
		}

		if (kbPath == null) {
			showError("Knowledge base not uploaded yet");
			return false;
		}

		return true;
	}
	
	private void removeSubmittedFiles() {
		removeQueryFile();
		removeKBFile();
	}
	
	private void removeQueryFile() {
		if (queryPath != null) {
			submitButton.setEnabled(false);
			queryUploader.reset();
			queryPath = null;
			formTable.setWidget(0, 1, queryUploader);
		}
	}
	
	private void removeKBFile() {
		if (kbPath != null) {
			submitButton.setEnabled(false);
			kbUploader.reset();
			kbPath = null;
			formTable.setWidget(1, 1, kbUploader);
		}
	}

	// Shows an error messages
	public void showError(String msg) {
		autohideTimer.cancel(); // cancel the current timer
		errorLabel.setText("ERROR: " + msg);
		errorLabel.setVisible(true);
		autohideTimer.schedule(20000);
	}
}
