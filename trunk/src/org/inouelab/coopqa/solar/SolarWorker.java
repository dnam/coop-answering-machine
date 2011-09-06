package org.inouelab.coopqa.solar;

import java.util.*;
import java.util.concurrent.Callable;

import org.inouelab.coopqa.base.AnswerMap;
import org.inouelab.coopqa.base.Query;
import org.inouelab.coopqa.base.QuerySet;

/**
 * This class implements {@link java.util.concurrent.Callable} interface
 * and returns {@link AnswerMap} for its {@link #call()} method.
 * <br />
 * It acts as a worker thread running in parallel with a generator class (which
 * generates queries.
 * 
 * @author Nam Dang
 * @see org.inouelab.coopqa.MultithreadGenOp
 */
public class SolarWorker implements Callable<AnswerMap> {
	private static final int DEFAULT_CYCLE = 20;
	private Queue<Query> queue;
	private volatile boolean finished;
	private int cycleSize;
	private SolarConnector connector;	

	/**
	 * Constructs a worker with the cycle size of 20
	 * @param connector corresponding {@link SolarConnector} object
	 */
	public SolarWorker(SolarConnector connector) {
		this(connector, DEFAULT_CYCLE);
	}
	
	/**
	 * Constructs a worker with the given value of cycle size
	 * @param connector the corresponding {@link SolarConnector}
	 * @param cycleSize the cycle size
	 */
	public SolarWorker(SolarConnector connector, int cycleSize) {
		super();
		this.connector = connector;
		this.queue = new LinkedList<Query>();
		this.finished = false;
		this.cycleSize = cycleSize;
	}

	/**
	 * Add a new {@link QuerySet} to the queue of this worker.
	 * @param qSet the set to add
	 */
	public synchronized void queue(QuerySet qSet) {
		queue.addAll(qSet);
	}
	
	/**
	 * Pop out a {@link Query} element
	 * @return the removed element
	 */
	public synchronized Query dequeue() {
		return queue.remove();
	}
	
	/**
	 * Set the <code>finished</code> attribute to <i>true</i><br />
	 * This worker will stop when it see this value set to <i>true</i>
	 */
	public synchronized void setFinished() {
		finished = true;
	}
	
	public synchronized boolean isFinished() {
		return finished;
	}

	@Override
	public AnswerMap call() throws Exception {
		AnswerMap ansMap = new AnswerMap(connector.getEnv());
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
				else
					throw new Exception("Unknown exception occurred in SolarWorker.call()");
			}
			
			// If we're done
			if (queue.isEmpty() && isFinished())
				break;
		}
		
		return ansMap;
	}
}
