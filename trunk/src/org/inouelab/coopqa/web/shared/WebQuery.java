package org.inouelab.coopqa.web.shared;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

/**
 * Query class for the web
 * @author Nam Dang
 * @see org.inouelab.coopqa.base.Query#webConvert()
 *
 */
public class WebQuery implements Serializable {
	private static final long serialVersionUID = 22L;
	private int id; // ID of a query, based on a global counter
	private Vector<WebLiteral> litVector;
	private Vector<String> listVar; // list of sorted variables (for mapping back)
	private boolean skipped;
	
	public WebQuery() {
		id = 0;
		litVector = new Vector<WebLiteral>();
		listVar = new Vector<String>();
		skipped = false;
	}
	
	public WebQuery(int id) {
		this();
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void addLit(WebLiteral lit) {
		litVector.add(lit);
	}
	
	public void addVar(String var) {
		listVar.add(var);
	}
	
	public void setSkipped(boolean skipped) {
		this.skipped = skipped;
	}
	
	/**
	 * Extracts the answers string from ansMap
	 * @param ansMap the answer map
	 * @return the string of answers
	 */
	public String getAnsString(WebAnswerMap ansMap) {
		if (skipped) {
			return "[skipped]\n";			
		}
		Vector<Vector<String>> ret = ansMap.get(id);
		
		if (ret == null)
			return "[no answer]\n";
		
		String retStr = "";
		for (int i = 0; i < ret.size(); i++) {
			Vector<String> ansVector = ret.get(i);
			
			retStr += "[";
			
			// For error reporting
			if (ansVector.size() != listVar.size())
				return "[FATAL ERROR: ansVector's size is different from listVar in WebQuery]\n";
			
			for (int j = 0; j < ansVector.size(); j++) {
				retStr += (listVar.get(j) + "->" + ansVector.get(j));
				if (j + 1 < ansVector.size())
					retStr += ", ";
			}
			
			retStr += "]";
			
			if (i + 1 < ret.size())
				retStr += "\n";
		}
		
		return retStr;
	}
	
	@Override
	public String toString() {
		Iterator<WebLiteral> it = litVector.iterator();
		String str = new String();
		while (it.hasNext()) {
			str += it.next();
			if (it.hasNext())
				str += " & ";
		}

		return str;
	}
	

	
}
