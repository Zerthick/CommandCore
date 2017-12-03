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

package io.github.zerthick.commandcore.antlr;

import java.util.List;

public class CKValue implements Comparable<CKValue> {

    public static final CKValue NULL = new CKValue();
    public static final CKValue VOID = new CKValue();

    private Object value;

    //Private constructor only for NULL and VOID
    private CKValue() {
        value = new Object();
    }

    public CKValue(Object v) {
        if(v == null) {
            throw new RuntimeException("Value is null");
        }
        value = v;
        // Check that value is a valid data type
        if(!(isBoolean() || isList() || isNumber() || isString())) {
            throw new RuntimeException("Invalid data type: " + v + " (" + v.getClass() + ")");
        }
    }

    public Boolean asBoolean() {
        return (Boolean)value;
    }

    public Double asDouble() {
        return ((Number)value).doubleValue();
    }

    @SuppressWarnings("unchecked")
    public List<CKValue> asList() {
        return (List<CKValue>)value;
    }

    public String asString() {
        return (String)value;
    }

    @Override
    public int compareTo(CKValue o) {
        if(this.isNumber() && o.isNumber()) {
            if(this.equals(o)) {
                return 0;
            }
            else {
                return this.asDouble().compareTo(o.asDouble());
            }
        }
        else if(this.isString() && o.isString()) {
            return this.asString().compareTo(o.asString());
        }
        else {
            throw new RuntimeException("Illegal expression: can't compare `" + this + "` to `" + o + "`");
        }
    }

    @Override
    public boolean equals(Object o) {
        if(this == VOID || o == VOID) {
            throw new RuntimeException("Can't use VOID: " + this + " ==/!= " + o);
        }
        if(this == o) {
            return true;
        }
        if(o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CKValue that = (CKValue) o;
        if(this.isNumber() && that.isNumber()) {
            double diff = Math.abs(this.asDouble() - that.asDouble());
            return diff < 0.00000000001;
        }
        else {
            return this.value.equals(that.value);
        }
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public boolean isBoolean() {
        return value instanceof Boolean;
    }

    public boolean isNumber() {
        return value instanceof Number;
    }

    public boolean isList() {
        return value instanceof List<?>;
    }

    public boolean isNull() {
        return this == NULL;
    }

    public boolean isVoid() {
        return this == VOID;
    }

    public boolean isString() {
        return value instanceof String;
    }

    @Override
    public String toString() {

        if (isNull()) {
            return "NULL";
        }

        if (isVoid()) {
            return "VOID";
        }

        if (isList()) {
            return asList().toString();
        }

        if (isNumber()) {

            Double val = asDouble();

            // Value is an int
            if (val % 1 == 0) {
                return String.valueOf(val.intValue());
            }

            return String.valueOf(val);
        }

        return String.valueOf(value);
    }
}
