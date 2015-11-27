package org.metaborg.meta.lib.antlr;

import org.metaborg.core.syntax.IParseService;
import org.metaborg.spoofax.meta.core.IBuildStep;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

public class AntlrModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(IBuildStep.class).to(AntlrBuildStep.class).in(Singleton.class);
		
        final MapBinder<String, IParseService<IStrategoTerm>> parsers = MapBinder.newMapBinder(binder(), new TypeLiteral<String>() {}, new TypeLiteral<IParseService<IStrategoTerm>>() {});
        parsers.addBinding("antlr").to(ANTLRParseService.class).in(Singleton.class);
	}
}
