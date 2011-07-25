package org.nii.cqa.base;

import java.util.*;

import org.nii.cqa.base.*;
import org.nii.cqa.operators.*;

public class QuerySet extends HashSet<Query> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Vector<Operator> ops; // Operator performed upon this set
	private QuerySet parent;
	private Vector<QuerySet> children;

	public QuerySet() {
		super();		
		ops = new Vector<Operator>();
		children = new Vector<QuerySet>();
		parent = null;
	}
	
	public QuerySet(Set<Query> set) {
		super(set);		
		ops = new Vector<Operator>();
		children = new Vector<QuerySet>();
		parent = null;
	}
	
	public QuerySet(QuerySet parent) {
		this();
		this.parent = parent;
	}
	
	public QuerySet(Set<Query> set, QuerySet parent) {
		this(set);
		this.parent = parent;
	}
	
	public void addOperator(Operator op) {
		ops.add(op);
	}
	
	public void setParent(QuerySet parent) {
		this.parent = parent;
	}
	
	public QuerySet getParent() {
		return this.parent;
	}
	
	public void addChild(QuerySet child) {
		children.add(child);
	}
	
	public Iterator<QuerySet> getChildIterator() {
		return children.iterator();
	}
	
	public QuerySet getChildAt(int idx) {
		return children.get(idx);
	}
	
	public void addAllOps(QuerySet other) {
		this.ops.addAll(other.ops);
	}
}
