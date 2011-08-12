package org.inouelab.coopqa.base;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.inouelab.coopqa.web.shared.WebResult;

public class Result {
	private QuerySet root;	
	private AnswerMap answerMap;
	private double solarTime;
		
	public double getSolarTime() {
		return solarTime;
	}

	public void setSolarTime(double solarTime) {
		this.solarTime = solarTime;
	}

	public QuerySet getRoot() {
		return root;
	}

	public void setRoot(QuerySet querySet) {
		this.root = querySet;
	}

	public AnswerMap getAnswerMap() {
		return answerMap;
	}

	public void setAnswerMap(AnswerMap answerMap) {
		this.answerMap = answerMap;
	}
	
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
	 * @param querySet
	 */
	public String printQuerySet(QuerySet querySet) {
		StringBuilder str = new StringBuilder();
		
		
		Iterator<Query> it = querySet.iterator();
		str.append("Query Set: " + querySet.getOpStr() + ": \n");
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
