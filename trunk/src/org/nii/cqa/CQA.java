/**
 * Author: Maheen Bakhtyar and Nam Dang
 * Org: NII
 */
package org.nii.cqa;

import java.util.*;

import org.nii.cqa.base.KnowledgeBase;
import org.nii.cqa.base.Query;
import org.nii.cqa.base.QuerySet;
import org.nii.cqa.operators.Operator;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.print.attribute.standard.Finishings;

public class CQA {
	private static QuerySet root = new QuerySet(); 

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		//The root of the tree
		Query q = Query.parse("../CQA/lib/gen_query.txt");
		root.add(q);
		
		// Initializes the static Knowledgebase
		KnowledgeBase.init("../CQA/lib/gen_kb.txt");
		
		// A queue of QuerySet to process
		Queue<QuerySet> workingQueue = new LinkedList<QuerySet>();
		workingQueue.offer(root); // add the root
		
		// The thread to handle solar connection
		SolarWorker worker = new SolarWorker();
		Thread workerThread = new Thread(worker);
		workerThread.start();
		
		// Runs until the queue is empty
		int cnt = 0; // TODO: configurable
		while(!workingQueue.isEmpty()) {
//			System.out.println("Size: " + workingQueue.size());
			
			QuerySet nextSet = workingQueue.poll();
			
			Integer op = nextSet.getLastOp();
			QuerySet ret = null;
			
			boolean doAI = false, doDC = false, doGR = false;
			
			// Root
			if (op == null || op == Operator.GR_t) {
				doAI = true; doDC = true; doGR = true;
			}
			else if (op == Operator.DC_t) {
				doDC = true; doAI = true;
			}
			else if (op == Operator.AI_t) {
				doAI = true;
			}
			
			if (doAI) {
				ret = Operator.AI.run(nextSet);
				if (ret != null) {
					workingQueue.add(ret);
					System.out.println(ret);
					worker.queue(ret);
					cnt += ret.size();
					
				}
			}
			
			if (doDC) {
				ret = Operator.DC.run(nextSet);
				if (ret != null) {
					workingQueue.add(ret);
					worker.queue(ret);
					cnt += ret.size();
					System.out.println(ret);
				}
			}
			
			if (doGR) {
				ret = Operator.GR.run(nextSet);
				if (ret != null) {
					workingQueue.add(ret);
					worker.queue(ret);
					cnt += ret.size();
					System.out.println(ret);
				}
			}
		}
		
		worker.setFinished(); // Notify the worker that we're done
		
		workerThread.join();
		
		System.out.println("\n");
		System.out.println("Answer set: " + worker.ansMap);
		System.out.println("Done");
	}
}
