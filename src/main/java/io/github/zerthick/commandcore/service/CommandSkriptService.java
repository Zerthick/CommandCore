package io.github.zerthick.commandcore.service;

import io.github.zerthick.commandcore.antlr.CKValue;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandSource;

import java.util.Set;

public interface CommandSkriptService {

    void executeSkript(String skriptName, CommandSource commandSource, String[] args, Logger logger);

    CKValue evaluateExpression(String expression, CommandSource commandSource, Logger logger);

    Set<String> getSkripts();
}
