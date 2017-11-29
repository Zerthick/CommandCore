package io.github.zerthick.commandcore.service;

import io.github.zerthick.commandcore.antlr.CKValue;
import io.github.zerthick.commandcore.antlr.CKVisitor;
import io.github.zerthick.commandcore.antlr.scope.Scope;
import io.github.zerthick.commandcore.antlr.scope.ScopeBuilder;
import io.github.zerthick.commandskript.CommandSkriptLexer;
import io.github.zerthick.commandskript.CommandSkriptParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;

import java.util.Set;

public class MyCommandSkriptService implements CommandSkriptService {


    @Override
    public void executeSkript(String skriptName, CommandSource commandSource, String[] args, Logger logger) {

    }

    @Override
    public CKValue evaluateExpression(String expression, CommandSource commandSource, Logger logger) {

        CommandSkriptLexer lexer = new CommandSkriptLexer(CharStreams.fromString(expression));
        CommandSkriptParser parser = new CommandSkriptParser(new CommonTokenStream(lexer));

        Scope scope = ScopeBuilder.buildScope(new String[0]);

        CKVisitor visitor = new CKVisitor(scope, commandSource, Sponge.getServer().getConsole(), logger);
        return visitor.visit(parser.expression());
    }

    @Override
    public Set<String> getSkripts() {
        return null;
    }
}
