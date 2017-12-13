package io.github.zerthick.commandcore.antlr.scope.resolver;

import io.github.zerthick.commandcore.antlr.CKValue;
import org.spongepowered.api.command.CommandSource;

public class ValueResolver implements ScopeResolver {

    private CKValue value;

    public ValueResolver(Object value) {
        if (value instanceof Boolean || value instanceof Number || value instanceof String) {
            this.value = new CKValue(value);
        } else {
            this.value = new CKValue(value.toString());
        }
    }

    @Override
    public CKValue resolve(CommandSource source) {
        return value;
    }
}
