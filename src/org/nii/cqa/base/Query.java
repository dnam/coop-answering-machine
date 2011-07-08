/**
 * @author Maheen Bakhtyar
 * Description: The Query Class 
 */
package org.nii.cqa.base;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;



public class Query implements Comparable<Query> {
	private PriorityQueue<Literal> query;

	public Query() {
		query = new PriorityQueue<Literal>();
	}

	public void add(Literal literal) {
		query.add(literal);
	}

	public Iterator<Literal> iterator() {
		return query.iterator();
	}

	@Override
	public int compareTo(Query otherQ) {  //compares if two Queries are duplicate
		/** return 0 means duplicate queries
		 *  -1 means otherQuery(parameter query) is ranked higher
		 * 1 means this query is ranked higher
		*/
		int rank = 0;
		boolean eqFlag = true;

		this.printQ();
		otherQ.printQ();
		//		System.out.println(this.query.get(0).getParamAt(0));
		//		System.out.println(this.query.get(0).getID());

		if(this.query.size() == otherQ.query.size())	//checks if # of literals in both queries same
		{
			System.out.println("Qs have same size");
			Iterator<Literal> it1 = this.query.iterator();
			Iterator<Literal> it2 = otherQ.query.iterator();
			while(it1.hasNext())
			{
				Literal l1 = it1.next();
				Literal l2 = it2.next();
				Map<Integer, Integer> theta = new HashMap<Integer, Integer>();
				if(l1.isEquivalent(l2, theta))	//checks if l1 and l2 are equivalent
				{
					eqFlag = true;
					System.out.println("Literals are equal");

				}
				else{
					eqFlag = false;
					break;
				}
			}
			if(!eqFlag)			//checks if Queries are not equivalent
			{
				rank = 2;    //to change the initial value from 0 which means equivalent. Making sure to change it to 1 or -1 later
				System.out.println("Not equivalent");
				int sum1 = 0,sum2 = 0;					//calculate sum of IDs of literals to rank
				it1 = this.query.iterator();
				it2 = otherQ.query.iterator();
				
				while(it1.hasNext())
				{
					Literal l1 = it1.next();					
					Literal l2 = it2.next();

					if(l1.isNegative())
						sum1 -= l1.getID();
					else 
						sum1 += l1.getID();

					if(l2.isNegative())
						sum2 -= l2.getID();
					else 
						sum2 += l2.getID();
					
				}
				
				if(sum1 >= sum2)
					rank = 1;
				else
					rank = -1;
			}
		}
		else if(this.query.size() < otherQ.query.size())	//checks if # of literals in q1 < than that of q2
		{
			System.out.println("first query is lower");
			rank = -1;
		}
		else if(this.query.size() > otherQ.query.size())	//checks if # of literals in q1 > than that of q2
		{
			System.out.println("first query is higher");
			rank = 1;
		}

		return rank;
	}

	public void printQ()
	{
		System.out.println("Query:");
		Iterator<Literal> it = query.iterator();
		while(it.hasNext())
		{
			Literal l = it.next();
			// l.getAllParams() = All parameters of that single literal
			System.out.println("literal " + l.getID()+ "(" + l.getAllParams() + ")");
		}
		System.out.println("---------------------------------------");
	}

	// @author: Nam Dang
	// Converts the query into a string. Now is just a fake class
	public String toString() {
		return "this query has " + query.size() + " literals";
	}



}
