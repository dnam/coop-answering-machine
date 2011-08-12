package org.inouelab.coopqa.web.shared;

import java.io.Serializable;
import java.util.Vector;

public class WebLiteral implements Serializable {
	private static final long serialVersionUID = 87L;
	private String pred;
	private boolean neg;
	private Vector<String> params;

	// constructors
	public WebLiteral() {
		params = new Vector<String>();
	}

	/**
	 * Sets the id of the literal's predicate
	 * @param id the id of predicate
	 */
	public void setPred(String pred) {
		this.pred = pred;
	}

	public void setNegative(boolean neg) {
		this.neg = neg;
	}

	public void add(String param) {
		params.add(param);
	}
	
	@Override
	public String toString() {
		String str = new String();
		str += ((neg)? "-" : "" );
		str += (pred + "(");
		for (int i = 0; i < params.size(); i++) {
			str += (params.get(i));
			if (i + 1 < params.size())
				str += (",");
		}
		str += (")");

		return str.toString();
	}
	

}
