package io.github.zerthick.commandcore.antlr.scope;

import io.github.zerthick.commandcore.antlr.scope.resolver.*;

import java.util.HashMap;
import java.util.Map;

public class ScopeBuilder {

    private static PlayerNameResovler playerNameResovler = new PlayerNameResovler();
    private static PlayerUUIDResolver playerUUIDResolver = new PlayerUUIDResolver();
    private static WorldNameResolver worldNameResolver = new WorldNameResolver();
    private static WorldUUIDResolver worldUUIDResolver = new WorldUUIDResolver();

    public static Scope buildScope(String[] args) {

        Scope scope = new Scope();
        Map<String, ScopeResolver> resolvers = new HashMap<>();
        resolvers.put("ARGS", new ArgsResolver(args));
        resolvers.put("PLAYER_NAME", playerNameResovler);
        resolvers.put("PLAYER_UUID", playerUUIDResolver);
        resolvers.put("WORLD_NAME", worldNameResolver);
        resolvers.put("WORLD_UUID", worldUUIDResolver);
        scope.setResolves(resolvers);

        return scope;
    }
}
