package org.inouelab.coopqa.operators.comgen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

public class MultiCombinationGenerator<E>
	implements Iterator<List<E>>, Iterable<List<E>>{
	private int[] kList;
	private Vector<CombiGenerator<E>> genList;
	private List<E> nextResult;
	private Vector<List<E>> lastResults;
	
	public MultiCombinationGenerator (List<List<E>> multiList, int[] kList) {
		genList = new Vector<CombiGenerator<E>>();
		
		if (kList == null || multiList.size() != kList.length)
			throw new IllegalArgumentException("Invalid arguments");
		
		this.kList = kList.clone();
		
		// Generate a list of combination generator
		// and generate the first combination
		this.nextResult = new Vector<E>();
		this.lastResults = new Vector<List<E>>();
		for (int i = 0; i < kList.length; i++) {
			CombiGenerator<E> comb = new CombiGenerator<E>(multiList.get(i), kList[i]);
			genList.add(comb);
			
			if (nextResult!= null && comb.hasNext()) {
				List<E> ret = comb.next();
				lastResults.add(ret);
				nextResult.addAll(ret);
			}
			else {
				nextResult = null;
				break; // don't bother to proceed. There's no more combinations to generate
			}
		}
		
	}

	@Override
	public Iterator<List<E>> iterator() {
		 return this;
	}

	@Override
	public boolean hasNext() {
		return (nextResult != null);
	}

	@Override
	public List<E> next() {
		if(!hasNext()) {
            throw new NoSuchElementException();
        }
		
		List<E> toReturn = new ArrayList<E>();
		toReturn.addAll(nextResult);
		nextResult.clear();
		
		int idx = kList.length - 1;		
		while(idx >= 0 && !genList.get(idx).hasNext()) {
			genList.get(idx).reset();
			idx--;
		}
		
		if (idx < 0) {
			nextResult = null;
			return toReturn;
		}
		
		// Not null
		for (int i = 0; i < idx; i++)		
			nextResult.addAll(lastResults.get(i));
		
		
		// Move the iterator
		for (int i = idx; i < kList.length; i++) {
			List<E> next = genList.get(i).next();
			lastResults.set(i, next);
			nextResult.addAll(next);	
		}

//		System.out.print("Line 94: ");
//		Iterator<E> it = nextResult.iterator();
//		while(it.hasNext()) {
//			System.out.print(it.next());
//		}
//		System.out.println();
		
		
		return toReturn;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	
	public static void main(String args[]) {
		String alphabet = "abcdefghijklmnopqrstuvwxyz";
		Vector<String> strVec = new Vector<String>();
		List<List<Character>> charMultiList = new ArrayList<List<Character>>();
		int[] kList = {2,3,1};
		int start = 5;
		for (int i = 0; i < 3; i++) {
			List<Character> subStr = new ArrayList<Character>();
			
			for (int j = 0; j < i+start; j++)
				subStr.add(alphabet.charAt(i));
			
			charMultiList.add(subStr);
			start++;
		}
		
		Iterator<List<Character>> it = charMultiList.iterator();
		while(it.hasNext()) {
			List<Character> ret = it.next();
			Iterator<Character> charIt = ret.iterator();
			while(charIt.hasNext())
				System.out.print(charIt.next());
			System.out.println();
		}
		
		System.out.println("==============");
		// Perform multicombination
		int cnt = 0;
		it = new MultiCombinationGenerator<Character>(charMultiList, kList);
		
		while(it.hasNext()) {
			cnt++;
			List<Character> ret = it.next();
			Iterator<Character> charIt = ret.iterator();
			while(charIt.hasNext())
				System.out.print(charIt.next());
			System.out.println();
		}
		
		System.out.println("Count: " + cnt); // should be 3150
	}
}
