package io.github.zerthick.commandcore.antlr.scope;

import io.github.zerthick.commandcore.antlr.scope.resolver.*;
import org.spongepowered.api.command.CommandSource;

import java.util.HashMap;
import java.util.Map;

public class ScopeBuilder {

    public static Scope buildScope(CommandSource source, String[] args) {

        Scope scope = new Scope();
        Map<String, ScopeResolver> resolvers = new HashMap<>();
        resolvers.put("ARGS", new ArgsResolver(args));
        resolvers.put("PLAYER_NAME", new PlayerNameResovler());
        resolvers.put("PLAYER_UUID", new PlayerUUIDResolver());
        resolvers.put("WORLD_NAME", new WorldNameResolver());
        resolvers.put("WORLD_UUID", new WorldUUIDResolver());
        scope.setResolves(resolvers);

        return scope;
    }
}
