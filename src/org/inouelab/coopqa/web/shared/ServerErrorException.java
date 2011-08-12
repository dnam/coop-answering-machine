package org.inouelab.coopqa.web.shared;

import java.io.Serializable;

public class ServerErrorException extends Exception implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5264579861373070829L;
	
	public ServerErrorException(){
		super();
	}
	
	public ServerErrorException(String msg) {
		super(msg);
	}
}
