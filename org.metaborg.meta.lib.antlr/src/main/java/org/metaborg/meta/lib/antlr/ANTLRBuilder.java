package org.metaborg.meta.lib.antlr;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.Tool;
import org.antlr.v4.runtime.misc.Utils;
import org.metaborg.spoofax.meta.core.IBuildStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ANTLRBuilder implements IBuildStep {
	private static final Logger logger = LoggerFactory.getLogger(ANTLRBuilder.class);
	
	/**
	 * Invoke the ANTLR parser generator.
	 */
	@Override
	public void build() {
		List<String> args = getArgs();
		
		logger.debug("Invoking ANTLR with args: " + Utils.join(args.iterator(), " "));
		
		Tool tool = new Tool(listToArray(args));
        tool.addListener(new BuildANTLRToolListener(tool));
        tool.processGrammarsOnCommandLine();
        
        if (tool.getNumErrors() > 0) {
        	// TODO: handle error!
        }
        
        //JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        
        //int compilationResult = compiler.run(null, null, null, "Test/target/Test/syntax/ExprParser.java");

        //if (compilationResult > 0) {
        	// TODO: Handle error!
        //}
	}
	
	/**
	 * Get list of arguments to invoke ANTLR with.
	 * 
	 * @return
	 */
	protected List<String> getArgs() {
		List<String> args = new ArrayList<String>();
		
		// TODO: Find grammar file dynamically (e.g. using language start symbol? how about multiple files?)
		// and make path relative to project root.
		
		args.add("Test/syntax/Expr.g4");
		args.add("-o");
		args.add("Test/target");
		
		return args;
	}
	
	/**
	 * Turn a argument list of strings into an argument array of strings.
	 *  
	 * @param args
	 * @return
	 */
	protected static String[] listToArray(List<String> args) {
		return args.toArray(new String[args.size()]);
	}
}
