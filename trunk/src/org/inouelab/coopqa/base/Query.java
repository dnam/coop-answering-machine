package org.inouelab.coopqa.base;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.inouelab.coopqa.Env;
import org.inouelab.coopqa.operators.Operator;
import org.inouelab.coopqa.parser.QueryParser;
import org.inouelab.coopqa.solar.SolarConnector;
import org.inouelab.coopqa.web.shared.WebQuery;

/**
 * A class represents a query in disjucntive normal form (DNF)
 * It provides methods for:
 * - Checking equivalence (see {@link Query#equals(Object)})
 * - Checking subsumption (see {@link Query#isSubsumedBy(List)})
 * 
 * @author Maheen Bakhtyar
 * @author Nam Dang 
 */
public class Query {
	private int id; // ID of a query, based on a global counter
	private Vector<Literal> litVector; // vector of literals
	private Map<Integer, Integer> idCountMap;
	private Vector<Integer> segVector; // segment vector
	private Integer hashVal; // hash code of the object
	private Env env;
	private boolean skipped; // skipped the query: not solve it
	

	/**
	 * @param env The environment of this job
	 */
	public Query(Env env) {
		this.id = env.symTab().getQueryID();
		this.litVector = new Vector<Literal>();
		this.hashVal = null;
		this.env = env;
		this.skipped = false;
		
		// For AI operators
		idCountMap = new HashMap<Integer, Integer>();
	}
	
	/**
	 * A static method to parse a query from an input file
	 * with respect to a given environment.
	 * @param inputFile the path of the input file
	 * @param env The environment of the job
	 * @return the Query object parsed from inputFile
	 * @throws Exception if parsing error occurs
	 * @see QueryParser#QueryParser(java.io.Reader, Env)
	 * @see QueryParser#parse()
	 */
	public static Query parse(String inputFile, Env env) throws Exception {
		Query q = new Query(env);
		
		QueryParser p = new QueryParser(new FileReader(inputFile), env);
		Query parsedQuery = (Query) p.parse().value;
		
		for (Literal l : parsedQuery.litVector) {
			q.add(l);
		}
		
		return q;
	}
	
	public static Query parseString(String inputString, Env env) throws Exception {
		Query q = new Query(env);

		InputStream is = new ByteArrayInputStream(inputString.getBytes());
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		QueryParser p = new QueryParser(br, env);

		Query parsedQuery = (Query) p.parse().value;

		for (Literal l : parsedQuery.litVector) {
			q.add(l);
		}

		return q;
	}

	public int getID() {
		return this.id;
	}
	
	/**
	 * @return the iterator of literal vector
	 */
	public Iterator<Literal> iterator() {
		return litVector.iterator();
	}
	
	/**
	 * @param skipped <code>true</code> if this query is not included
	 * 					in GR operations
	 * 				  <code>false</code> otherwise
	 */
	public void setSkipped(boolean skipped) {
		this.skipped = skipped;
	}
	
	/**
	 * @return <code>true</code> if the querey is not included in
	 * 			GR operations
	 * 			<code>false</code> otherwise
	 */
	public boolean isSkipped() {
		return skipped;
	}
	
	/**
	 * returns all the variables in the query
	 * @return sorted list of variables
	 */
	private Vector<Integer> getAllVars() {
		Vector<Integer> varVector = new Vector<Integer>();
		
		Iterator<Literal> it = litVector.iterator();
				while (it.hasNext()) {
			       varVector.addAll(it.next().getAllVars());
		}		
		Collections.sort(varVector);
		
		return varVector;
	}

	/**
	 * @param literal the literal to add
	 */
	public void add(Literal literal) {	
		if (litVector.contains(literal)) // Remove duplicates
			return;
		
		litVector.add(literal);
		Collections.sort(litVector);
		hashVal = null; // reset the hash code for later computing
		
		// Update the countVarMap and constSet
		int n = literal.paramSize(); // no. of params
		for (int i = 0; i < n; i++) {
			int id = literal.getParamAt(i);

			Integer cnt = idCountMap.get(id);
			cnt = (cnt == null)? 1 : cnt + 1;
			idCountMap.put(id, cnt);
		}
	}
	
