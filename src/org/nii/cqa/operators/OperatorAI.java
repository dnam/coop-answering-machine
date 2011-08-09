package org.nii.cqa.operators;
/**
 * @author Nam Dang, Maheen Bakhtyar
 * A class with only one public method that performs AI operation
 * upon a given set of Queries
 */

import java.util.Iterator;
import org.nii.cqa.base.*;

class OperatorAI extends Operator {
	protected OperatorAI(CoopQAJob job) {
		super(false, job);
	}
	
	@Override
	QuerySet perform(Query query) {
		QuerySet retSet = new QuerySet();
		
		if (query.isSkipped())
			return retSet;
		
		Iterator<Integer> idIt = query.extractSet().iterator();
		
		// Generate a new variable to replace
		// should be different each time this method is called
		int newVarID = job.symTab().generateVar();
		while(idIt.hasNext()) {
			int id = idIt.next();
			Iterator<Query> itQu = query.replace(id, newVarID).iterator();
			while (itQu.hasNext()) {
				Query q = itQu.next();
				if (!globalSet.contains(q)) { // if previously generated
					retSet.add(q);
					globalSet.add(q);
				}
				else if (!retSet.contains(q)) { // do not add twice
					q.setSkipped(true);
					retSet.add(q);
				}
			}
		}
		
		return retSet;
	}

	@Override
	public int getType() {
		return AI_t;
	}
}
