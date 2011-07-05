/**
 * Author: Maheen Bakhtyar and Nam Dang
 * Org: NII
 */
package org.nii.cqa;

import org.nii.cqa.base.Literal;
import org.nii.cqa.base.SymTable;

public class CQA {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//just checking SymTable[remove]
		SymTable.addSymbol("Researches");
		SymTable.addSymbol("Study");
		
		//just checking Literal[remove]
		Literal l = new Literal();
		
		System.out.println(l.getId() + " " + l.toString());
		
	}

}
