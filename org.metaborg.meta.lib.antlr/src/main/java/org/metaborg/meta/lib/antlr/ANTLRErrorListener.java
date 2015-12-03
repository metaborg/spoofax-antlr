package org.metaborg.meta.lib.antlr;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.apache.commons.vfs2.FileObject;
import org.metaborg.core.messages.IMessage;
import org.metaborg.core.messages.Message;
import org.metaborg.core.messages.MessageSeverity;
import org.metaborg.core.messages.MessageType;
import org.metaborg.core.source.ISourceRegion;
import org.metaborg.core.source.SourceRegion;

class ANTLRErrorListener extends BaseErrorListener {
	private FileObject source;
	private List<IMessage> messages;
	
	public ANTLRErrorListener(FileObject source) {
		this.source = source;
		this.messages = new ArrayList<IMessage>();
	}
	
	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
		int endOffset = charPositionInLine + length(offendingSymbol);
		
		ISourceRegion region = new SourceRegion(
				charPositionInLine,
				line,
				charPositionInLine,
				endOffset,
				line,
				endOffset
		);
		
		IMessage message = new Message(msg, MessageSeverity.ERROR, MessageType.PARSER, source, region, e);
		
		messages.add(message);
	}
	
	/**
	 * Get all error messages registered by this listener.
	 * 
	 * @return
	 */
	public List<IMessage> getMessages() {
		return messages;
	}
	
	/**
	 * Computes the length of the offending symbol. If the offending symbol is
	 * not a token or of it spans multiple lines 0 is returned.
	 * 
	 * @return
	 */
	protected int length(Object offendingSymbol) {
		if (offendingSymbol instanceof Token) {
			Token offendingToken = (Token) offendingSymbol;
			int start = offendingToken.getStartIndex();
			int stop = offendingToken.getStopIndex();
			
			if (start >= 0 && stop >= 0) {
				return stop-start;
			}
		}
		
		return 0;
	}
}