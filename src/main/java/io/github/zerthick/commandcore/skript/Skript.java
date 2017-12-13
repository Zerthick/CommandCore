/*
 * Copyright (C) 2017  Zerthick
 *
 * This file is part of CommandCore.
 *
 * CommandCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * CommandCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CommandCore.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.zerthick.commandcore.skript;

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

import java.io.IOException;
import java.nio.file.Path;

public class Skript {

    private Path path;
    private CommandSkriptParser parser;

    public Skript(Path filePath) {

        this.path = filePath;
        try {
            CommandSkriptLexer lexer = new CommandSkriptLexer(CharStreams.fromPath(filePath));
            parser = new CommandSkriptParser(new CommonTokenStream(lexer));
        } catch (IOException ignore) {
            //Ignore
        }
    }

    public void execute(CommandSource commandSource, String[] args, Logger logger) {

        Scope scope = ScopeBuilder.buildScope(commandSource, args);

        CKVisitor visitor = new CKVisitor(scope, commandSource, Sponge.getServer().getConsole(), logger);
        visitor.visit(parser.script());
    }

    public String getName() {
        return path.getFileName().toString();
    }
}
