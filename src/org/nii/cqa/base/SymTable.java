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
	// Private attributes
	private static final Map<String, Integer> symIdMap = new HashMap<String, Integer>(); // sym -> id
	private static final Map<Integer, String> idSymMap = new HashMap<Integer, String>(); // id -> sym
	private static final Map<Integer, SymType> idTypeMap = new HashMap<Integer, SymType>(); // id -> type
	private static int counter  = 0; // counter for id
	
	// Reset the symbol tablle
	public static void reset() {
		counter = 0;
		symIdMap.clear();
		idSymMap.clear();
		idTypeMap.clear();
	}
	
	// Adds a new symbol to the map
	// returns the symbol's id.
	// if the symbol already exists, returns its current id
	// the matching is case-sensitive
	public static int addSymbol(String sym, SymType type) {
		if (!symIdMap.containsKey(sym)) {
			int id = counter++;
			symIdMap.put(sym, id);
			idSymMap.put(id, sym);
			idTypeMap.put(id, type);
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
	// returns INVALID if there is no matching
	public static SymType getTypeID(int id) {
		SymType type = idTypeMap.get(id);
		
		return (type == null)? SymType.INVALID : type;
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
}
