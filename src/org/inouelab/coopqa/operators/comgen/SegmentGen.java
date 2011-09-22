package org.inouelab.coopqa.operators.comgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.inouelab.coopqa.base.Literal;

/**
 * Beta: Segment generator for GR
 * @author Nam Dang
 * @see MultiSegmentGen
 */
class SegmentGen 
        implements Iterator<List<Literal>>, Iterable<List<Literal>> {
    private final List<Literal> 					querySeg;
    private int[] 									currentIdxs;
    private final int[] 							lastIdxs;
	private final Map<Integer, Integer> 			theta;
	private final ArrayList<Map<Integer, Integer>> 	savedThetas;
	// For mapping
	private Map<Integer, Integer>[][]	ruleMatrix;

	/**
	 * Constructor for the Segment Generator
	 * @param querySeg the segment of the query as a list of {@link Literal}
	 * @param ruleSeg the the rule as a list of {@link Literal}
	 * @param theta the shared theta of the job
	 */
	public SegmentGen(List<Literal> querySeg, List<Literal> ruleSeg, 
    		Map<Integer, Integer> theta) {
    	int r = ruleSeg.size();
        if(r < 1 || r > querySeg.size())
            throw new IllegalArgumentException("r < 1 || r > querySeg.size()");
        
        this.theta = theta;
               
        this.savedThetas = new ArrayList<Map<Integer, Integer>>(r);
        this.querySeg = new ArrayList<Literal>(querySeg);
        this.currentIdxs = new int[r];
        this.lastIdxs = new int[r];
        
        setRuleMatrix(ruleSeg);       
        
		setInitialIndexes();
	}

	/**
	 * Set the rules from literals to literals between the
	 * rule and the query segment
	 * @param ruleSeg the rule segment to set
	 */
	@SuppressWarnings(value = "unchecked")
	private void setRuleMatrix(List<Literal> ruleSeg) {
		int r = ruleSeg.size();
		int q = querySeg.size();
		ruleMatrix = new Map[r][q];		
		
		for (int i = 0; i < r; i++) {
			Literal rLit = ruleSeg.get(i);
			for (int j = 0; j < q; j++) {
				Literal qLit = querySeg.get(j);
				Map<Integer, Integer> newTheta = rLit.getSubRule(qLit);
				ruleMatrix[i][j] = newTheta;
			}
		}
	}
    
	/**
	 * Set the intial indexes
	 */
    private void setInitialIndexes() {
    	int r = lastIdxs.length;
    	currentIdxs = new int[r];
    	
    	for (int i = 0; i < r; i ++) { 
    		lastIdxs[i] = querySeg.size() - r + i;
    		savedThetas.add(null); // add a new element
    	}
    	
    	currentIdxs[0] = 0;
    	if(!setIdxesFrom(0))
    		currentIdxs = null;
    }
    
    /**
     * Extract the new rules from the new theta
     * @param newTheta new theta to extract rules from
     * @return a map of new rules
     */
    private Map<Integer, Integer> extractNewRules(Map<Integer, Integer> newTheta) {
    	Map<Integer, Integer> copyNewTheta = new HashMap<Integer, Integer> (newTheta);
    	
    	copyNewTheta.entrySet().removeAll(theta.entrySet());
    	
    	return copyNewTheta;
    }
    
    /**
     * Checks if the newTheta has any conflicting rules with
     * the current working theta
     * @param newTheta the theta to check against
     * @return <code>true</code> if conflicts
     */
    private boolean isConflicted(Map<Integer, Integer> newTheta) {
    	Iterator<Integer> it = newTheta.keySet().iterator();
    	while(it.hasNext()) {
    		Integer key = it.next();
    		if (!theta.containsKey(key))
    			continue;
    		
    		Integer otherVal = newTheta.get(key);
    		Integer thisVal = theta.get(key);
    		
    		if (!thisVal.equals(otherVal))
    			return true;
    	}

    	return false;
    }
    
    /**
     * Resets the generator
     */
    public void reset() {
    	for (int i = 0; i < savedThetas.size(); i++) {
    		removeSubTheta(savedThetas.get(i));
    	}
    	
    	setInitialIndexes();
    }

    @Override
	public boolean hasNext() {
        return currentIdxs != null;
    }

    @Override
	public Iterator<List<Literal>> iterator() {
        return this;
    }
    
    @Override
	public List<Literal> next() {
        if(!hasNext()) {
            throw new NoSuchElementException();
        }
        
        List<Literal> currentCombination = new ArrayList<Literal>();
        for(int i : currentIdxs) {
            currentCombination.add(querySeg.get(i));
        }
        
        setNextIndexes();
        
        return currentCombination;
    }
    
    @Override
	public void remove() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Removes the subTheta from theta 
     * @param subTheta
     */
    private void removeSubTheta(Map<Integer, Integer> subTheta) {
    	if (subTheta == null)
    		return;
    	
    	Iterator<Integer> keyIt = subTheta.keySet().iterator();
    	while(keyIt.hasNext()) {
    		Integer key = keyIt.next();
    		theta.remove(key);
    	}
    }
    
    /**
     * Set the next indexes for the generator
     */
    private void setNextIndexes() {
        for(int i = currentIdxs.length-1, j = querySeg.size()-1; i >= 0; i--, j--) {
            if(currentIdxs[i] != j) {          	
            	// try to set the indexes from (i-length)
            	// starting from here it's similar to setInitialIndexs
            	
            	// First remove all the theta generated from i--length
            	for (int k = i; k < currentIdxs.length; k++) {
            		removeSubTheta(savedThetas.get(k));
            		savedThetas.set(k, null);
            	}
            	 	
            	// Now create a new set of matched literals
            	currentIdxs[i]++;
            	
            	if(setIdxesFrom(i))            	
            		return;
            }
        }
        
        currentIdxs = null;
    }
    
    /**
     * Sets index from a position
     * @param startPos the starting position
     * @return <code>true</code> if successful
     */
    private boolean setIdxesFrom(int startPos) {
    	int i = startPos;
    	
    	boolean forward = true;
        while(i < currentIdxs.length) {
        	if (i < startPos) { // Total failure
        		return false;
        	}
     
        	// we're falling back, remove current local theta
            if (!forward) { 
            	removeSubTheta(savedThetas.get(i));
            	savedThetas.set(i, null);
            }
            
            int startIdx = (forward)? 
            		((i == startPos)? currentIdxs[i]: currentIdxs[i-1]+1): 
            			currentIdxs[i]+1;
            
            currentIdxs[i] = -1;
            for(int j = startIdx; j <= lastIdxs[i]; j++) {
            	Map<Integer, Integer> newTheta = ruleMatrix[i][j];
            	
            	if (newTheta != null && !isConflicted(newTheta)) {
            		newTheta = extractNewRules(newTheta);
            		
            		// Add to the history
                	savedThetas.set(i, newTheta);
            		theta.putAll(newTheta);   
            		
                	currentIdxs[i] = j;
                	break;
            	}
            }
            
            if (currentIdxs[i] < 0) { // fail to find a suitable index
            	i--;
        		forward = false;
            }
            else {
            	i++;
            	forward = true;
            }
        }
        
        return true;
    }
}