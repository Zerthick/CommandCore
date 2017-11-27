package io.github.zerthick.commandcore.service;

import io.github.zerthick.commandcore.antlr.CKValue;
import org.spongepowered.api.command.CommandSource;

import java.util.Set;

public interface CommandSkriptService {

    void executeSkript(String skriptName, CommandSource commandSource, String[] args);

    CKValue evaluateExpression(String expression, CommandSource commandSource, String[] args);

    Set<String> getSkripts();
}
