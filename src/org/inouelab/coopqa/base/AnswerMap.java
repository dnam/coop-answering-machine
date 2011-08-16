package org.inouelab.coopqa.base;

import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Vector;

import org.inouelab.coopqa.Env;
import org.inouelab.coopqa.web.shared.WebAnswerMap;

/**
 * An object storing sets of answer for queries.
 * The map stores the answers in the following manner
 * - A query id (unique for each {@link Query})
 * - A list of answers
 * - Answers are just an ordered list of constants ids
 */
public class AnswerMap extends HashMap<Integer, List<List<Integer>>>{
	private static final long serialVersionUID = -5333557672963251276L;
	
	private Env env;		// current environment
	private double time; 	// time to get this answer list
	
	public AnswerMap(Env env) {
		super();
		
		this.env = env;
		this.time = 0;
	}
	
	/**
	 * Set the time spent on SOLAR to obtain this answer
	 * @param time the execution time as <code>double</code
	 */
	public void setTime(double time) {
		this.time = time;
	}
	
	public double getTime() {
		return time;
	}
	
	/**
	 * Converts this object to another class used in the website
	 * @see WebAnswerMap
	 */
	public WebAnswerMap webConvert() {
		WebAnswerMap webMap = new WebAnswerMap();
		
		Iterator<Integer> it = this.keySet().iterator();
		while (it.hasNext()) {
			int id = it.next();
			List<List<Integer>> ansList = this.get(id);
			
			Vector<Vector<String>> webAnsList = new Vector<Vector<String>>();
			Iterator<List<Integer>> ansIt = ansList.iterator();
			while (ansIt.hasNext()) {
				List<Integer> ans = ansIt.next();
				Vector<String> webAns = new Vector<String>();
				
				Iterator<Integer> localIt = ans.iterator();
				while (localIt.hasNext()) {
					webAns.add(env.symTab().getSym(localIt.next()));
				}
				
				webAnsList.add(webAns);
			}
			
			webMap.put(id, webAnsList);
		}
				
		return webMap;
	}
	
	/**
	 * Merges the other <code>AnswerMap</code> object into
	 * this object
	 * @param other the other <code>AnswerMap</code> to merge
	 */
	public void putAll(AnswerMap other) {
		super.putAll(other);		
		this.time += other.time;
	}

}
