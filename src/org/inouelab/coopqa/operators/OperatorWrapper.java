package org.inouelab.coopqa.operators;

import org.inouelab.coopqa.Env;
import org.inouelab.coopqa.base.Query;
import org.inouelab.coopqa.base.QuerySet;

class OperatorWrapper extends Operator {

	protected OperatorWrapper(Env job) {
		super(true, job);
	}

	@Override
	QuerySet perform(Query query) {
		return null;
	}

	@Override
	public int getType() {
		return -1;
	}

}
