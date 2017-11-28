package io.github.zerthick.commandcore.antlr;

import org.antlr.v4.runtime.ParserRuleContext;

public class CKEvalException extends RuntimeException {

    public CKEvalException(ParserRuleContext ctx) {
        this("Illegal expression", ctx);
    }

    public CKEvalException(String msg, ParserRuleContext ctx) {
        super(msg + ": " + ctx.getText() + "    line:" + ctx.start.getLine());
    }
}
