package org.inouelab.coopqa.web.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

public class WebAnswerMap extends HashMap<Integer, Vector<Vector<String>>> implements Serializable{
	private static final long serialVersionUID = -4635875951596870651L;
	
	public WebAnswerMap() {
		super();
	}
	
}
