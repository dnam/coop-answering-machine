/*
 * @author: Maheen
 * Description:
 */
package org.nii.cqa.base;

import java.util.Iterator;
import java.util.Vector;

public class Query implements Comparable<Query> {
	private Vector<Literal> query;

	public Query() {
		query = new Vector<Literal>();
	}

	public void add(Literal literal) {
		query.add(literal);
	}
	
	public Iterator<Literal> iterator() {
		return query.iterator();
	}
	
	@Override
	public int compareTo(Query otherQ) {
		// return 0 means equal. -1 means otherQuery is higher, 1 means this query is higher
		
		int rank = 0;

//		System.out.println(this.query.get(0).getParamAt(0));
//		System.out.println(this.query.get(0).getID());
		
		if(this.query.size() == otherQ.query.size())
		{
			System.out.println("Qs have same size");
			
		}
		else if(this.query.size() < otherQ.query.size())
		{
			System.out.println("this is lower");
			rank = -1;
		}
		else if(this.query.size() > otherQ.query.size())
		{
			System.out.println("this is higher");
			this.printQ();
			rank = 1;
		}

		
		return rank;
	}
	
	public void printQ()
	{
		for(int i = 0; i<query.size(); i++)
		{
			// query.get(i).getID() = Single Literal ID
			// query.get(i).getAllParams() = All parameters of that single literal
			System.out.println("literal " + query.get(i).getID()+ "(" + query.get(i).getAllParams() + ")");
		}
	}

	// @author: Nam Dang
	// Converts the query into a string. Now is just a fake class
	public String toString() {
		return "this query has " + query.size() + " literals";
	}

	

}
