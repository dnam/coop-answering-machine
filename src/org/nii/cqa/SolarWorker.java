package org.nii.cqa;

import java.util.*;

import org.nii.cqa.base.QuerySet;
import org.nii.cqa.base.Query;
import org.nii.cqa.solar.SolarConnector;

public class SolarWorker implements Runnable {
	private Queue<Query> queue;
	private volatile boolean finished;
	private int CYCLE = 20;
	public Map<Integer, List<List<Integer>>> ansMap;
	
	public SolarWorker() {
		super();
		this.queue = new LinkedList<Query>();
		this.finished = false;
		this.ansMap = new HashMap<Integer, List<List<Integer>>>();
	}
	
	@Override
	public void run() {
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
}
