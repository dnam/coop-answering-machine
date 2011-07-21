package org.nii.cqa.operators.comgen;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.nii.cqa.base.*;
import org.nii.cqa.parser.QueryParser;

public class SegmentGen  {
	private final Vector<Literal> set;
    private int[] currentIdxs;
    private final int[] lastIdxs;
    private final Vector<Map<Integer, Integer>> thetaVec;
    private final Map<Integer, Integer> curTheta;
    private final Map<Integer, Integer> lastTheta;
    private Vector<Literal> rule;
    
    public SegmentGen(Vector<Literal> query, 
    		Vector<Literal> rule, Map<Integer, Integer> initMap) {
    	int r = rule.size();
    	if(r < 1 || r > query.size()) {
    		System.err.println("r: " + r + " query: " + query.size());
            throw new IllegalArgumentException("r < 1 || r > set.size()");
        }
    	
        this.set = new Vector<Literal>(query);
        this.currentIdxs = new int[r];
        this.lastIdxs = new int[r];
        this.rule = new Vector<Literal>(rule);
        this.thetaVec = new Vector<Map<Integer, Integer>>(r);
        this.curTheta = new HashMap<Integer, Integer>();
        this.lastTheta = new HashMap<Integer, Integer>();
        
        // Init Map at 0
        Map<Integer, Integer> mapAtZero = new HashMap<Integer, Integer>(initMap);
        this.thetaVec.add(mapAtZero);
        for (int i = 1; i < r; i++) {
        	this.thetaVec.add(null);
        }
        
        for(int i = 0, j = 0; i < r && j < this.set.size(); j++) {
        	Map<Integer, Integer> theta = new HashMap<Integer, Integer>();
        	if (this.thetaVec.get(i) != null)
        		theta.putAll(this.thetaVec.get(i));
        	
        	if (this.rule.get(i).subsume(this.set.get(j), theta)) {
        		if (i+1 < r)
        			this.thetaVec.set(i+1, theta);
        		else {
        			curTheta.clear();
        			curTheta.putAll(theta);
        		}
        		
        		this.currentIdxs[i] = j;
        		this.lastIdxs[i] = this.set.size() - r + j;
        		i++;
        	}
        	
        	if (i < r && j + 1 >= this.set.size()) {
        		currentIdxs = null;
        		break;
        	}
        }
    }
    
    public void reset(Map<Integer, Integer> initMap) {
    	int r = lastIdxs.length;
    	currentIdxs = new int[r];
        for(int i = 0; i < r; i++) {
            this.currentIdxs[i] = i;
            this.lastIdxs[i] = set.size() - r + i;
            this.thetaVec.set(i, null);
        }
        
        Map<Integer, Integer> mapAtZero = new HashMap<Integer, Integer>(initMap);
        this.thetaVec.set(0, mapAtZero);
        
    }

    public boolean hasNext() {
        return currentIdxs != null;
    }


    public Vector<Literal> next() {
        if(!hasNext()) {
            throw new NoSuchElementException();
        }
        
        Vector<Literal> retVec = new Vector<Literal>();
        for(int i : currentIdxs) {
        	retVec.add(set.get(i));
        }
        
//        System.out.println("SegmentGen:");
//        System.out.print("retVec");
//        MatchedSegmentGen.printVector(retVec);
//        System.out.print("rule");
//        MatchedSegmentGen.printVector(rule);
//        Iterator<Integer> itMap = curTheta.keySet().iterator();
//		while (itMap.hasNext()) {
//			int key = itMap.next();
//			int id = curTheta.get(key);
//			System.out.println("From: " + SymTable.getSym(key) + "->" + SymTable.getSym(id));
//		}
//		System.out.println("end seg");
        
        lastTheta.clear();
        lastTheta.putAll(curTheta);
        
        setNextIndexes();
        
        return retVec;
    }
    
    public Map<Integer, Integer> getTheta() {
    	Map<Integer, Integer> tmpTheta = 
    		new HashMap<Integer, Integer>(lastTheta);
    	return tmpTheta;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    private void setNextIndexes() {
        for(int i = currentIdxs.length-1, j = set.size()-1; i >= 0; i--, j--) {
            if(currentIdxs[i] != j) {
            	currentIdxs[i]++;

            	Map<Integer, Integer> theta = new HashMap<Integer, Integer>();
            	if (thetaVec.get(i) != null)
            		theta.putAll(thetaVec.get(i));
            	
            	if (rule.get(i).subsume(set.get(currentIdxs[i]), theta)) {
            		if (i+1 < thetaVec.size())
            			thetaVec.set(i+1, theta);
            		else {
            			curTheta.clear();
            			curTheta.putAll(theta);
            		}
            		
            		int k = i+1;
            		while (k < currentIdxs.length) {
            			int idx = currentIdxs[k-1]+1;
            			
            			theta = new HashMap<Integer, Integer>(thetaVec.get(k));
            			
            			while(idx < set.size()) {            				
            				if (rule.get(k).subsume(set.get(idx), theta)) {
            					currentIdxs[k++] = idx;
            					if (k + 1 < thetaVec.size())
            						thetaVec.set(k + 1, theta);
            					else {
            						curTheta.clear();
            						curTheta.putAll(theta);
            					}
            					
            					break;
            				}
            				
            				idx++;            				
            			}
                    }
            		
            		if (k == currentIdxs.length)
            			return;
            	}
            }
        }
        
        currentIdxs = null;
    }
    
    public static void main(String args[]) throws Exception {
    	QueryParser p;
		p = new QueryParser(new FileReader("../CQA/lib/gen_query.txt"));
		Query query = (Query) p.parse().value;
		Vector<Literal> qVector = new Vector<Literal>();
		Iterator<Literal> it = query.iterator();
		while(it.hasNext())
			qVector.add(it.next());
		
		p = new QueryParser(new FileReader("../CQA/lib/gen_rule.txt"));
		Query rule = (Query) p.parse().value;
		
		Vector<Literal> rVector = new Vector<Literal>();
		it = rule.iterator();
		while(it.hasNext())
			rVector.add(it.next());
		
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		SegmentGen seggen = new SegmentGen(qVector, rVector, map);
		
		int cnt = 0;
		while(seggen.hasNext()) {
			Vector<Literal> v = seggen.next();
			cnt++;
			for (Literal l: v) {
				System.out.print(l + " ");
			}
			System.out.println();
		}
		System.out.println("Count: " + cnt);
    }
}
