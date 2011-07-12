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
	// Reset the symbol tablle
	public static void reset() {
		newVarCnt = 0;
		predCounter = 0;
		varCounter = GAP;
		constCounter = GAP * 2;
		symIdMap.clear();
		idSymMap.clear();
	}
	
	// Adds a new symbol to the map
	// returns the symbol's id.
	// if the symbol already exists, returns its current id
	// the matching is case-sensitive
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
	
	
	// Finds the symbol to the corresponding id
	// if the id does not exist, returns null
	public static String getSym(int symId) {
		return idSymMap.get(symId);
	}
	
	
	// Finds the id of a symbol
	// return -1 if there is no matching
	public static int getID(String symStr) {
		Integer i = symIdMap.get(symStr);

		return (i == null)? -1: i;
	}
	
	// Finds the type of a symbol
	// returns INVALID is there is no matching
	public static SymType getTypeSym(String symStr) {
		// First find the id
		int id = getID(symStr);
		
		return (id == -1)? SymType.INVALID : getTypeID(id);
	}
	
	// Finds the type of an identifier
	// we do not check if the id exists or not
	// @return SymType
	public static SymType getTypeID(int id) {
		if (id/GAP == 0)
			return SymType.PREDICATE;
		if (id/GAP == 1)
			return SymType.VARIABLE;
		
		return SymType.CONSTANT;
	}
	
	// Checks if the table contains a symbol
	public static boolean hasSym(String symStr) {
		return symIdMap.containsKey(symStr);
	}
	
	// Checks if the table contains an identifier
	public static boolean hasID(int id) {
		return idSymMap.containsKey(id);
	}
	
	// Remove a symbol
	// Returns true if the symbol exists and is removed
	// Returns false otherwise
	public static boolean removeSym(String symStr) {
		return false;
	}
	
	/**
	 * returns a newly generated variable's id
	 * used for AI
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
