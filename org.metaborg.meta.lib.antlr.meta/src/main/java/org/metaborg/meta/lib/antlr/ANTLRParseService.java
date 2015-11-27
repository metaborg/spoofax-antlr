package org.metaborg.meta.lib.antlr;

import org.apache.commons.vfs2.FileObject;
import org.metaborg.core.language.ILanguageImpl;
import org.metaborg.core.syntax.IParseService;
import org.metaborg.core.syntax.IParserConfiguration;
import org.metaborg.core.syntax.ParseException;
import org.metaborg.core.syntax.ParseResult;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class ANTLRParseService implements IParseService<IStrategoTerm> {
	@Override
	public ParseResult<IStrategoTerm> parse(String text, FileObject resource, ILanguageImpl language, IParserConfiguration parserConfig) throws ParseException {
		// TODO Invoke ANTLR-generated parser (ExprParser) on text. The code below, but all dynamic (i.e. dynamic classes, dynamic methods..)
		
//		try {
//			URL url = new URL("file:/Users/martijn/Documents/runtime-Spoofax/Test/target/Test/syntax/");
			
			//ANTLRInputStream inputStream = new ANTLRInputStream(text);
			
//			URLClassLoader classLoader = new URLClassLoader(new URL[]{url}, ANTLRParseService.class.getClassLoader());
			
//			try {
				// TODO: This won't work since we're missing org/antlr/v4/runtime/Lexer (i.e. ANTLR runtime) dependency
				//Class<?> clazz = Class.forName("ExprLexer", true, classLoader);
				
				// clazz.newInstance(); // TODO: THis won't work, since constructor needs CharStream as parameter
				//Lexer lexer = (Lexer) clazz.getDeclaredConstructor(CharStream.class).newInstance(inputStream);
//			} catch (ClassNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (InstantiationException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				try {
//					classLoader.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		} catch (MalformedURLException e1) {
//			e1.printStackTrace();
//		}

		// 
		// Located in /Test/target/Test/syntax/ are ExprLexer, ExprParser, and ExprListener.
		// 
		// --
		//
        // Create a CharStream from the source text
        // ANTLRInputStream inputStream = new ANTLRInputStream(text);
		//
		// Create a lexer that feeds off of input CharStream
        // Lexer lexer = new ExprLexer(inputStream);
		//
		// Create a buffer of tokens pulled from the lexer
		// CommonTokenStream tokens = new CommonTokenStream(lexer);
		//
		// Create a parser that feeds off the tokens buffer
		// ExprParser parser = new ExprParserParser(tokens);
		//
		// Begin parsing at init rule
		// ParseTree tree = parser.prog();
		// 
		// Print LISP-style tree
		// System.out.println(tree.toStringTree(parser));
		//
		// TODO Turn ANTLR AST into ATerm AST
		
		return null;
	}

	@Override
	public String unparse(IStrategoTerm parsed, ILanguageImpl language) {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public ParseResult<IStrategoTerm> emptyParseResult(FileObject resource, ILanguageImpl language, ILanguageImpl dialect) {
		// TODO Auto-generated method stub
		
		return null;
	}
}
