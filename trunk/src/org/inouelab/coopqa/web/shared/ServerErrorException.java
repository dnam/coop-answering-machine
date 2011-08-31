package org.inouelab.coopqa.web.shared;

import java.io.Serializable;

/**
 * Thrown when some error occurred on the server side
 * @author Nam Dang
 *
 */
public class ServerErrorException extends Exception implements Serializable{

	private static final long serialVersionUID = -5264579861373070829L;
	
	/**
	 * Constructor
	 */
	public ServerErrorException(){
		super();
	}
	
	/** 
	 * @param msg the message to throw
	 */
	public ServerErrorException(String msg) {
		super(msg);
	}
}
