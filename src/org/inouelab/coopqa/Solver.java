package org.inouelab.coopqa;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.inouelab.coopqa.base.AnswerMap;
import org.inouelab.coopqa.base.Query;
import org.inouelab.coopqa.base.QuerySet;
import org.inouelab.coopqa.base.Result;
import org.inouelab.coopqa.operators.Operator;
import org.inouelab.coopqa.solar.SolarWorker;

public class Solver {
	/**
	 * @param env The given environment
	 * @return the result object
	 * @throws Exception if any error occurs
	 */
	public static Result run(Env env) throws Exception {
		Result result = new Result();
		Query initQuery = Query.parse(env.getQueryFile(), env);
		QuerySet root = new QuerySet();
		
		root.add(initQuery);
		
		result.setRoot(root);

		// The thread to handle solar connection
		SolarWorker worker = env.worker();
		FutureTask<AnswerMap> future = new FutureTask<AnswerMap>(worker);
		ExecutorService executor = Executors.newFixedThreadPool(1);
		executor.execute(future);

		// A queue of QuerySet to process
		Queue<QuerySet> workingQueue = new LinkedList<QuerySet>();
		workingQueue.offer(root); // add the root

		// Runs until the queue is empty
		int limit = env.getLimitval();
		Operator OP = env.op();
		while (!workingQueue.isEmpty() && limit > 0) {
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
					limit -= ret.size();
				}
			}
			if (limit <= 0)
				break;

			if (doDC) {
				ret = OP.DC.run(nextSet);
				if (ret != null) {
					workingQueue.add(ret);
					worker.queue(ret);
					limit -= ret.size();
				}
			}
			if (limit <= 0)
				break;

			if (doGR) {
				ret = OP.GR.run(nextSet);
				if (ret != null) {
					workingQueue.add(ret);
					worker.queue(ret);
					limit -= ret.size();
				}
			}
			if (limit <= 0)
				break;
		}

		worker.setFinished(); // Notify the worker that we're done
		AnswerMap ansMap = future.get();
		
		executor.shutdown();
		
		result.setAnswerMap(ansMap);
		result.setSolarTime(ansMap.getTime());

		return result;
	}
}
