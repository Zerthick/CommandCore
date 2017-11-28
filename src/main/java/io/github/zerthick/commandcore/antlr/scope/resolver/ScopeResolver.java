package io.github.zerthick.commandcore.antlr.scope.resolver;

import io.github.zerthick.commandcore.antlr.CKValue;
import org.spongepowered.api.command.CommandSource;

public interface ScopeResolver {

    CKValue resolve(CommandSource source);

}
