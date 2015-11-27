package org.metaborg.meta.lib.antlr;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.antlr.v4.Tool;
import org.antlr.v4.runtime.misc.Utils;
import org.metaborg.spoofax.meta.core.IBuildStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AntlrBuildStep implements IBuildStep {
	private static final Logger logger = LoggerFactory.getLogger(AntlrBuildStep.class);
	
	/**
	 * Invoke the ANTLR parser generator.
	 */
	@Override
	public void build() {
		generateAntlrParser();
		compileParser();
	}

	/**
	 * Generate Java parser from ANTLR grammar
	 */
	protected void generateAntlrParser() {
		List<String> args = getArgs();
		
		logger.debug("Invoking ANTLR with args: " + Utils.join(args.iterator(), " "));
		
		Tool tool = new Tool(listToArray(args));
        tool.addListener(new BuildAntlrToolListener(tool));
        tool.processGrammarsOnCommandLine();
        
        if (tool.getNumErrors() > 0) {
        	// TODO: handle error!
        }
	}

	/**
	 * Compile ANTLR-generated Java parser
	 */
	protected void compileParser() {
        // Get the java files
        File dir = new File("/Users/martijn/Documents/runtime-Spoofax/Test/target/Test/syntax/");
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".java");
            }
        });
        
        // Compile
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(files);
        String classpath = "/Users/martijn/.m2/repository/org/antlr/antlr4/4.5.1/antlr4-4.5.1.jar"; // TODO: Get antlr from somewhere. Only think on our classpath is: /Users/martijn/Eclipse/spoofax-dev/plugins/org.eclipse.equinox.launcher_1.3.0.v20140415-2008.jar
        Iterable<String> options = Arrays.asList("-classpath", classpath);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits);
        boolean success = task.call();
        
        if (!success) {
        	logger.debug(Arrays.toString(diagnostics.getDiagnostics().toArray()));
        } else {
        	logger.debug("Compiled ANTLR-generated parser");
        }
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
