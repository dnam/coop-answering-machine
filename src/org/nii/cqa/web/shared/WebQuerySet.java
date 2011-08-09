package org.nii.cqa.web.shared;

import java.awt.geom.QuadCurve2D;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

public class WebQuerySet extends Vector<WebQuery> implements Serializable {
	private static final long serialVersionUID = 10L;
	private Vector<Integer> ops; // Operator performed upon this set
	private WebQuerySet parent;
	private Vector<WebQuerySet> children;
	private WebAnswerMap ansMap;
	

	public WebQuerySet() {
		super();
		this.ops = new Vector<Integer>();;
		this.parent = null;
		this.children = new Vector<WebQuerySet>();
		this.ansMap = new WebAnswerMap();
	}
	
	public WebQuerySet(Vector<Integer> ops) {
		super();		
		this.ops = new Vector<Integer>();
		this.ops.addAll(ops);
		
		this.parent = null;
		this.children = new Vector<WebQuerySet>();
	}
	
	public void addOperator(int op) {
		ops.add(op);
	}
	
	public void addAllOps(WebQuerySet other) {
		this.ops.addAll(other.ops);
	}
	
	public void setParent(WebQuerySet parent) {
		this.parent = parent;
	}	
	
	public void addChild(WebQuerySet child) {
		children.add(child);
	}
	
	public WebQuerySet getParent() {
		return this.parent;
	}
	
	public WebQuerySet getChildAt(int idx) {
		return children.get(idx);
	}
		
	public Iterator<WebQuerySet> getChildIterator() {
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
	
	/**
	 * Must be called at the root
	 * @param other
	 */
	public void setAnsMap(WebAnswerMap other) {
		this.ansMap = other;
		for (int i = 0; i < children.size(); i++)
			children.get(i).setAnsMap(other);
	}
	
	/**
	 * Prints the current set only (no children)
	 */
	public String printSelf() {
		String bld = "";
		
		for (int i = 0; i < super.size(); i++) {
			WebQuery q = super.get(i);
			bld += ("Query: " + q + "\n");
			bld += ("Answer(s):\n" + q.getAnsString(ansMap));
			
			if (i + 1 < super.size())
				bld += "\n\n";
		}
		return bld;
	}
	
	@Override
	public String toString() {
		String bld = "";
		if (ops.size() == 0) {
			bld +=  ("(): ");
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
				
				if (i >= 0)
					bld += ("(");
			}
			
			bld += (" Q(X) ");
			for (int i = 0; i < ops.size(); i++)
				bld += (")");
			bld += (": ");
		}
		
		bld += " " + super.toString() + "\n";
		
		for (int i = 0; i < children.size(); i++) {
			WebQuerySet child = children.get(i);
			
			bld += "\n" + child.getOpStr() + "\n";
			bld += (child.toString());
		}
		
		return bld.toString();
	}

	/**
	 * Recursively generates the table
	 */
	private void genData(Vector<WebQuerySet> setVec) {
		// Add to setList
		setVec.add(this);
		
		// Add the children, keep track of the count
		for (int i = 0; i < children.size(); i++) {
			children.get(i).genData(setVec);
		}
	}
	
	public Vector<WebQuerySet> getAllSets() {
		Vector<WebQuerySet> setVec = new Vector<WebQuerySet>();
		
		genData(setVec);
		
		return setVec;
	}
}