	/**
	 * Extracts the answer string corresponding to
	 * this query from ansMap.
	 * This method is done by looking up the query id in
	 * the map and convert the answer list to a string
	 * @param ansMap the answer map to look up into
	 * @return the string of answers
	 */
	public String getAnswerString(AnswerMap ansMap) {
		if (skipped) {
			return "[skipped]";			
		}
		
		List<List<Integer>> ret = ansMap.get(id);
		Vector<Integer> listVar = getAllVars();
		
		if (ret == null)
			return "[no answer]";
		
		String retStr = "";
		Iterator<List<Integer>> ansIt = ret.iterator();
		while(ansIt.hasNext()) {
			Vector<Integer> ansVector = new Vector<Integer>();
			ansVector.addAll(ansIt.next());
			
			retStr += "[";
			
			// For error reporting
			if (ansVector.size() != listVar.size())
				return "[FATAL ERROR: ansVector's size is different from listVar in Query]";
			
			for (int j = 0; j < ansVector.size(); j++) {
				retStr += (env.symTab().getSym(listVar.get(j))+ "->" + env.symTab().getSym(ansVector.get(j)));
				if (j + 1 < ansVector.size())
					retStr += ", ";
			}
			
			retStr += "]";
			
			if (ansIt.hasNext())
				retStr += "\n";
		}
		
		return retStr;
	}
	
	
	/**
	 * @param litVector a vector of literal to swap
	 * @param i index of the first element
	 * @param j index of the second element
	 */
	private static void swap(Vector<Literal> litVector, int i, int j) {
		Literal tmp = litVector.get(i);
		litVector.set(i, litVector.get(j));
		litVector.set(j, tmp);
	}
	
	/**
	 * Check the equivalence between two queries.
	 * Two queries are defined to be equivalent if:
	 * There is an inversible substitution <code>theta</code> that:
	 * 		- <code>(thisQuery)(theta) == otherQuery</code>
	 * 		- <code>(otherQuery)(inverse-theta) == thisQuery</code>
	 * @param obj the object to check equivalence
	 * @see Literal#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Query))
			return false;
		
		if (obj == this)
			return true;
		
		Query other = (Query) obj;
		
		// First they are of the same size?
		if (this.litVector.size() != other.litVector.size())
			return false;
		
		// Checking predicate fingerprint
		for (int i = 0; i < litVector.size(); i++) {
			Literal thisLit = this.litVector.get(i);
			Literal otherLit = other.litVector.get(i);
			if (thisLit.isNegative() != otherLit.isNegative())
				return false;
			if (thisLit.getPred() != otherLit.getPred())
				return false;
		}
		
		// Build segmentation vector
		if (this.segVector == null)
			this.buildSegment();
		if (other.segVector == null)
			other.buildSegment();
		
		if (this.segVector == null || other.segVector == null) {
			return false;
		}
		
		// Do they have the same segment vector
		if (this.segVector.size() != other.segVector.size())
			return false;
		
		for (int i = 0; i < this.segVector.size(); i++) {
			if (this.segVector.get(i) != other.segVector.get(i))
				return false;
		}
					
		// Storing substitution rule THETA
		Map<Integer, Integer> theta = new HashMap<Integer, Integer>();
		
		// Caching substitution rule and swapping at each step
		Vector<Map<Integer, Integer>> thetaCache = new Vector<Map<Integer, Integer>>();
		Vector<Integer> swapCache = new Vector<Integer>();
		
		// Initialize the cache vectors
		for (int i = 0; i < litVector.size(); i++) {
			thetaCache.add(null);
			swapCache.add(null);
		}
		
		// Now we check segment by segment		
		int i = 0, segIdx = 0;
		boolean fallBack = false;
		while (segIdx < segVector.size()) {
			int begin = (segIdx == 0)? 0 : segVector.get(segIdx-1);
			int end = segVector.get(segIdx);
			
			while (i < end && i >= begin) { // iterate within the segment
				Literal thisLit = this.litVector.get(i);
				
				// Check against [other-i... other-(end-1)] for forwarding case
				// or other-j+1... other-(end-1) for falling back case
				boolean succeed = false;
				int beginIdx = (fallBack)? swapCache.get(i) + 1 : begin;
				
				// Restore the previous state before the fall back
				if (fallBack) {
					swap(other.litVector, i, swapCache.get(i));
					theta = thetaCache.get(i);
				}
				
				for (int j = beginIdx; j < end; j++) {
					Literal otherLit = other.litVector.get(j);
					
					// copy the current theta into a cache
					// as the isEquivalent will change theta
					Map<Integer, Integer> tmpTheta = new HashMap<Integer, Integer>();
					tmpTheta.putAll(theta);
					
					if (thisLit.isEquivalent(otherLit, theta)) {
						// swapping i <--> j
						swap(other.litVector, i, j);
						
						// Store the swap
						swapCache.set(i, j); // we swapped i by j
						
						// Store the old theta into the cache
						thetaCache.set(i, tmpTheta);
						
						// set the succeed bit
						succeed = true;
						break;
					}
					else
						theta = tmpTheta; // restore the old theta
					
					// try matching with the next literal
				}
				
				if (succeed) { // successful
					i++;
					fallBack = false;
				}
				else { // failed substitution. Fall back to a previous one
					thetaCache.set(i, null); // clear the memory for theta cache
					swapCache.set(i, null); // clear the memory of the swap cache
					
					i--;
					fallBack = true;
				}
			}
			
			if (i < 0)
				return false; // TOTAL FAILURE
			
			if (i < begin) // fall back to the previous segment
				segIdx--;
			
			if (i >= end)
				segIdx++;
		}

		return true;
	}

	
	
	
	/**
	 * Converts the query into TPTP topclause for SOLAR input
	 * @see SolarConnector
	 */
	public String toTopClause() {
		StringBuilder str = new StringBuilder();
		
		str.append("cnf(query" + id + ", top_clause, [");
		
		Iterator<Literal> litIt = litVector.iterator();
		while (litIt.hasNext()) {
			str.append(litIt.next().toNegatedString());
			str.append(", ");
		}
		str.append("ans" + id + "(");
		
		// For answer predicate
		String ans_pred = "pf([ans" + id + "(";
		
		Iterator<Integer> varIt = this.getAllVars().iterator();
		while(varIt.hasNext()) {
			str.append(env.symTab().getSym(varIt.next()));
			ans_pred += "_";
			if (varIt.hasNext()) {
				str.append(", ");
				ans_pred += ",";
			}
		}
		str.append(")]).\n");
		ans_pred += ")]).";
		
		// Append the production field
		str.append(ans_pred);
		
		return str.toString();
	}		
	
