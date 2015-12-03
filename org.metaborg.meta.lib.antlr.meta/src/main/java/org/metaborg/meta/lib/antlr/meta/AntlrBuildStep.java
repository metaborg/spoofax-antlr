package org.metaborg.meta.lib.antlr.meta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.Tool;
import org.antlr.v4.runtime.misc.Utils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.metaborg.core.resource.IResourceService;
import org.metaborg.spoofax.core.SpoofaxConstants;
import org.metaborg.spoofax.meta.core.IBuildStep;
import org.metaborg.spoofax.meta.core.MetaBuildInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class AntlrBuildStep implements IBuildStep {
	private static final Logger logger = LoggerFactory.getLogger(AntlrBuildStep.class);
	private static final String PARSER_JAR = "antlr.jar";
	private static final String JAVA_ANTLR_DIR = "antlr";
	private static final String CLASS_DIR = "target/classes";
	private static final String CLASS_ANTLR_DIR = CLASS_DIR + "/antlr";
	
	private IResourceService resourceService;
	
	@Inject public AntlrBuildStep(IResourceService resourceService) {
		this.resourceService = resourceService;
	}
	
	/**
	 * Pre-java compilation will invoke the ANTLR parser generator to generate a Java parser.
	 */
	@Override
	public void compilePreJava(MetaBuildInput input) {
		List<String> args = getArgs(input);
		
		logger.debug("Invoking ANTLR with args: " + Utils.join(args.iterator(), " "));
		
		Tool tool = new Tool(listToArray(args));
		tool.addListener(new BuildAntlrToolListener(tool));
		tool.processGrammarsOnCommandLine();
	}
	
	/**
	 * Post-java compilation will jar the generated class files for the ANTLR-generated parser.
	 */
	@Override
	public void compilePostJava(MetaBuildInput input) {
		FileObject location = input.project.location();
		
		try {
			location.resolveFile(SpoofaxConstants.DIR_INCLUDE + "/" + PARSER_JAR).delete();
		} catch (FileSystemException e) {
			e.printStackTrace();
		}
		
		try {
			FileObject base = location.resolveFile(CLASS_DIR);
			FileObject source = location.resolveFile(CLASS_ANTLR_DIR);
			FileObject destination = location.resolveFile(SpoofaxConstants.DIR_INCLUDE + "/" + PARSER_JAR);
			
			JarBuilder jarBuilder = new JarBuilder();
			jarBuilder.jar(source, destination, base);
		} catch (FileSystemException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the list of arguments to invoke ANTLR with.
	 * 
	 * @return
	 * @throws FileSystemException 
	 */
	protected List<String> getArgs(MetaBuildInput input) {
		List<String> args = new ArrayList<String>();
		
		try {
			String name = input.settings.settings().name();
			File grammar = resourceService.localFile(input.settings.getSyntaxDirectory().resolveFile(name + ".g4"));
			File javaDirectory = resourceService.localFile(input.settings.getJavaDirectory());
			
			args.add(grammar.getAbsolutePath());
			args.add("-o");
			args.add(javaDirectory.getAbsolutePath() + "/" + JAVA_ANTLR_DIR);
			args.add("-package");
			args.add("antlr");
		} catch (FileSystemException e) {
			e.printStackTrace();
		}
		
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
