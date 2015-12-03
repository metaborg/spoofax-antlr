package org.metaborg.meta.lib.antlr.meta;

import org.metaborg.spoofax.meta.core.IBuildStep;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

public class AntlrMetaModule extends AbstractModule {
	@Override
	protected void configure() {
		final Multibinder<IBuildStep> buildSteps = Multibinder.newSetBinder(binder(), IBuildStep.class);
		buildSteps.addBinding().to(AntlrBuildStep.class).in(Singleton.class);
	}
}
