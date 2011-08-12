package org.inouelab.coopqa.solar;

import java.util.*;
import java.util.concurrent.Callable;

import org.inouelab.coopqa.base.AnswerMap;
import org.inouelab.coopqa.base.Query;
import org.inouelab.coopqa.base.QuerySet;

public class SolarWorker implements Callable<AnswerMap> {
	private static final int DEFAULT_CYCLE = 20;
	private Queue<Query> queue;
	private volatile boolean finished;
	private int cycleSize;
	private SolarConnector connector;	

	public SolarWorker(SolarConnector connector) {
		this(connector, DEFAULT_CYCLE);
	}
	
	public SolarWorker(SolarConnector connector, int cycle) {
		super();
		this.connector = connector;
		this.queue = new LinkedList<Query>();
		this.finished = false;
		this.cycleSize = cycle;
	}

	public synchronized void queue(QuerySet qSet) {
		queue.addAll(qSet);
	}
	
	public synchronized Query dequeue() {
		return queue.remove();
	}
	
	public synchronized void setFinished() {
		finished = true;
	}
	
	public synchronized boolean isFinished() {
		return finished;
	}

	@Override
	public AnswerMap call() throws Exception {
		AnswerMap ansMap = new AnswerMap(connector.getJob());
		while(true) {
			int qSize = queue.size();
			int wqSize = (cycleSize < qSize)? (qSize/cycleSize)*cycleSize : qSize;
			Queue<Query> workingQueue = new LinkedList<Query>();
			for (int i = 0; i < wqSize; i++)
				workingQueue.offer(this.dequeue());
			
			// Now solve the queue
			int cycles = (wqSize/cycleSize == 0)? 1 : wqSize/cycleSize;
			for (int i = 0; i < cycles; i++) {
				QuerySet qSet = new QuerySet();
				wqSize = workingQueue.size();
				
				int qSetSize = (cycleSize < wqSize)? cycleSize : wqSize;
				for (int j = 0; j < qSetSize; j++) {
					qSet.add(workingQueue.remove());
				}
				
				// Run solar
				AnswerMap thisAns = connector.run(qSet);
				if (thisAns != null)
					ansMap.putAll(thisAns);
			}
			
			// If we're done
			if (queue.isEmpty() && isFinished())
				break;
		}
		return ansMap;
	}
}
