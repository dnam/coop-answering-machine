
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

/**
 * This is a class that provides a result based on given
 * <code>Env</code> (environment) object.
 * @author Nam Dang
 * @see Env
 */
public class GenOp {
	/**
	 * @param env An initialized <code>Env</code> object
	 * @return a <code>Result</code> object
	 * @throws Exception if any error occurs
	 * @see Env#init()
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

		// Get the limits
		int queryLimit = env.getLimitval();
		int depthLimit = env.getDepth();
	
		Operator OP = env.op();
		while (!workingQueue.isEmpty() && queryLimit > 0) {
			QuerySet nextSet = workingQueue.poll();
			if (nextSet.getDepth() + 1 == depthLimit)
				continue;

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
					queryLimit -= ret.size();
				}
			}
			if (queryLimit <= 0)
				break;

			if (doDC) {
				ret = OP.DC.run(nextSet);
				if (ret != null) {
					workingQueue.add(ret);
					worker.queue(ret);
					queryLimit -= ret.size();
				}
			}
			if (queryLimit <= 0)
				break;

			if (doGR) {
				ret = OP.GR.run(nextSet);
				if (ret != null) {
					workingQueue.add(ret);
					worker.queue(ret);
					queryLimit -= ret.size();
				}
			}
			if (queryLimit <= 0)
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
