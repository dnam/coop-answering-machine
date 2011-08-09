package org.nii.cqa.operators;

import org.nii.cqa.base.CoopQAJob;
import org.nii.cqa.base.Query;
import org.nii.cqa.base.QuerySet;

class OperatorWrapper extends Operator {

	protected OperatorWrapper(CoopQAJob job) {
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
