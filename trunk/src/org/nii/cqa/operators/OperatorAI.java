package org.nii.cqa.operators;
/**
 * @author Nam Dang, Maheen Bakhtyar
 * A class with only one public method that performs AI operation
 * upon a given set of Queries
 */

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.nii.cqa.base.*;

class OperatorAI extends Operator {
	@Override
	Set<Query> perform(Query query) {
		Set<Query> retSet = new HashSet<Query>();
		Iterator<Integer> idIt = query.extractSet().iterator();
		
		// Generate a new variable to replace
		// should be different each time this method is called
		int newVarID = SymTable.generateVar();
		while(idIt.hasNext()) {
			int id = idIt.next();
			retSet.addAll(query.replace(id, newVarID));
		}
		
		return retSet;
	}
}
