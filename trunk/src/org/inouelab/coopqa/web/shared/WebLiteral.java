package org.inouelab.coopqa.web.shared;

import java.io.Serializable;

public class WebLiteral implements Serializable {
	private static final long serialVersionUID = 87L;
	private String pred;
	private boolean neg;
	private String[] params;
	
	public WebLiteral() {
		this.params = null;
	}

	// constructors
	public WebLiteral(String[] params) {
		this.params = new String[params.length];
		for (int i = 0; i < params.length; i++)
			this.params[i] = params[i];
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
	@Override
	public String toString() {
		String str = new String();
		str += ((neg)? "-" : "" );
		str += (pred + "(");
		for (int i = 0; i < params.length; i++) {
			str += (params[i]);
			if (i + 1 < params.length)
				str += (",");
		}
		str += (")");

		return str.toString();
	}
	

}
