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
