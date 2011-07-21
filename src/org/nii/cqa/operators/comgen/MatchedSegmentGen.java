package org.nii.cqa.operators.comgen;

import java.io.FileReader;
import java.util.*;

import java_cup.internal_error;

import org.nii.cqa.base.*;
import org.nii.cqa.parser.QueryParser;

public class MatchedSegmentGen {
	public Vector<Vector<Literal>> querySide;
	public Vector<Vector<Literal>> ruleSide;
	private Vector<Literal> nextResult;
	private Vector<Vector<Literal>> lastResults;
	private Vector<SegmentGen> genList;
	private Vector<Map<Integer, Integer>> thetaList;
	private Map<Integer, Integer> lastTheta;

	// For testing
	public MatchedSegmentGen(Vector<Literal> qVector, Vector<Literal> rVector) {

		this.querySide = new Vector<Vector<Literal>>();
		this.ruleSide = new Vector<Vector<Literal>>();
		this.nextResult = new Vector<Literal>();
		this.lastResults = new Vector<Vector<Literal>>();
		this.genList = new Vector<SegmentGen>();
		this.thetaList = new Vector<Map<Integer, Integer>>();
		this.lastTheta = new HashMap<Integer, Integer>();

		// Build segments of the rule
		int begin = 0, end = 0;
		Vector<Literal> segment = new Vector<Literal>();
		while (end < rVector.size()) {
			if (rVector.get(begin).compareTo(rVector.get(end)) != 0) {
				ruleSide.add(segment);
				segment = new Vector<Literal>();
				begin = end;
			}
			segment.add(rVector.get(end++));
		}
		ruleSide.add(segment);

		// Extract matching segment of the query
		for (int i = 0; i < ruleSide.size(); i++) {
			Literal l = ruleSide.get(i).get(0);
			segment = new Vector<Literal>();

			int low = 0, high = qVector.size() - 1;
			int mid = (low + high) / 2;
			while (low <= high) {
				mid = (low + high) / 2;
				int comp = qVector.get(mid).compareTo(l);				
				if (comp == 0)
					break;
				else if (comp > 1)
					high = mid - 1;
				else
					low = mid + 1;
			}

			if (qVector.get(mid).compareTo(l) != 0) {
				// The two of them are not match!!
				nextResult = null;
				return;
			}

			int idx = mid;
			while (idx >= 0 && qVector.get(idx).compareTo(l) == 0)
				idx--;
			idx++;

			while (idx < qVector.size() && qVector.get(idx).compareTo(l) == 0) {
				segment.add(qVector.get(idx));
				idx++;
			}
			
			querySide.add(segment);
		}
		

		// no matching
		if (nextResult == null)
			return;

		// Generate a list of segment generator
		Map<Integer, Integer> theta = new HashMap<Integer, Integer>();
		for (int i = 0; i < ruleSide.size(); i++) {
			thetaList.add(new HashMap<Integer, Integer>(theta));

			SegmentGen sg = new SegmentGen(querySide.get(i), ruleSide.get(i),
					theta);
			genList.add(sg);
			
			if (nextResult != null && sg.hasNext()) {
				Vector<Literal> ret = sg.next();
				lastResults.add(ret);
				nextResult.addAll(ret);
				theta.putAll(sg.getTheta());
				
				if (i + 1 == ruleSide.size())
					lastTheta.putAll(theta);
				
			} else {
				nextResult = null;
				break;
			}
		}
	}
	
