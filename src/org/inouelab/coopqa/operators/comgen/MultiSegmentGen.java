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


/**
 * Multi-segment generator for GR
 * @author Nam Dang
 */
public class MultiSegmentGen
	implements Iterator<List<Literal>>, Iterable<List<Literal>>{
	private ArrayList<SegmentGen>		seggenList;
	private List<Literal> 				nextResult;
	private ArrayList<List<Literal>> 	lastResults;
	
	private Map<Integer, Integer> theta;
	private Map<Integer, Integer> lastTheta;
	
	/**
	 * @param querySegs list of segments of the query
	 * @param ruleSegs list of segments of the rule
	 */
	public MultiSegmentGen (List<List<Literal>> querySegs, List<List<Literal>> ruleSegs) {
		if (querySegs.size() != ruleSegs.size())
			throw new IllegalArgumentException("Query and Rule must be of the same segments");
	
		seggenList = new ArrayList<SegmentGen>();
		lastTheta = null;
	
		// Generate a list of combination generator
		// and generate the first combination
		this.nextResult = new ArrayList<Literal>();
		this.lastResults = new ArrayList<List<Literal>>();
		
		theta = new HashMap<Integer, Integer>();
		for (int i = 0; i < querySegs.size(); i++) {
			SegmentGen comb = new SegmentGen(querySegs.get(i), ruleSegs.get(i), theta);
			seggenList.add(comb);
			lastResults.add(null);
		}
		
		setFirst();
	}
	
	/**
	 * Set the first result for the iterator
	 */
	private void setFirst() {
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

		// Copy theta
		lastTheta = new HashMap<Integer, Integer>(theta);
		
		// Generate next result
		setNext();		
		
		return toReturn;
	}
	
	/**
	 * Returns the last theta of the segmentation generation
	 * after {@link #next()} is called
	 * @return the theta in form of a map<br/>
	 * 			<code>null</code> if no last theta available
	 */
	public Map<Integer, Integer> getLastTheta() {
		return lastTheta;
	}
	
	/**
	 * Sets the next result of the operator
	 */
	private void setNext() {
		int i = seggenList.size() - 1;
		while(i >= 0 && i < seggenList.size()) {
			final SegmentGen thisGen = seggenList.get(i);
			
			if (!thisGen.hasNext()) { // out of elements
				thisGen.reset();
				i--; // try to fall back
				continue;
			}
			
			// There is a next element
			final List<Literal> ret = thisGen.next();
			lastResults.set(i, ret);
			
			// Move forward
			i++;
		}
	
		if (i < 0) {
			nextResult = null;
			return;
		}
				
		nextResult = new ArrayList<Literal>();
		for (int j = 0; j < lastResults.size(); j++) {
			nextResult.addAll(lastResults.get(j));
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
