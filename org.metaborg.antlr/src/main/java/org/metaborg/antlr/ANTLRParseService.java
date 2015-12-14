package org.metaborg.antlr;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.ATNSimulator;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.metaborg.core.language.ILanguageImpl;
import org.metaborg.core.messages.IMessage;
import org.metaborg.core.resource.IResourceService;
import org.metaborg.core.syntax.IParseService;
import org.metaborg.core.syntax.IParserConfiguration;
import org.metaborg.core.syntax.ParseException;
import org.metaborg.core.syntax.ParseResult;
import org.metaborg.spoofax.core.SpoofaxConstants;
import org.metaborg.spoofax.core.syntax.SyntaxFacet;
import org.metaborg.spoofax.core.terms.ITermFactoryService;
import org.metaborg.util.iterators.Iterables2;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class ANTLRParseService implements IParseService<IStrategoTerm> {
    private IResourceService resourceService;
    private ITermFactoryService termFactoryService;
    ATNSimulator dummy;

    @Inject public ANTLRParseService(IResourceService resourceService, ITermFactoryService termFactoryService) {
        this.resourceService = resourceService;
        this.termFactoryService = termFactoryService;
    }

    @Override public ParseResult<IStrategoTerm> parse(String text, FileObject resource, ILanguageImpl language,
        IParserConfiguration parserConfig) throws ParseException {
        final FileObject location = Iterables.get(language.locations(), 0);
        final FileObject parseJar;
        try {
            parseJar = location.resolveFile(SpoofaxConstants.DIR_INCLUDE).resolveFile("antlr.jar");
        } catch(FileSystemException e) {
            throw new ParseException(resource, language, "Cannot load parse JAR", e);
        }
        final File localParseJar = resourceService.localFile(parseJar);

        final ClassLoader parent = getClass().getClassLoader();
        try(final URLClassLoader classLoader = new URLClassLoader(new URL[] { localParseJar.toURI().toURL() }, parent)) {
            try {
                String name = language.id().id;
                Class<?> parserClass = Class.forName("antlr." + name + "Parser", true, classLoader);
                Class<?> lexerClass = Class.forName("antlr." + name + "Lexer", true, classLoader);

                // Create CharStream
                CharStream inputStream = new ANTLRInputStream(text);

                // Create Lexer from CharStream
                Lexer lexer = (Lexer) lexerClass.getDeclaredConstructor(CharStream.class).newInstance(inputStream);
                lexer.removeErrorListeners();
                ANTLRErrorListener lexerErrorListener = new ANTLRErrorListener(resource);
                lexer.addErrorListener(lexerErrorListener);

                // Create TokenStream from Lexer
                TokenStream tokens = new CommonTokenStream(lexer);

                // Create Parser from TokenStream
                Parser parser = (Parser) parserClass.getDeclaredConstructor(TokenStream.class).newInstance(tokens);
                parser.removeErrorListeners();
                ANTLRErrorListener parserErrorListener = new ANTLRErrorListener(resource);
                parser.addErrorListener(parserErrorListener);

                // Create AST from Parser
                Method parseMethod = parserClass.getMethod(getStartSymbol(language));
                ParseTree tree = (ParseTree) parseMethod.invoke(parser);

                // Turn ParseTree AST into ATerm AST
                ITermFactory termFactory = termFactoryService.get(language);
                ANTLRVisitor visitor = new ANTLRVisitor(termFactory, parser);
                IStrategoTerm ast = visitor.visit(tree);

                List<IMessage> messages = new ArrayList<IMessage>();
                messages.addAll(lexerErrorListener.getMessages());
                messages.addAll(parserErrorListener.getMessages());

                return new ParseResult<IStrategoTerm>(text, ast, resource, messages, 0, language, language,
                    new Object());
            } catch(NoClassDefFoundError e) {
                e.printStackTrace();
            } catch(ClassNotFoundException e) {
                e.printStackTrace();
            } catch(InstantiationException e) {
                e.printStackTrace();
            } catch(IllegalAccessException e) {
                e.printStackTrace();
            } catch(IllegalArgumentException e) {
                e.printStackTrace();
            } catch(InvocationTargetException e) {
                e.printStackTrace();
            } catch(NoSuchMethodException e) {
                e.printStackTrace();
            } catch(SecurityException e) {
                e.printStackTrace();
            }
        } catch(FileSystemException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override public String unparse(IStrategoTerm parsed, ILanguageImpl language) {
        throw new NotImplementedException("Pretty-printing with ANTLR is not supported.");
    }

    @Override public ParseResult<IStrategoTerm> emptyParseResult(FileObject resource, ILanguageImpl language,
        ILanguageImpl dialect) {
        return new ParseResult<IStrategoTerm>("", termFactoryService.getGeneric().makeTuple(), resource,
            Iterables2.<IMessage>empty(), -1, language, dialect, null);
    }

    /**
     * Gets the first start symbol for the given language
     * 
     * @param language
     * @return
     */
    protected String getStartSymbol(ILanguageImpl language) {
        Iterable<String> startSymbols = language.facet(SyntaxFacet.class).startSymbols;

        return startSymbols.iterator().next();
    }
}
