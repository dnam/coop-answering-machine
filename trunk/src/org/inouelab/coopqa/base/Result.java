package org.inouelab.coopqa.base;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.inouelab.coopqa.solar.SolarConnector;
import org.inouelab.coopqa.web.shared.WebResult;

/**
 * A class representing the result of the execution
 * It provides information of:
 * - The root of the {@link QuerySet}
 * - The answers in the form of {@link AnswerMap}
 * - The execution time (in seconds) of SOLAR {@link SolarConnector}
 * @author Nam Dang
 *
 */
public class Result {
	private QuerySet root;	
	private AnswerMap answerMap;
	private long solarTime;
	
	/**
	 * Constructor. Creates an empty result
	 */
	public Result() {
		
	}
	
	/**
	 * @return the execution time spent in SOLAR
	 * @see SolarConnector
	 * @see AnswerMap#getTime()
	 */
	public double getSolarTime() {
		return ((double)solarTime)/1000000000;
	}

	/**
	 * @param solarTime set the solar time spent to
	 * 					get the result
	 * @see SolarConnector
	 * @see AnswerMap#getTime()
	 */
	public void setSolarTime(long solarTime) {
		this.solarTime = solarTime;
	}
	
	public long getNanoTime() {
		return solarTime;
	}

	/**
	 * @return the root of the query set tree
	 * @see QuerySet
	 */
	public QuerySet getRoot() {
		return root;
	}

	/**
	 * Sets the root of the tree of query sets
	 * @param root the new root
	 */
	public void setRoot(QuerySet root) {
		this.root = root;
	}

	/**
	 * @return the answer of the execution
	 */
	public AnswerMap getAnswerMap() {
		return answerMap;
	}

	/**
	 * @param answerMap the new answer
	 */
	public void setAnswerMap(AnswerMap answerMap) {
		this.answerMap = answerMap;
		
		// Semantic filtering
		root.filter(answerMap);
	}
	
	/**
	 * @return this object converted into {@link WebResult}
	 */
	public WebResult webConvert() {
		WebResult webResult = new WebResult();
		
		webResult.setAnswerMap(answerMap.webConvert());
		webResult.setRoot(root.webConvert());
		webResult.setSolarTime(solarTime);
		
		return webResult;
	}
	
	/**
	 * Prints out the query set.
	 * The query set must be a child of root
	 * @param querySet the QuerySet to print out
	 */
	public String printQuerySet(QuerySet querySet) {
		StringBuilder str = new StringBuilder();	
		
		Iterator<Query> it = querySet.iterator();
		str.append("Query Set: " + querySet.getOpStr() + ": \n");
		str.append("Depth: " + querySet.getDepth() + "\n\n");
		while(it.hasNext()) {
			Query query = it.next();
			
			str.append(query + "\n");
			str.append("Answer(s):\n" + query.getAnswerString(answerMap) + "\n");
			
			if (it.hasNext())
				str.append("\n");
		}
		str.append("\n");
		
		return str.toString();
	}
	
	/**
	 * @return the <code>String</code> representing all query sets under
	 * 			this root
	 * @see #getRoot()
	 */
	public String printAll() {
		String str = "";
		Queue<QuerySet> setQueue = new LinkedList<QuerySet>();
		setQueue.add(root);
		
		while (!setQueue.isEmpty()) {
			QuerySet curSet = setQueue.remove();
			str += printQuerySet(curSet);
			
			// Add children to the queue
			Iterator<QuerySet> it = curSet.getChildIterator();
			while(it.hasNext())
				setQueue.add(it.next());
		}
		
		return str;
	}
}
