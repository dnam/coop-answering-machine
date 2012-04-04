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
					"C:\\Users\\namd\\Desktop\\Eclipse-COOPQA\\Test\\gen_kb.txt",
					"-q",
					"C:\\Users\\namd\\Desktop\\Eclipse-COOPQA\\Test\\gen_query.txt",
					"-solar",
					"C:\\Users\\namd\\Desktop\\Eclipse-COOPQA\\Test\\solar2-build310.jar"};
			options.init(testArgs);
//			options.init(args);
			
			// The root of the tree
			Result ret = GenOp.run(env);
			//Result ret = MultithreadGenOp.run(env);
			
			System.out.print(ret.printAll());
			System.out.println("Total SOLAR time: " + ret.getSolarTime() + "s");
			System.out.println("AI time: " + env.op().AI.getTime());
			System.out.println("DC time: " + env.op().DC.getTime());
			System.out.println("GR time: " + env.op().GR.getTime());
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			System.out.println("ERROR: " + e.getMessage());
			options.printHelp();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR: " + e.getMessage());
//			options.printHelp();
		}

	}

}
