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
        return isNull() ? "NULL" : isVoid() ? "VOID" : String.valueOf(value);
    }
}
