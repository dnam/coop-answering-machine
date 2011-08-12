package org.inouelab.coopqa.base;

import java.io.Serializable;
import java.util.*;

import org.inouelab.coopqa.web.shared.Operator;
import org.inouelab.coopqa.web.shared.WebQuerySet;

public class QuerySet extends HashSet<Query> implements Serializable {
	private static final long serialVersionUID = 10L;
	private Vector<Integer> ops; // Operator performed upon this set
	private QuerySet parent;
	private Vector<QuerySet> children;

	public QuerySet() {
		super();		
		ops = new Vector<Integer>();
		children = new Vector<QuerySet>();
		parent = null;
	}
	
	public QuerySet(Set<Query> set) {
		super(set);		
		ops = new Vector<Integer>();
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
	
	public void addOperator(int opType) {
		ops.add(opType);
	}
	
	public void addAllOps(QuerySet other) {
		this.ops.addAll(other.ops);
	}
	
	public void setParent(QuerySet parent) {
		this.parent = parent;
	}	
	
	public void addChild(QuerySet child) {
		children.add(child);
	}
	
	public QuerySet getParent() {
		return this.parent;
	}
	
	public QuerySet getChildAt(int idx) {
		return children.get(idx);
	}
		
	public Iterator<QuerySet> getChildIterator() {
		return children.iterator();
	}
	
	
	public Integer getLastOp() {
		if (this.ops.size() == 0)
			return null;
		
		return this.ops.lastElement();
	}
	
	public String getOpStr() {
		String bld = new String();
		if (ops.size() == 0) {
			bld +=  ("()");
		}
		else {
			for (int i = ops.size() - 1; i >= 0; i--) {
				int op = ops.get(i);
				switch(op) {
				case Operator.AI_t:
					bld += ("AI");
					break;
				case Operator.DC_t:
					bld += ("DC");
					break;
				case Operator.GR_t:
					bld += ("GR");
					break;
				}
				
				if (i > 0)
					bld += (".");
			}
		}
		
		return bld;
	}
	
	@Override
	public String toString() {
		StringBuilder bld = new StringBuilder();
		if (ops.size() == 0) {
			bld.append("(): ");
		}
		else {
			for (int i = ops.size() - 1; i >= 0; i--) {
				int op = ops.get(i);
				switch(op) {
				case Operator.AI_t:
					bld.append("AI");
					break;
				case Operator.DC_t:
					bld.append("DC");
					break;
				case Operator.GR_t:
					bld.append("GR");
					break;
				}
				
				if (i >= 0)
					bld.append("(");
			}
			
			bld.append(" Q(X) ");
			for (int i = 0; i < ops.size(); i++)
				bld.append(")");
			bld.append(": ");
		}
		
		bld.append(" ");
		bld.append(super.toString());
		bld.append("\n");
		
		for (int i = 0; i < children.size(); i++) {
			bld.append(children.get(i));
		}
		
		return bld.toString();
	}
	
	/**
	 * Must be used at root
	 * @return the tree set
	 */
	public WebQuerySet webConvert() {
		WebQuerySet webSet = new WebQuerySet(ops);
		
		// Convert the current set
		Iterator<Query> it = this.iterator();
		while(it.hasNext()) {
			webSet.add(it.next().webConvert());
		}
		
		// Add its children		
		for (int i = 0; i < children.size(); i++) {
			WebQuerySet curChild = children.get(i).webConvert();
			curChild.setParent(webSet);
			
			webSet.addChild(curChild);
		}
		
		return webSet;
	}
}
