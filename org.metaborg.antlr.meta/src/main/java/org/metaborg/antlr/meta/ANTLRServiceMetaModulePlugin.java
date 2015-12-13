package org.metaborg.antlr.meta;

import org.metaborg.meta.core.plugin.IServiceMetaModulePlugin;
import org.metaborg.util.iterators.Iterables2;

import com.google.inject.Module;

public class ANTLRServiceMetaModulePlugin implements IServiceMetaModulePlugin {
    @Override public Iterable<Module> modules() {
        return Iterables2.<Module>singleton(new ANTLRMetaModule());
    }
}
