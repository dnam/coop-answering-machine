/**
 * Description: Stores the mapping of symbols (predicate, constant, variable) to a unique integer
 * as identifier. This symbol table should be made global with respect to a given knowledge base (KB)
 * and a query Q(X).
 * @author: Nam Dang
 */

package org.nii.cqa.base;

import java.util.HashMap;
import java.util.Map;

public class SymTable { 
	// each type's id is seperated by GAP
	// predicate: 0-99,999, variable: 100,000-199,999, constant: 200,000~
	private static final int GAP = 100000; 
	private static final Map<String, Integer> symIdMap = new HashMap<String, Integer>(); // sym -> id
	private static final Map<Integer, String> idSymMap = new HashMap<Integer, String>(); // id -> sym
	private static int predCounter = 1;
	private static int varCounter  = GAP; // counter for id
	private static int constCounter = GAP*2;
	private static int newVarCnt = 0; // for generating new variable
	private static int queryCounter = 0; // counter for query
	private static int ansCounter = 0; // counter to prevent duplication of ans predicate
	
	/**
	 * resets the symbol tablle
	 */
	public static void reset() {
		newVarCnt = 0;
		predCounter = 0;
		varCounter = GAP;
		constCounter = GAP * 2;
		queryCounter = 0;
		ansCounter = 0;
		symIdMap.clear();
		idSymMap.clear();
	}
	
	/**
	 * if the symbol already exists, returns its current id
	 * the matching is case-sensitive
	 * @param sym a new symbol
	 * @param type of the new symbol
	 * @return the id of the symbol if succeed
	 */
	public static int addSymbol(String sym, SymType type) {
		if (!symIdMap.containsKey(sym)) {
			int id;
			if (type == SymType.VARIABLE)
				id = varCounter++;
			else if (type == SymType.CONSTANT)
				id = constCounter++;
			else
				id = predCounter++;
			
			symIdMap.put(sym, id);
			idSymMap.put(id, sym);
			return id;
		}
		
		return symIdMap.get(sym);
	}
	
	
	/**
	 * Finds the symbol to the corresponding id
	 * @param symId the id to lookup
	 * @return the string corresponding to symID
	 * 			null if the id doesn't exist
	 */
	public static String getSym(int symId) {
		return idSymMap.get(symId);
	}
	
	
	/**
	 * Finds the id of a symbol
	 * @param symStr the string of the symbol
	 * @return the id if found
	 * 			-1 otherwise
	 */
	public static int getID(String symStr) {
		Integer i = symIdMap.get(symStr);

		return (i == null)? -1: i;
	}
	
	/**
	 * Finds the type of a symbol
	 * @param symStr the symbol
	 * @return the type of the symbol
	 * 			INVALID if not found
	 */
	public static SymType getTypeSym(String symStr) {
		int id = getID(symStr);
		
		return (id == -1)? SymType.INVALID : getTypeID(id);
	}
	
	/** 
	 * Finds the type of an identifier
	 * we do not check if the id exists or not
	 * @param id the id to look up
 	 * @return SymType the type of the id
 	 */
	public static SymType getTypeID(int id) {
		if (id/GAP == 0)
			return SymType.PREDICATE;
		if (id/GAP == 1)
			return SymType.VARIABLE;
		
		return SymType.CONSTANT;
	}
	
	/**
	 * Checks if the table contains a symbol
	 * @param symStr the symbol
	 * @return true if the table contains the symbol
	 * 			false otherwise
	 */
	public static boolean hasSym(String symStr) {
		return symIdMap.containsKey(symStr);
	}
	
	/**
	 * Checks if the table contains an identifier
	 * @param id the identifier
	 * @return true if id exists, false otherwise
	 */
	public static boolean hasID(int id) {
		return idSymMap.containsKey(id);
	}

	/**
	 * Generates a unique id for queries
	 * @return the newly generated id
	 */
	public static int getQueryID() {
		return queryCounter++;
	}
	
	/**
	 * Generates a new variable
	 * @return a newly generated variable's id
	 */
	public static int generateVar() {
		String newVarStr = null;
	
		do {
			newVarStr= "V_" + newVarCnt++;
			// assume that no overflow occurs
		}
		while(symIdMap.containsKey(newVarStr));
		
		return addSymbol(newVarStr, SymType.VARIABLE);
	}
}
