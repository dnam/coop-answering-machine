/**
 * Author: Maheen Bakhtyar and Nam Dang
 * Org: NII
 */
package org.nii.cqa;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import org.nii.cqa.base.KnowledgeBase;
import org.nii.cqa.base.Query;
import org.nii.cqa.base.QuerySet;
import org.nii.cqa.operators.Operator;
import org.nii.cqa.parser.QueryParser;

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
		KnowledgeBase.initKB("../CQA/lib/gen_kb.txt");
		
		// A queue of QuerySet to process
		Queue<QuerySet> workingQueue = new LinkedList<QuerySet>();
		workingQueue.offer(root); // add the root
		
		// Runs until the queue is empty
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
					
				}
			}
			
			if (doDC) {
				ret = Operator.DC.run(nextSet);
				if (ret != null) {
					workingQueue.add(ret);
					System.out.println(ret);
				}
			}
			
			if (doGR) {
				ret = Operator.GR.run(nextSet);
				if (ret != null) {
					workingQueue.add(ret);
					System.out.println(ret);
				}
			}
			
			
		}
	}
}
