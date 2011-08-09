package org.nii.cqa.solar;

import java.util.*;
import java.util.concurrent.Callable;

import org.nii.cqa.base.AnswerMap;
import org.nii.cqa.base.QuerySet;
import org.nii.cqa.base.Query;

public class SolarWorker implements Callable<AnswerMap> {
	private Queue<Query> queue;
	private volatile boolean finished;
	private int CYCLE = 20;
	
	public SolarWorker() {
		super();
		this.queue = new LinkedList<Query>();
		this.finished = false;
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
		AnswerMap ansMap = new AnswerMap();
		while(true) {
			int qSize = queue.size();
			int wqSize = (CYCLE < qSize)? (qSize/CYCLE)*CYCLE : qSize;
			Queue<Query> workingQueue = new LinkedList<Query>();
			for (int i = 0; i < wqSize; i++)
				workingQueue.offer(this.dequeue());
			
			// Now solve the queue
			int cycles = (wqSize/CYCLE == 0)? 1 : wqSize/CYCLE;
			for (int i = 0; i < cycles; i++) {
				QuerySet qSet = new QuerySet();
				wqSize = workingQueue.size();
				
				int qSetSize = (CYCLE < wqSize)? CYCLE : wqSize;
				for (int j = 0; j < qSetSize; j++) {
					qSet.add(workingQueue.remove());
				}
				
				// Run solar
				ansMap.putAll(SolarConnector.run(qSet));
			}
			
			// If we're done
			if (queue.isEmpty() && isFinished())
				break;
		}
		return ansMap;
	}
}
