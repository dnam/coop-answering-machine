/**
 * Author: Maheen Bakhtyar and Nam Dang
 * Org: NII
 */
package org.inouelab.coopqa;

import org.inouelab.coopqa.base.Result;

public class CoopQA {
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) {
		Env env = new Env(); // a new environment
		Options options = new Options(env);

		options.printHelp(); // if you need to see the usage
		try {
			String[] testArgs = { "-kb",
					"C:\\Users\\Nam\\workspace\\CQA\\lib\\gen_kb.txt", 
					"-q",
					"C:\\Users\\Nam\\workspace\\CQA\\lib\\gen_query.txt" };
			options.init(testArgs);
//			options.init(args);

			// options.initWithInputs("C:\\Users\\Nam\\workspace\\CQA\\lib\\gen_kb.txt","C:\\Users\\Nam\\workspace\\CQA\\lib\\gen_query.txt");

			// The root of the tree
			Result ret = Solver.run(env);

			System.out.println(ret.printAll());
		}
		catch (IllegalArgumentException e) {
			System.out.println("ERROR:");
			System.out.println(e.getMessage());
			options.printHelp();
			
		}
		catch (Exception e) {
			System.out.println("ERROR:");
			e.printStackTrace();
			options.printHelp();
		}

	}

}
