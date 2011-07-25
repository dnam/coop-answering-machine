package org.nii.cqa.operators;
/**
 * @author Nam Dang, Maheen Bakhtyar
 * A class with only one public method that performs AI operation
 * upon a given set of Queries
 */

import java.util.Iterator;
import org.nii.cqa.base.*;

class OperatorAI extends Operator {
	@Override
	QuerySet perform(Query query) {
		QuerySet retSet = new QuerySet();
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

	@Override
	int getType() {
		return 1;
	}
}
