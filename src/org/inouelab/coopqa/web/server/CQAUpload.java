package org.inouelab.coopqa.web.server;

import java.io.File;

import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import org.apache.commons.fileupload.FileItem;

public class CQAUpload extends UploadAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7946877476507810602L;
	private File tmpDir;

	/**
	 * This method is called when all data is received in the server.
	 * 
	 * Temporary files are not deleted until the user calls
	 * removeSessionFiles(request)
	 * 
	 * Override this method to customize the behavior
	 * 
	 * @param request
	 * @param sessionFiles
	 * 
	 * @return the text/html message to be sent to the client. In the case of
	 *         null the standard response configured for this action will be
	 *         sent.
	 * 
	 * @throws UploadActionException
	 *             In the case of error
	 * 
	 */
	@Override
	public String executeAction(HttpServletRequest request,
			List<FileItem> sessionFiles) throws UploadActionException {
		String response = "";
		int cont = 0;
			
		for (FileItem item : sessionFiles) {
			if (false == item.isFormField()) {
				cont++;
				try {				
					File file = File.createTempFile("upload-", ".cqa", tmpDir);
					item.write(file);

					// / Send a customized message to the client.
					response += file.getName();
					System.out.println("File: " + file.getName());

				} catch (Exception e) {
					throw new UploadActionException(e);
				}
			}
		}

		// / Remove files from session because we have a copy of them
		removeSessionFileItems(request);

		// / Send your customized message to the client.
		return response;
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException  {
		super.init(config);
		
		String tmpDirPath = config.getServletContext().getInitParameter("tmpDir");
		if (tmpDirPath == null)
			throw new ServletException("Cannot read context setting of 'tmpDir'. Check web.xml");
		
		tmpDir = new File(tmpDirPath);
		
		if (!tmpDir.exists() || !tmpDir.isDirectory()) {
			throw new ServletException("Temp Dir at " + tmpDirPath + " does not exist");
		}
	}

}
