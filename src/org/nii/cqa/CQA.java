/**
 * Author: Maheen Bakhtyar and Nam Dang
 * Org: NII
 */
package org.nii.cqa;

import org.nii.cqa.base.Literal;
import org.nii.cqa.base.SymTable;
import org.nii.cqa.base.SymType;

public class CQA {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//just checking SymTable[remove]
		int id = SymTable.addSymbol("researches", SymType.PREDICATE);
		
		//just checking Literal[remove]
		Literal l = new Literal();
		l.setId(id);
		System.out.println(l.getId() + " " + l.toString());
		
	}

}
