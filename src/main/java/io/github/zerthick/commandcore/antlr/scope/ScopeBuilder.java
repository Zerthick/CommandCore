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
