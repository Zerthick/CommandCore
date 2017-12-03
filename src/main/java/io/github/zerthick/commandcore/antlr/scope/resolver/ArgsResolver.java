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
