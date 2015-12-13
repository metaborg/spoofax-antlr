package org.metaborg.antlr;

import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Trees;
import org.apache.commons.lang3.StringUtils;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

class ANTLRVisitor extends AbstractParseTreeVisitor<IStrategoTerm> {
    private final ITermFactory termFactory;
    private final List<String> ruleNames;


    public ANTLRVisitor(ITermFactory termFactory, Parser parser) {
        this.termFactory = termFactory;
        this.ruleNames = Arrays.asList(parser.getRuleNames());
    }


    @Override public IStrategoTerm visit(ParseTree tree) {
        return super.visit(tree);
    }

    @Override public IStrategoTerm visitChildren(RuleNode node) {
        IStrategoConstructor constructor = makeConstructor(node);

        IStrategoTerm[] childTerms = new IStrategoTerm[node.getChildCount()];

        for(int i = 0; i < node.getChildCount(); i++) {
            childTerms[i] = visit(node.getChild(i));
        }

        return termFactory.makeAppl(constructor, childTerms);
    }

    @Override public IStrategoTerm visitErrorNode(ErrorNode node) {
        System.out.println(node);
        System.out.println(node.getClass());

        return super.visitErrorNode(node);
    }

    @Override public IStrategoTerm visitTerminal(TerminalNode node) {
        return termFactory.makeString(node.getText());
    }


    /**
     * Create a stratego constructor for the given rule node.
     * 
     * @param node
     * @return
     */
    protected IStrategoConstructor makeConstructor(RuleNode node) {
        final IStrategoConstructor constructor =
            termFactory.makeConstructor(StringUtils.capitalize(Trees.getNodeText(node, ruleNames)),
                node.getChildCount());

        return constructor;
    }
}