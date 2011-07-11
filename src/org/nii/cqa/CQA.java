/**
 * Author: Maheen Bakhtyar and Nam Dang
 * Org: NII
 */
package org.nii.cqa;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.nii.cqa.base.Query;
import org.nii.cqa.operators.Operator;
import org.nii.cqa.parser.QueryParser;



public class CQA {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		//just checking SymTable[remove]
		//int id = SymTable.addSymbol("researches", SymType.PREDICATE);
		
		//just checking Literal[remove]
		//Literal l = new Literal();
		//l.setId(id);
		//System.out.println(l.getId() + " " + l.toString());
		
		QueryParser p;
		p = new QueryParser(new FileReader("../CQA/lib/query1.txt"));
		
		Query q1 = (Query) p.parse().value;
		
		p = new QueryParser(new FileReader("../CQA/lib/query2.txt"));
		Query q2 = (Query) p.parse().value;
		System.out.println("Result: " + q1.equals(q2));

//		Set<Query> inSet = new HashSet<Query>();
//		inSet.add(q1);
//		
//		System.out.println("________________");
//		Set<Query> ret = Operator.DC.run(inSet);
//		Iterator<Query> it = ret.iterator();
//		inSet.clear();
//		while(it.hasNext()) {
//			System.out.println("DC: " + it.next());
//		}
//		
//		ret = Operator.DC.run(ret);
//		System.out.println("________________");
//		it = ret.iterator();
//		inSet.clear();
//		while(it.hasNext()) {
//			System.out.println("DC: " + it.next());
//		}
//		
	}

}
