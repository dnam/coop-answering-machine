/**
 * Author: Maheen Bakhtyar and Nam Dang
 * Org: NII
 */
package org.nii.cqa;

import java.io.*;
import java.util.*;

import org.nii.cqa.base.AnswerMap;
import org.nii.cqa.base.CoopQAJob;
import org.nii.cqa.base.Query;
import org.nii.cqa.base.QuerySet;
import org.nii.cqa.operators.Operator;
import org.nii.cqa.solar.SolarWorker;
import org.nii.cqa.web.shared.WebQuerySet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class CoopQA {
	private static QuerySet root = new QuerySet();
	private static CoopQAJob job = new CoopQAJob();

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// Trying out init file
		Properties pri = new Properties();
		pri.setProperty("solarPath", "C:\\tmp\\solar2-build310.jar");
		pri.store(new FileOutputStream("test.ini"), "nothing");
		
		
		// Initializes the job
		job.init("C:\\Users\\Nam\\workspace\\CQA\\lib\\gen_kb.txt", 
				"C:\\tmp\\solar2-build310.jar", "C:\\tmp\\");
		
		// The root of the tree
		Query q = Query.parse("C:\\Users\\Nam\\workspace\\CQA\\lib\\gen_query.txt", job);
		root.add(q);
	

		// A queue of QuerySet to process
		Queue<QuerySet> workingQueue = new LinkedList<QuerySet>();
		workingQueue.offer(root); // add the root

		// The thread to handle solar connection
		SolarWorker worker = job.worker();
		FutureTask<AnswerMap> future = new FutureTask<AnswerMap>(worker);
		ExecutorService executor = Executors.newFixedThreadPool(1);
		executor.execute(future);

		// Runs until the queue is empty
		int cnt = 0; // TODO: configurable
		Operator OP = job.op();
		while (!workingQueue.isEmpty()) {
			// System.out.println("Size: " + workingQueue.size());

			QuerySet nextSet = workingQueue.poll();

			Integer op = nextSet.getLastOp();
			QuerySet ret = null;

			boolean doAI = false, doDC = false, doGR = false;

			// Root
			if (op == null || op == Operator.GR_t) {
				doAI = true;
				doDC = true;
				doGR = true;
			} else if (op == Operator.DC_t) {
				doDC = true;
				doAI = true;
			} else if (op == Operator.AI_t) {
				doAI = true;
			}

			if (doAI) {
				ret = OP.AI.run(nextSet);
				if (ret != null) {
					workingQueue.add(ret);
					worker.queue(ret);
					cnt += ret.size();

				}
			}

			if (doDC) {
				ret = OP.DC.run(nextSet);
				if (ret != null) {
					workingQueue.add(ret);
					worker.queue(ret);
					cnt += ret.size();
				}
			}

			if (doGR) {
				ret = OP.GR.run(nextSet);
				if (ret != null) {
					workingQueue.add(ret);
					worker.queue(ret);
					cnt += ret.size();
				}
			}
		}

		worker.setFinished(); // Notify the worker that we're done

		System.out.println(root);
		
		AnswerMap ans = future.get();
		executor.shutdown();
		
		System.out.println("\n");
		System.out.println("Answer set: " + ans.webConvert());
		System.out.println("SOLAR time: " + ans.getTime() + "s");
		
//		
//		WebQuerySet newSet = new WebQuerySet();
//		try {
//			FileOutputStream fos = new FileOutputStream("test.tmp");
//			ObjectOutputStream oos = new ObjectOutputStream(fos);
//			WebQuerySet toWriteObj = root.webConvert();
//			toWriteObj.setAnsMap(ans.webConvert());
//			oos.writeObject(toWriteObj);
//
//			FileInputStream fin = new FileInputStream("test.tmp");
//			ObjectInputStream ois = new ObjectInputStream(fin);
//			newSet = (WebQuerySet) ois.readObject();
//		} catch (java.io.FileNotFoundException e) {
//			System.out.println(e.toString());
//		} catch (java.io.IOException e) {
//			System.out.println(e.toString());
//		}
//
//		
//		System.out.println(newSet);
		
		
	}

}
