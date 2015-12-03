package org.metaborg.meta.lib.antlr.meta;

import org.antlr.v4.Tool;
import org.antlr.v4.tool.ANTLRMessage;
import org.antlr.v4.tool.ANTLRToolListener;

public class BuildAntlrToolListener implements ANTLRToolListener {
	protected Tool tool;

    public BuildAntlrToolListener(Tool tool) {
        this.tool = tool;
    }
    
	@Override
	public void info(String msg) {
		System.out.println("info: " + msg);
	}

	@Override
	public void error(ANTLRMessage msg) {
		 track(msg);
	}

	@Override
	public void warning(ANTLRMessage msg) {
		track(msg);
	}

	private void track(ANTLRMessage msg) {
        System.out.println(tool.errMgr.getMessageTemplate(msg).render());
    }
}
