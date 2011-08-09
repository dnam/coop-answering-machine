/**
 * Author: Maheen Bakhtyar and Nam Dang
 * Org: NII
 */
package org.nii.cqa;

import java.io.*;
import java.util.*;

import org.nii.cqa.base.AnswerMap;
import org.nii.cqa.base.KnowledgeBase;
import org.nii.cqa.base.Query;
import org.nii.cqa.base.QuerySet;
import org.nii.cqa.operators.Operator;
import org.nii.cqa.solar.SolarWorker;
import org.nii.cqa.web.shared.WebAnswerMap;
import org.nii.cqa.web.shared.WebQuerySet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class CQA {
	private static QuerySet root = new QuerySet();

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// The root of the tree
		Query q = Query.parse("../CQA/lib/gen_query.txt");
		root.add(q);

		// Initializes the static Knowledgebase
		KnowledgeBase.init("../CQA/lib/gen_kb.txt");

		// A queue of QuerySet to process
		Queue<QuerySet> workingQueue = new LinkedList<QuerySet>();
		workingQueue.offer(root); // add the root

		// The thread to handle solar connection
		SolarWorker worker = new SolarWorker();
		FutureTask<AnswerMap> future = new FutureTask<AnswerMap>(worker);
		ExecutorService executor = Executors.newFixedThreadPool(1);
		executor.execute(future);

		// Runs until the queue is empty
		int cnt = 0; // TODO: configurable
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
				ret = Operator.AI.run(nextSet);
				if (ret != null) {
					workingQueue.add(ret);
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
				}
			}

			if (doGR) {
				ret = Operator.GR.run(nextSet);
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

		System.out.println("\n");
		System.out.println("Answer set: " + ans.webConvert());
		System.out.println("Done");
		executor.shutdown();
		
		WebQuerySet newSet = new WebQuerySet();
		try {
			FileOutputStream fos = new FileOutputStream("test.tmp");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			WebQuerySet toWriteObj = root.webConvert();
			toWriteObj.setAnsMap(ans.webConvert());
			oos.writeObject(toWriteObj);

			FileInputStream fin = new FileInputStream("test.tmp");
			ObjectInputStream ois = new ObjectInputStream(fin);
			newSet = (WebQuerySet) ois.readObject();
		} catch (java.io.FileNotFoundException e) {
			System.out.println(e.toString());
		} catch (java.io.IOException e) {
			System.out.println(e.toString());
		}

		
		System.out.println(newSet);
		
		
	}

}
