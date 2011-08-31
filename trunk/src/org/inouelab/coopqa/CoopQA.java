/**
 * Author: Maheen Bakhtyar and Nam Dang
 * Org: NII
 */
package org.inouelab.coopqa;

import org.inouelab.coopqa.base.Result;

/**
 * CoopQA terminal interface class
 * @author Nam Dang
 */
public class CoopQA {
	/**
	 * @param args Argument from the shell
	 * @throws Exception if any error occurs
	 */
	public static void main(final String[] args) {
		Env env = new Env(); // a new environment
		Options options = new Options(env);

//		options.printHelp(); // if you need to see the usage
		try {
			String[] testArgs = { "-kb",
					"C:\\Users\\Nam\\workspace\\CQA\\test\\gen_kb.txt",
					"-q",
					"C:\\Users\\Nam\\workspace\\CQA\\test\\gen_query.txt",
					"-d",
					"10"};
			options.init(testArgs);
//			options.init(args);
			
			// The root of the tree
			Result ret = Solver.run(env);
			
			System.out.println(ret.printAll());
			System.out.println("Total SOLAR time: " + ret.getSolarTime() + "s");
		}
		catch (IllegalArgumentException e) {
			System.out.println("ERROR: " + e.getMessage());
			options.printHelp();
		}
		catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
			options.printHelp();
		}

	}

}
