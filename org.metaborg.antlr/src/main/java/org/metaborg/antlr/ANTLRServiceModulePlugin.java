package org.metaborg.antlr;

import org.metaborg.core.plugin.IServiceModulePlugin;
import org.metaborg.util.iterators.Iterables2;

import com.google.inject.Module;

public class ANTLRServiceModulePlugin implements IServiceModulePlugin {
    @Override public Iterable<Module> modules() {
        return Iterables2.<Module>singleton(new ANTLRModule());
    }
}
