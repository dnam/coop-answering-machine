package org.inouelab.coopqa.operators;

import java.util.*;

import org.inouelab.coopqa.Env;
import org.inouelab.coopqa.base.*;
import org.inouelab.coopqa.operators.comgen.*;

/**
 * Goal-Replacement Operator
 * @author Nam Dang
 *
 */
final class OperatorGR extends Operator {
	protected OperatorGR(Env job) {
		super(false, job);
	}
	// Suppose we already have a knowledge base

	@Override
	QuerySet perform(Query query) {		
		QuerySet retSet = new QuerySet();
		
		if (query.isSkipped())
			return retSet;

		Iterator<Rule> itRule = env.kb().iteratorSHRR();
		while(itRule.hasNext()) {
			Rule rule = itRule.next();
			// skip if the rule is too long
			if (rule.extractLeft().size() > query.size())
				continue;
			
			QuerySet set = doGR(query, rule);
			if (set != null)
				retSet.addAll(set);
		}
		
		return retSet;
	}
	
	QuerySet doGR(Query q, Rule r) {
		// Literal's alternate comparator
		Literal.AltComp altComp = new Literal.AltComp();
		// Extract two vectors of Query and Rule
		List<Literal> qVector = new Vector<Literal>();
		Iterator<Literal> it = q.iterator();
		while (it.hasNext())
			qVector.add(it.next());
		Collections.sort(qVector, altComp);
		
		List<Literal> rVector = r.extractLeft();
		Collections.sort(rVector, altComp);
		
		// Create segments of Rule
		Vector<List<Literal>> rSegments = new Vector<List<Literal>>();
		int begin = 0, end = 0;
		Vector<Literal> segment = new Vector<Literal>();
		while(end < rVector.size()) {
			if (altComp.compare(rVector.get(begin), rVector.get(end)) != 0) {
				rSegments.add(segment);
				segment = new Vector<Literal>();
				begin = end;
			}
			segment.add(rVector.get(end));
			end++;
		}
		rSegments.add(segment);
		
		// Extract matched segment of the query
		List<List<Literal>> qSegments = new Vector<List<Literal>>();
		for (int i = 0; i < rSegments.size(); i++) {
			Literal lit = rSegments.get(i).get(0);
			segment = new Vector<Literal>();
			
			int low = 0, high = qVector.size() - 1;
			int mid = (low + high) / 2;
			while (low <= high) {
				mid = (low + high)/2;
				int comp = altComp.compare(qVector.get(mid),lit);
				if (comp == 0)
					break;
				else if (comp > 1)
					high = mid - 1;
				else
					low = mid + 1;
			}
			
			// Mismatch
			if (altComp.compare(qVector.get(mid), lit) != 0)
				return null;
			
			int idx = mid;
			while(idx >= 0 && altComp.compare(qVector.get(idx), lit) == 0)
				idx--;
			idx++;
			
			while(idx < qVector.size() && altComp.compare(qVector.get(idx), lit) == 0) {
				segment.add(qVector.get(idx));
				idx++;
			}
			
			qSegments.add(segment);			
		}
		
		
		// mismatching segment
		if (rSegments.size() != qSegments.size())
			return null;
		
		//Set of retrurned query
		QuerySet setQ = new QuerySet();
	
		// Generate the matching segments from the query
		MultiSegmentGen segGen = new MultiSegmentGen(qSegments, rSegments);
		while (segGen.hasNext()) {
			List<Literal> lVector = segGen.next();
			Map<Integer, Integer> theta = segGen.getLastTheta();

			Literal rightSideRule = r.getFirstRight().substitute(theta);
			// check if the query is subsumed by the left-hand side
			Query newQuery = q.replaceLiterals(lVector, rightSideRule);
			if (!globalSet.add(newQuery))  { // set the query as "skipped" if added before
				newQuery.setSkipped(true);
			}
			
			// Add to the current set
			setQ.add(newQuery);

		}
		
		return setQ;		
	}
	
	
	
	@Override
	public int getType() {
		return GR_t;
	}
}
