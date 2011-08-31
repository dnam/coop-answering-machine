package org.inouelab.coopqa.base;

import java.util.*;

import org.inouelab.coopqa.web.shared.Operator;
import org.inouelab.coopqa.web.shared.WebQuerySet;

/**
 * A set of pairwise non-equivalent queries.
 * This is a sub-class of {@link java.util.HashSet}
 * This class relies on {@link Query#hashCode()} and
 * {@link Query#equals(Object)} to differentiate {@link Query}
 * objects.
 * 
 * The object also contains information about
 * - Operations that've performed to obtain this set
 * - The parent of the set
 * - The list of child sets
 * 
 * @author Nam Dang
 */
public class QuerySet extends HashSet<Query> {
	private static final long serialVersionUID = 4528402400374370351L;
	
	private Vector<Integer> ops; // Operator performed upon this set
	private QuerySet parent;
	private Vector<QuerySet> children;
	private int depth;

	/**
	 * Constructs an empty query set with empty children
	 * list and parent as <code>null</code>
	 */
	public QuerySet() {
		super();		
		ops = new Vector<Integer>();
		children = new Vector<QuerySet>();
		parent = null;
		depth = 0;
	}
	
	/**
	 * @return the depth of this query in the execution tree
	 */
	public int getDepth() {
		return depth;
	}
	
	/**
	 * Constructs an query set with empty children
	 * list and parent as <code>null</code>. However,
	 * the elements in the set are from the input set
	 * @param set the set of {@link Query} to add to
	 */
	public QuerySet(Set<Query> set) {
		super(set);		
		ops = new Vector<Integer>();
		children = new Vector<QuerySet>();
		parent = null;
	}
	
	/**
	 * Constructs an empty set with empty list of children
	 * but with the specified parent.
	 * @param parent the parent set of this set
	 */
	public QuerySet(QuerySet parent) {
		this();
		this.parent = parent;
		this.depth = parent.depth + 1;
	}
	
	/**
	 * Constructs a set containing element from a given set
	 * and the parent with the specified parent
	 * @param set		a set of {@link Query}
	 * @param parent	the parent of this set
	 */
	public QuerySet(Set<Query> set, QuerySet parent) {
		this(set);
		this.parent = parent;
	}
	
	/**
	 * Adds an operator to the list of operators performed upon
	 * this set
	 * @param opType the type of the operator
	 * @see Operator
	 */
	public void addOperator(int opType) {
		ops.add(opType);
	}
	
	/**
	 * Adds all operators of the other set to this set
	 * @param other the other <code>QuerySet</code> object
	 * @see Operator
	 */
	public void addAllOps(QuerySet other) {
		this.ops.addAll(other.ops);
	}
	
	/**
	 * Sets the parent of this set
	 * @param parent the parent of this set
	 */
	public void setParent(QuerySet parent) {
		this.parent = parent;
		this.depth = parent.depth + 1;
	}	
	
	/**
	 * Adds a child to list of children of this set
	 * @param child the new children
	 */
	public void addChild(QuerySet child) {
		children.add(child);
		child.setParent(this);
	}
	
	/**
	 * @return the parent of this set
	 */
	public QuerySet getParent() {
		return this.parent;
	}
	
	/**
	 * @param idx the index of the wanted child
	 * @return	the set at this index
	 */
	public QuerySet getChildAt(int idx) {
		return children.get(idx);
	}
		
	/**
	 * @return the {@link Iterator} of the children list
	 */
	public Iterator<QuerySet> getChildIterator() {
		return children.iterator();
	}
	
	/**
	 * When performing generalization upon a set, we are more
	 * concerned of the most recent operation done upon the
	 * queries in the set
	 * @return the last operation done upon this set
	 * @see Operator
	 */
	public Integer getLastOp() {
		if (this.ops.size() == 0)
			return null;
		
		return this.ops.lastElement();
	}
	
	/**
	 * @return the string representing the operations done upon this
	 * 			query set
	 * @see Operator
	 */
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
	 * @return a string representing the QuerySet with operators.
	 * 			It'll be in the following format:
	 * 				AI.DC.GR( Q(X) ):
	 * 				[ query 0 ]
	 * 				[ query 1]
	 */
	@Override
	public String toString() {
		StringBuilder bld = new StringBuilder();
		bld.append("Depth: " + depth + "\n\n");
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
	 * This method <b>should</b> be used only at the root of the
	 * set tree
	 * @return a corresponding set in the Web format
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
