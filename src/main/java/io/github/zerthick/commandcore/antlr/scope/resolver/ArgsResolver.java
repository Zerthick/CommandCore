package io.github.zerthick.commandcore.antlr.scope.resolver;

import io.github.zerthick.commandcore.antlr.CKValue;
import org.spongepowered.api.command.CommandSource;

import java.util.ArrayList;
import java.util.List;

public class ArgsResolver implements ScopeResolver {

    private CKValue value;

    public ArgsResolver(String[] args) {
        List<CKValue> argsList = new ArrayList<>();
        for (String arg : args) {
            argsList.add(new CKValue(arg));
        }
        value = new CKValue(argsList);
    }

    @Override
    public CKValue resolve(CommandSource source) {
        return value;
    }
}