	/*public MatchedSegmentGen(Query q, Rule r) {
		this.querySide = new Vector<Vector<Literal>>();
		this.ruleSide = new Vector<Vector<Literal>>();
		this.nextResult = new Vector<Literal>();
		this.lastResults = new Vector<Vector<Literal>>();
		this.genList = new Vector<SegmentGen>();
		this.thetaList = new Vector<Map<Integer, Integer>>();

		// Build segments of the rule
		Vector<Literal> rVector = r.extractLeft();
		int begin = 0, end = 0;
		Vector<Literal> segment = new Vector<Literal>();
		while (end < rVector.size()) {
			if (rVector.get(begin).compareTo(rVector.get(end)) != 0) {
				ruleSide.add(segment);
				segment = new Vector<Literal>();
				begin = end;
			}
			segment.add(rVector.get(end++));
		}
		ruleSide.add(segment);

		// Extract matching segment of the query
		Vector<Literal> qVector = new Vector<Literal>();
		Iterator<Literal> it = q.iterator();
		while (it.hasNext())
			qVector.add(it.next());

		for (int i = 0; i < ruleSide.size(); i++) {
			Literal l = ruleSide.get(i).get(0);
			segment = new Vector<Literal>();

			int low = 0, high = qVector.size() - 1;
			int mid = (low + high) / 2;
			while (low <= high) {
				mid = (low + high) / 2;
				int comp = qVector.get(mid).compareTo(l);
				if (comp == 0)
					break;
				else if (comp > 1)
					high = mid - 1;
				else
					low = mid + 1;
			}

			if (qVector.get(i).compareTo(l) != 0) {
				// The two of them are not match!!
				nextResult = null;
				break;
			}

			while (i >= 0 && qVector.get(i).compareTo(l) == 0)
				i--;

			while (qVector.get(i).compareTo(l) == 0)
				segment.add(qVector.get(i++));
		}

		// no matching
		if (nextResult == null)
			return;

		// Generate a list of segment generator
		Map<Integer, Integer> theta = new HashMap<Integer, Integer>();
		for (int i = 0; i < ruleSide.size(); i++) {
			thetaList.add(new HashMap<Integer, Integer>(theta));

			SegmentGen sg = new SegmentGen(querySide.get(i), ruleSide.get(i),
					null);
			if (nextResult != null && sg.hasNext()) {
				Vector<Literal> ret = sg.next();
				lastResults.add(ret);
				nextResult.addAll(ret);
				theta.putAll(sg.getTheta());
			} else {
				nextResult = null;
				break;
			}
		}
	}*/

	public boolean hasNext() {
		return (nextResult != null);
	}

	public Vector<Literal> next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		Vector<Literal> toReturn = new Vector<Literal>();
		toReturn.addAll(nextResult);
		nextResult.clear();

		int idx = ruleSide.size() - 1;
		while (idx >= 0) {
			if (!genList.get(idx).hasNext()) {
				idx--;
				continue;
			}
			
			// Move forward
			for (int i = 0; i < idx; i++)
				nextResult.addAll(lastResults.get(i));
			
			// Move the iterator
			for (int i = idx; i < ruleSide.size(); i++) {
				Vector<Literal> next = genList.get(i).next();
				if (next == null) {
					idx--;
					break;
				}
				
				lastResults.set(i, next);
				nextResult.addAll(next);

				Map<Integer, Integer> theta = genList.get(i).getTheta();
				if (i + 1 < ruleSide.size())
					genList.get(i + 1).reset(theta);
				else {
					lastTheta.clear();
					lastTheta.putAll(theta);
				}
			}
		}
		
		if (idx < 0) {
			nextResult = null;
			return toReturn;
		}
		
		return toReturn;
	}

	public static void main(String args[]) throws Exception {
		QueryParser p;
		p = new QueryParser(new FileReader("../CQA/lib/gen_query.txt"));
		Query query = (Query) p.parse().value;
		Vector<Literal> qVector = new Vector<Literal>();
		Iterator<Literal> it = query.iterator();
		while (it.hasNext())
			qVector.add(it.next());

		p = new QueryParser(new FileReader("../CQA/lib/gen_rule.txt"));
		Query rule = (Query) p.parse().value;

		Vector<Literal> rVector = new Vector<Literal>();
		it = rule.iterator();
		while (it.hasNext())
			rVector.add(it.next());

		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		MatchedSegmentGen seggen = new MatchedSegmentGen(qVector, rVector);

		int cnt = 0;
		while (seggen.hasNext()) {
			Vector<Literal> v = seggen.next();
			cnt++;
			printVector(v);
			Iterator<Integer> itMap = seggen.lastTheta.keySet().iterator();
			while (itMap.hasNext()) {
				int key = itMap.next();
				int id = seggen.lastTheta.get(key);
				System.out.println(SymTable.getSym(key) + "->" + SymTable.getSym(id));
			}
		}
		System.out.println("Count: " + cnt);
		
		
	}
	
	public static void printVector(Vector<Literal> v) {
		Iterator<Literal> it = v.iterator();
		while (it.hasNext())
			System.out.print(it.next() + " ");
		System.out.println();
	}
}
