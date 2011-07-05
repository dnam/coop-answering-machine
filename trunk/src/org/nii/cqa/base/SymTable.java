/**
 * Author: Nam Dang
 * Description: Stores the mapping of symbols (predicate, constant, variable) to a unique integer
 * as identifier. This symbol table should be made global with respect to a given knowledge base (KB)
 * and a query Q(X).
 */

package org.nii.cqa.base;

import java.util.HashMap;
import java.util.Map;

public class SymTable { 
	// Private attributes
	private Map<String, Integer> symIdMap; // sym -> id
	private Map<Integer, String> idSymMap; // id -> sym
	private Map<Integer, SymType> idTypeMap; // id -> type
	private int counter; // counter for id
	
	public SymTable() {
		// initialization
		counter = 0;
		
		symIdMap = new HashMap<String, Integer>();
		idSymMap = new HashMap<Integer, String>();
		idTypeMap = new HashMap<Integer, SymType>();
	}
	
	// Reset the symbol tablle
	public void reset() {
		counter = 0;
		symIdMap.clear();
		idSymMap.clear();
		idTypeMap.clear();
	}
	
	// Adds a new symbol to the map
	// returns the symbol's id.
	// if the symbol already exists, returns its current id
	// the matching is case-sensitive
	public int addSymbol(String sym) {
		if (!symIdMap.containsKey(sym)) {
			int id = counter++;
			symIdMap.put(sym, id);
			return id;
		}
		
		return symIdMap.get(sym);
	}
	
	
	// Finds the symbol to the corresponding id
	// if the id does not exist, returns null
	public String getSym(int symId) {
		return idSymMap.get(symId);
	}
	
	
	// Finds the id of a symbol
	// return -1 if there is no matching
	public int getID(String symStr) {
		Integer i = symIdMap.get(symStr);

		return (i == null)? -1: i;
	}
	
	// Finds the type of a symbol
	// returns INVALID is there is no matching
	public SymType getTypeSym(String symStr) {
		// First find the id
		int id = getID(symStr);
		
		return (id == -1)? SymType.INVALID : getTypeID(id);
	}
	
	// Finds the type of an identifier
	// returns INVALID if there is no matching
	public SymType getTypeID(int id) {
		SymType type = idTypeMap.get(id);
		
		return (type == null)? SymType.INVALID : type;
	}
	
	// Checks if the table contains a symbol
	public boolean hasSym(String symStr) {
		return symIdMap.containsKey(symStr);
	}
	
	// Checks if the table contains an identifier
	public boolean hasID(int id) {
		return idSymMap.containsKey(id);
	}
	
	// Remove a symbol
	// Returns true if the symbol exists and is removed
	// Returns false otherwise
	public boolean removeSym(String symStr) {
		return false;
	}
}
