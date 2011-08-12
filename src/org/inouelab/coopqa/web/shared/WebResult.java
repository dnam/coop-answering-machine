package org.inouelab.coopqa.web.shared;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class WebResult implements Serializable {
	private static final long serialVersionUID = -4228568422318343615L;
	
	private WebQuerySet root;	
	private WebAnswerMap answerMap;
	private double solarTime;
	
	public WebResult() {
		solarTime = 0;
	}
	
	public WebQuerySet getRoot() {
		return root;
	}
	public void setRoot(WebQuerySet root) {
		this.root = root;
	}
	public WebAnswerMap getAnswerMap() {
		return answerMap;
	}
	public void setAnswerMap(WebAnswerMap answerMap) {
		this.answerMap = answerMap;
	}
	public double getSolarTime() {
		return solarTime;
	}
	public void setSolarTime(double solarTime) {
		this.solarTime = solarTime;
	}
	
	/**
	 * Prints out the query set.
	 * The query set must be a child of root
	 * @param querySet
	 */
	public String printQuerySet(WebQuerySet querySet) {
		String str = "";
		for (int i = 0 ; i < querySet.size(); i++) {
			WebQuery q = querySet.get(i);
			str += ("Query: " + q + "\n");
			str += ("Answer(s):\n" + q.getAnsString(answerMap));
			
			if (i + 1 < querySet.size())
				str += "\n\n";
		}
		
		return str;
	}
	
	public String printAll() {
		String str = "";
		Queue<WebQuerySet> setQueue = new LinkedList<WebQuerySet>();
		setQueue.add(root);
		
		while (!setQueue.isEmpty()) {
			WebQuerySet curSet = setQueue.remove();
			str += printQuerySet(curSet);
			
			// Add children to the queue
			Iterator<WebQuerySet> it = curSet.getChildIterator();
			while(it.hasNext())
				setQueue.add(it.next());
		}
		
		return str;
	}
}
