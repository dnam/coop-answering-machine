package org.inouelab.coopqa.operators.comgen;

import org.inouelab.coopqa.Env;
import org.inouelab.coopqa.Options;
import org.inouelab.coopqa.base.Literal;
import org.inouelab.coopqa.base.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Vector;


public class MultiSegmentGen
	implements Iterator<List<Literal>>, Iterable<List<Literal>>{
	private ArrayList<SegmentGen>		seggenList;
	private List<Literal> 				nextResult;
	private ArrayList<List<Literal>> 	lastResults;
	private Env env;
	
	public MultiSegmentGen (List<List<Literal>> querySegs, List<List<Literal>> ruleSegs, Env env) {
		if (querySegs.size() != ruleSegs.size())
			throw new IllegalArgumentException("Query and Rule must be of the same segments");
	
		seggenList = new ArrayList<SegmentGen>();
	
		// Generate a list of combination generator
		// and generate the first combination
		this.nextResult = new ArrayList<Literal>();
		this.lastResults = new ArrayList<List<Literal>>();
		
		Map<Integer, Integer> theta = new HashMap<Integer, Integer>();
		SegmentGen.setDebug(false);
		for (int i = 0; i < querySegs.size(); i++) {
			SegmentGen comb = new SegmentGen(querySegs.get(i), ruleSegs.get(i), theta, env);
			seggenList.add(comb);
			lastResults.add(null);
		}
		
		setFirst();
	}
	
	public void setFirst() {
		int i = 0;
		while (i < seggenList.size()) {
			if (i < 0) {
				nextResult = null;
				return;
			}
			
			SegmentGen gen = seggenList.get(i);
			if (!gen.hasNext()) {
				i--;
				continue;
			}
			
			List<Literal> ret = gen.next();
			lastResults.set(i, ret);
			i++;
		}
		
		nextResult = new ArrayList<Literal>();
		for (int j = 0; j < lastResults.size(); j++) {
			nextResult.addAll(lastResults.get(j));
		}
		
		setNext();
	}

	@Override
	public Iterator<List<Literal>> iterator() {
		 return this;
	}

	@Override
	public boolean hasNext() {
		return (nextResult != null);
	}

	@Override
	public List<Literal> next() {
		if(!hasNext()) {
            throw new NoSuchElementException();
        }
		
		List<Literal> toReturn = new ArrayList<Literal>(nextResult);
		
		setNext();		
		
		return toReturn;
	}
	
	private void setNext() {
		int i = seggenList.size() - 1;
		while(i >= 0 && i < seggenList.size()) {
			final SegmentGen thisGen = seggenList.get(i);
			if (!thisGen.hasNext()) { // out of elements
				thisGen.reset();
				i--;
				continue;
			}
			
			// There is a next element
			final List<Literal> ret = thisGen.next();
			lastResults.set(i, ret);
			if (ret == null) {
				System.out.println("ERROR");
			}
			
			// Move forward
			i++;
		}
	
		if (i < 0) {
			nextResult = null;
			return;
		}
		
//		System.out.println("i = "  + i + " seggenLIst size: " + seggenList.size());
		
		nextResult = new ArrayList<Literal>();
		for (int j = 0; j < lastResults.size(); j++) {
			nextResult.addAll(lastResults.get(j));
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	
	public static void main(String args[]) throws Exception {
		Env env = new Env(); // a new environment
		Options options = new Options(env);
		
		String[] testArgs = { "-kb",
				"C:\\Users\\Nam\\workspace\\CQA\\lib\\gen_kb.txt", 
				"-q",
				"C:\\Users\\Nam\\workspace\\CQA\\lib\\gen_query.txt"};
		options.init(testArgs);
		   	
    	Query query = Query.parse("test/query.txt", env);
    	Query rule = Query.parse("test/rule.txt", env);
    	
    	Vector<Literal> rVector = rule.getLitVector();
    	Vector<Literal> qVector = query.getLitVector();
    	
    	Collections.sort(rVector);
    	Collections.sort(qVector);
    	
		// Create segments of Rule
		List<List<Literal>> rSegments = new Vector<List<Literal>>();
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
		System.out.println("# rule segments: " + rSegments.size());
		
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
			if (qVector.get(mid).compareTo(lit) != 0) {
				System.err.println("error");
				System.exit(-1);
			}
			
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
		System.out.println("# query segments: " + qSegments.size());
    	
		MultiSegmentGen multiGen = new MultiSegmentGen(qSegments, rSegments, env);
		
		System.out.println("All combinations: ");
		while(multiGen.hasNext()) {
			System.out.println(multiGen.next());
		}
	}
}
