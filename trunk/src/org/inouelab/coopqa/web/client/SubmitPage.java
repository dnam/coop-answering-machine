package org.inouelab.coopqa.web.client;

import gwtupload.client.IUploadStatus;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.UploadedInfo;
import gwtupload.client.SingleUploader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * SubmitPage class that provides the interface
 * for submitting a job
 * @author Nam Dang
 *
 */
public class SubmitPage extends Composite   {
	/* Simple default examples */
	private static final String sampleKB = "ill(pete, cough), \n" +
											"ill(marry, flu),\n" +
											"treat(pete, medi),\n" +
											"ill(X, cough) -> treat(X, medi)";
	private static final String sampleQuery = "ill(X, flu) & ill(X, cough)";

	// Binders
	private static CoopQAUiBinder uiBinder = GWT.create(CoopQAUiBinder.class);
	private final ServiceAsync cqaSrv = GWT.create(Service.class);
	
	
	// UiField objects
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
	
	@UiField
	TextBox depthInput;
	
	private final Button 		queryResetButton;
	private final Button 		kbResetButton;
	private SingleUploader 		queryUploader;
	private SingleUploader 		kbUploader;
	private Timer 				autohideTimer;
	private String 				queryPath;
	private String				kbPath;

	interface CoopQAUiBinder extends UiBinder<Widget, SubmitPage> {
	}

	/**
	 * Constructor of the submission page
	 */
	public SubmitPage() {
		initWidget(uiBinder.createAndBindUi(this));
		
		Window.setTitle("Cooperative Query Answering - Job Submission");
				
		// Setting up for error and success notification
		errorLabel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				errorLabel.setVisible(false);
			}
		});
		
		// Timer for error message
		autohideTimer = new Timer() {
			@Override
			public void run() {
				errorLabel.setVisible(false);
				this.cancel();
			}

		};
		
		// Depth Input box
		depthInput.addFocusHandler(new FocusHandler() {			
			@Override
			public void onFocus(FocusEvent event) {
				depthInput.setText("");
			}
		});		
		depthInput.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String text = depthInput.getText().replaceAll("[^0-9]", "");
				if (text.length() == 0)
					text = "0";
				depthInput.setText(text);
			}
		});
				
		// Query and KB file text input
		kbBox.setText(sampleKB);
		queryBox.setText(sampleQuery);
		
		// Query and KB file upload
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
		
		// Upload reset buttons
		queryResetButton = new Button("Reset");	
		queryResetButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				resetQueryUploadForm();
			}
		});
		
		kbResetButton = new Button("Reset");		
		kbResetButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				resetKBUploadForm();
			}
		});
		
		// Add the two uploaders to a form table
		formTable.setWidget(0, 0, new Label("CQA Query File: "));
		formTable.setWidget(0, 1, queryUploader);
		formTable.setWidget(1, 0, new Label("CQA Knowledge base File: "));
		formTable.setWidget(1, 1, kbUploader);
		formTable.getCellFormatter().setWidth(0, 0, "40%");
		formTable.getCellFormatter().setWidth(0, 1, "60%");
		formTable.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		formTable.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		
		// Submit handler
		submitButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (History.getToken().equals("file")) {
					submitButton.setEnabled(false);
					queryResetButton.setVisible(false);
					kbResetButton.setVisible(false);					
					submitFileMode();
				}
				else {
					submitTextMode();
				}
			}
		});
		
		// First switch to text input mode
		switchMode(false);
	}
	
	/**
	 * Submits the query and knowledge base as String
	 */
	private void submitTextMode() {
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
		
		cqaSrv.submitTextJob(queryString, kbString, getDepth(), new AsyncCallback<String>() {
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
	
	private int getDepth() {
		String text = depthInput.getText();
		if (text.equals("UNLIMITED"))
			text = "0";
		return Integer.parseInt(text);		
	}
	
	/**
	 * Submits with files
	 */
	private void submitFileMode() {
		if (!checkFormData())
			return;
		
		cqaSrv.submitFileJob(queryPath, kbPath, getDepth(), new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				kbPath = null;
				queryPath = null;
				
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
	
	/**
	 * Switching between file mode and text input mode
	 * @param toFileMode <i>true</i> to switch to file mode<br/>
	 * 					<i>false</i> to switch to text input mode
	 */
	public void switchMode(boolean toFileMode) {
		successPanel.setVisible(false); // Hide the success panel		
		errorLabel.setVisible(false); // hide error
		
		if (toFileMode) { // change to file mode
			removeSubmittedFiles();
			
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
			kbBox.setEnabled(true);
			queryBox.setEnabled(true);	
			submitButton.setEnabled(true);
		}
	}

	/**
	 * Perform validity checking on the form.
	 * Also set the appropriate error message
	 * @return <i>true</i> if the data is valid<br />
	 * 			<i>false</i> if not
	 */
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
	
	/**
	 * Remove the submitted files
	 */
	private void removeSubmittedFiles() {
		resetQueryUploadForm();
		resetKBUploadForm();
	}
	
	/**
	 * Remove the uploaded query file
	 */
	private void resetQueryUploadForm() {
		if (queryPath != null) {
			// Remove the two files
			cqaSrv.removeFile(queryPath, new AsyncCallback<Void>() {
				@Override
				public void onSuccess(Void result) {
				}
				
				@Override
				public void onFailure(Throwable caught) {
					showError("could not delete the query file: " + caught.getMessage());
				}
			});		
		}
		
		submitButton.setEnabled(false);
		queryUploader.reset();
		queryPath = null;
		formTable.setWidget(0, 1, queryUploader);
	}
	
	/**
	 * Remove the uploaded knowledge base file
	 */
	private void resetKBUploadForm() {
		if (kbPath != null) {
			// Remove the two files
			cqaSrv.removeFile(kbPath, new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void result) {
				}
				
				@Override
				public void onFailure(Throwable caught) {
					showError("could not delete the kb file: " + caught.getMessage());
				}
			});			
		}
		
		submitButton.setEnabled(false);
		kbUploader.reset();
		kbPath = null;
		formTable.setWidget(1, 1, kbUploader);			
	}

	/**
	 * Shows an error messages
	 * @param msg the message to show
	 */
	public void showError(String msg) {
		autohideTimer.cancel(); // cancel the current timer
		errorLabel.setText("ERROR: " + msg);
		errorLabel.setVisible(true);
		autohideTimer.schedule(20000);
	}
}
