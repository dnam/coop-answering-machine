package org.inouelab.coopqa.web.shared;

import java.io.Serializable;

/**
 * Exception thrown when the client tries to get
 * result from a job under processing
 * @author Nam Dang
 *
 */
public class JobNotFinishedException extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -859695998196863376L;

}
