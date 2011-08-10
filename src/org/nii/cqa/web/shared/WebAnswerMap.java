package org.nii.cqa.web.shared;

import java.util.HashMap;
import java.util.Vector;

public class WebAnswerMap extends HashMap<Integer, Vector<Vector<String>>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4635875951596870651L;
	private double time;
	
	public WebAnswerMap() {
		super();
		this.time = 0;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}
	
}
