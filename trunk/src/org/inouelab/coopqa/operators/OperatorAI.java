package org.inouelab.coopqa.operators;
/**
 * @author Nam Dang, Maheen Bakhtyar
 * A class with only one public method that performs AI operation
 * upon a given set of Queries
 */

import java.util.Iterator;

import org.inouelab.coopqa.Env;
import org.inouelab.coopqa.SemanticRelThreshold;
import org.inouelab.coopqa.base.*;

/**
 * Anti-instantiation Operator
 * @author Nam Dang
 *
 */
final class OperatorAI extends Operator {
	protected OperatorAI(Env job) {
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
		int newVarID = env.symTab().generateVar();
		while(idIt.hasNext()) {
			int id = idIt.next();
			Iterator<Query> itQu = query.replace(id, newVarID).iterator();
			while (itQu.hasNext()) {
				Query newQuery = itQu.next();
				
				if (SemanticRelThreshold.enable && newQuery.isFiltered())
					continue;
				
				if (globalSet.add(newQuery)) { // new query
					retSet.add(newQuery);
				}
				else { // do not add twice
					newQuery.setSkipped(true);
					retSet.add(newQuery);
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