	/**
	 * @return the string of this query in CoopQA format
	 */
	@Override
	public String toString() {
		Iterator<Literal> it = litVector.iterator();
		StringBuilder str = new StringBuilder();
		while (it.hasNext()) {
			str.append(it.next());
			if (it.hasNext())
				str.append(" & ");
		}

		return str.toString();
	}

	
	@Override
	public int hashCode() {
		if (hashVal == null)
			hashVal = computeHash();
		
		return hashVal;
	}
	
	/**
	 * @return the hash value for the Query
	 */
	private int computeHash() {
		int result = 17;
		
		for (int i = 0; i < litVector.size(); i++)
			result = result * 19 + litVector.get(i).hashCode();
		
		return result;
	}
	

	/**
	 * @return the size of the internal query
	 */
	public int size() {
		return litVector.size();
	}

	/***************** DR OPERTAOR ****************/
	/**
	 * @param i   	the index to drop the literal. Assume that <code>i</code>
	 * 				ranges from 0 to <code>(size() -1)</code>
	 * @return 		a new query by dropping the specified literal
	 * @see Operator#DC
	 */
	public Query dropAt(int i) {
		Query q = this.clone();
		q.remove(i);
		
		return q;
	}
	
	/**
	 * Removes a literal at a certain index
	 * @param 	idx the index
	 * @return 	the removed literal
	 */
	public Literal remove(int idx) {
		if (idx >= litVector.size() || idx < 0)
			throw new IllegalArgumentException("Invalid index");
		
		Literal l = litVector.get(idx);
		for (int i = 0; i < l.paramSize(); i++) {
			int sym = l.getParamAt(i);
			
			// Update the count map
			Integer cnt = idCountMap.remove(sym);
			if (cnt > 1)
				idCountMap.put(sym, cnt-1);
		}
		litVector.remove(idx);
		
		hashVal = null; // reset the hash code
		
		return l;
	}
	
	/********************** AI OPERATOR ****************/
	
	/**
	 * Creates a clone of this query
	 */
	@Override
	public Query clone() {
		Query q = new Query(env);
		for (int i = 0; i < this.litVector.size(); i++) {
			q.add(this.litVector.get(i));
		}
		q.hashVal = this.hashVal;
		q.skipped = this.skipped;
		
		return q;
	}
	
	/**
	 * Builds a vectors containing segments of the query
	 * a segment = a set of equal literals (defined in Literal.compareTo() 
	 * segVector stores the index after the end (end + 1) of each segment (we can get the
	 * beginning by looking at the end of the previous segment)
	 */
	public void buildSegment() {
		if (litVector.size() == 0) // nothing to build
			return;
		
		segVector = new Vector<Integer>();

		int begin = 0, end = 1;
		while (end < litVector.size()) {
			// if we get to the next segment
			if (litVector.get(begin).compareTo(litVector.get(end)) != 0) {
				segVector.add(end);
				begin = end;
			}
			end++;
		}
		
		segVector.add(end);
	}
	
	
	/**
	 * Returns a set of possible constants and variables for
	 * AI operator
	 * @see Operator#AI
	 */
	public Set<Integer> extractSet() {
		Set<Integer> retSet = new HashSet<Integer>();
		
		Iterator<Integer> it = idCountMap.keySet().iterator();
		while(it.hasNext()) {
			int id = it.next();
			if (env.symTab().getTypeID(id) == SymType.CONSTANT 
					||	idCountMap.get(id) > 1)
				retSet.add(id);
		}
		
		return retSet;
	}
	
