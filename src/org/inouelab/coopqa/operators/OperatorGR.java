package org.inouelab.coopqa.operators;

import java.util.*;

import org.inouelab.coopqa.Env;
import org.inouelab.coopqa.base.*;
import org.inouelab.coopqa.operators.comgen.MultiCombinationGenerator;

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
			QuerySet set = doGR(query, itRule.next());
			if (set != null)
				retSet.addAll(set);
		}
		
		return retSet;
	}
	
	QuerySet doGR(Query q, Rule r) {
		// Extract two vectors of Query and Rule
		List<Literal> qVector = new Vector<Literal>();
		Iterator<Literal> it = q.iterator();
		while (it.hasNext())
			qVector.add(it.next());
		Collections.sort(qVector);
		
		List<Literal> rVector = r.extractLeft();
		Collections.sort(rVector);
		
		// Create segments of Rule
		Vector<Vector<Literal>> rSegments = new Vector<Vector<Literal>>();
		int begin = 0, end = 0;
		Vector<Literal> segment = new Vector<Literal>();
		while(end < rVector.size()) {
			if (rVector.get(begin).compareTo(rVector.get(end)) != 0) {
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
				int comp = qVector.get(mid).compareTo(lit);
				if (comp == 0)
					break;
				else if (comp > 1)
					high = mid - 1;
				else
					low = mid + 1;
			}
			
			// Mismatch
			if (qVector.get(mid).compareTo(lit) != 0)
				return null;
			
			int idx = mid;
			while(idx >= 0 && qVector.get(idx).compareTo(lit) == 0)
				idx--;
			idx++;
			
			while(idx < qVector.size() && qVector.get(idx).compareTo(lit) == 0) {
				segment.add(qVector.get(idx));
				idx++;
			}
			
			qSegments.add(segment);			
		}
		
		
		// mismatching segment
		if (rSegments.size() != qSegments.size())
			return null;
		
		int[] rSegs = new int[rSegments.size()];
		for (int i = 0; i < rSegs.length; i++)
			rSegs[i] = rSegments.get(i).size();
		
		//Set of retrurned query
		QuerySet setQ = new QuerySet();
		
		// Generate a combination
		MultiCombinationGenerator<Literal> comGen = 
			new MultiCombinationGenerator<Literal>(qSegments, rSegs);
		
		// Produce segments of query
		while (comGen.hasNext()) {
			List<Literal> lVector = comGen.next();
			Query subQ = new Query(lVector, env);
			if (subQ.subsumed(rVector)) {
				Query newQuery = q.replaceLiterals(lVector, r.getFirstRight());
				if (!globalSet.contains(newQuery)) {
					setQ.add(newQuery);
					globalSet.add(newQuery);
				}
				else if (!setQ.contains(q)) { // do not add twice
					q.setSkipped(true);
					setQ.add(q);
				}
			}
		}
		
		return setQ;		
	}
	
	@Override
	public int getType() {
		return GR_t;
	}
}
