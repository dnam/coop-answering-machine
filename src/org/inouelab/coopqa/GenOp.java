
package org.inouelab.coopqa;

import java.util.LinkedList;
import java.util.Queue;

import org.inouelab.coopqa.base.AnswerMap;
import org.inouelab.coopqa.base.Query;
import org.inouelab.coopqa.base.QuerySet;
import org.inouelab.coopqa.base.Result;
import org.inouelab.coopqa.operators.Operator;
import org.inouelab.coopqa.solar.SolarConnector;

/**
 * This is a class that provides a result based on given
 * <code>Env</code> (environment) object.
 * This is a single-threaded version of {@link MultithreadGenOp}
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
		Query initQuery = env.query();
		QuerySet root = new QuerySet();
		
		root.add(initQuery);
		
		result.setRoot(root);

		// The thread to handle solar connection
		SolarConnector con = env.con();
		
		// Answer
		AnswerMap ansMap = new AnswerMap(env);

		// A queue of QuerySet to process
		Queue<QuerySet> workingQueue = new LinkedList<QuerySet>();
		workingQueue.offer(root); // add the root

		// Get the limits
		int queryLimit = env.getLimitval();
		int depthLimit = env.getDepth();
		
		// Solve the root first
		ansMap.putAll(con.run(root));
	
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
				if (ret != null && !ret.isEmpty()) {
					workingQueue.add(ret);
					ansMap.putAll(con.run(ret));					
					queryLimit -= ret.size();
				}
			}
			if (queryLimit <= 0)
				break;

			if (doDC) {
				ret = OP.DC.run(nextSet);
				if (ret != null && !ret.isEmpty()) {
					workingQueue.add(ret);
					ansMap.putAll(con.run(ret));
					queryLimit -= ret.size();
				}
			}
			if (queryLimit <= 0)
				break;

			if (doGR) {
				ret = OP.GR.run(nextSet);
				if (ret != null && !ret.isEmpty()) {
					workingQueue.add(ret);
					ansMap.putAll(con.run(ret));
					queryLimit -= ret.size();
				}
			}
			if (queryLimit <= 0)
				break;
		}
		
		result.setAnswerMap(ansMap);
		result.setSolarTime(ansMap.getTime());

		return result;
	}
	
	/**
	 * A solver that only perform generalization
	 * without actually solving the queries
	 * @param env An initialized <code>Env</code> object
	 * @return a <code>Result</code> object
	 * @throws Exception if any error occurs
	 * @see Env#init()
	 */
	public static Result runNoSOLAR(Env env) throws Exception {
		Result result = new Result();
		Query initQuery = env.query();
		QuerySet root = new QuerySet();
		
		root.add(initQuery);
		
		result.setRoot(root);
		// Answer
		AnswerMap ansMap = new AnswerMap(env);

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
					queryLimit -= ret.size();
				}
			}
			if (queryLimit <= 0)
				break;

			if (doDC) {
				ret = OP.DC.run(nextSet);
				if (ret != null) {
					workingQueue.add(ret);
					queryLimit -= ret.size();
				}
			}
			if (queryLimit <= 0)
				break;

			if (doGR) {
				ret = OP.GR.run(nextSet);
				if (ret != null) {
					workingQueue.add(ret);
					queryLimit -= ret.size();
				}
			}
			if (queryLimit <= 0)
				break;
		}
		System.out.println();
		
		result.setAnswerMap(ansMap);
		result.setSolarTime(ansMap.getTime());

		return result;
	}
}