	/**
	 * Returns a set of queries by replacing each occurrence of a given
	 * constant and/or variable with a new one
	 * @param id 	 the id of the const/var to be replaced
	 * @param newVar the id of the new variable
	 * @return 		 a new set of queries containing newVar
	 * @see			 Operator#AI
	 */
	public Set<Query> replace(int id, int newVar) {
		Set<Query> retSet = new HashSet<Query>();
		int repCnt = idCountMap.get(id); // the id should exists in the map
		
		// if the variable occurs only twice, we only make one replacement
		if (env.symTab().getTypeID(id) == SymType.VARIABLE && repCnt == 2)
			repCnt = 1;
		
		for (int i = 0; i < litVector.size() && repCnt > 0; i++) {
			Literal l = litVector.get(i);
			for (int j = 0; j < l.paramSize() && repCnt > 0; 
						j++) {
				if (l.getParamAt(j) == id) {
					Literal newLiteral = l.clone(); // clone the literal
					newLiteral.setParamAt(j, newVar); // set new variable to the clone
					
					// Create a query
					Query newQuery = this.clone();
					newQuery.remove(i);

					// add the modified literal
					newQuery.add(newLiteral);

					// Add to the result set
					retSet.add(newQuery);
					
					repCnt--; // decrease the counter
				}
			}
		}	
		
		return retSet;
	}
	
	/****************** GR OPERATOR *********************/
	
	/**
	 * Replaces a set of literals in a query with the left side of a
	 * rule
	 * @param litSet set of literals to be replaced
	 * @param ruleLS the left side of the rule
	 * @return a set of resulted queries after substitution
	 * @see Operator#GR
	 */
	public Query replaceLiterals(List<Literal> litSet, Literal ruleLS) {
		Iterator<Literal> it = litSet.iterator();

		// Now the query
		Query q = this.clone();
		while (it.hasNext())
			q.remove(it.next());
		
		// Add the replacement
		q.add(ruleLS);
		
		return q;
	}
	
	public void remove(Literal lit) {
		int begin = 0, end = litVector.size() - 1;
		int mid = -1;
		while (begin <= end) {
			mid = (begin + end)/2;
			int comp = litVector.get(mid).compareTo(lit);
			if (comp == 0)
				break;
			else if (comp > 0)
				end = mid - 1;
			else if (comp < 0)
				begin = mid + 1;
		}
		
		if (mid == -1 || litVector.get(mid).compareTo(lit) != 0)
			throw new IllegalStateException("Unable to locate the literal: " + lit + " in " + this);
		
		while (mid >= 0 && litVector.get(mid).compareTo(lit) == 0)
			mid--;
		mid++;
		
		while (mid < litVector.size() && litVector.get(mid).compareTo(lit) == 0) {
			if (litVector.get(mid).equals(lit)) {
				this.remove(mid);
				hashVal = null;
				return;
			}
			mid++;
		}
		
		// Otherwise
		throw new IllegalStateException("Unable to locate the literal: " + lit + " in " + this);
	}
	
	/**
	 * Check if the query is subsumed by the other Query
	 * That is to say: there exists a substitution theta such
	 * that: <code>(other Query) (theta) := thisQuery</code>
	 * @param 	query the query in form of a List
	 * @param	rule the left hand side of a rule
	 * @return  <code>true</code> if the other query subsumes this query
	 * 			<code>false</code> otherwise
	 */
	public static boolean subsume(List<Literal> rule, List<Literal> query) {
		if (rule.size() != query.size())
			return false;
		
		Map<Integer, Integer> theta = new HashMap<Integer, Integer>();
		for (int i = 0; i < rule.size(); i++) {
			if (!rule.get(i).subsume(query.get(i), theta))
				return false;
		}
		
		return true;
	}
	
	/**
	 * Converts a {@link QuerySet} to a {@link WebQuery} object.
	 * @return a {@link WebQuery} object
	 * @see WebQuery
	 */
	public WebQuery webConvert() {
		WebQuery webQuery = new WebQuery(id);
		for (Literal l: litVector) {
			webQuery.addLit(l.webConvert());
		}
		
		Vector<Integer> varList = getAllVars();
		for (int i = 0; i < varList.size(); i++) {
			webQuery.addVar(env.symTab().getSym(varList.get(i)));
		}
		
		webQuery.setSkipped(this.skipped);
		
		return webQuery;
	}
	
	//TODO: remove
	public Vector<Literal> getLitVector() {
		return litVector;
	}
}
