package io.github.zerthick.commandcore.antlr.scope;

import io.github.zerthick.commandcore.antlr.CKValue;
import io.github.zerthick.commandcore.antlr.scope.resolver.ScopeResolver;
import org.spongepowered.api.command.CommandSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Scope {

    private Scope parent;

    private Map<String, CKValue> variables;
    private Map<String, CKValue> constants;
    private Map<String, ScopeResolver> resolves;

    public Scope() {
        // constructor for global scope, parent is null
        this(null);
    }

    public Scope(Scope parent) {
        this.parent = parent;
        variables = new HashMap<>();
        constants = new HashMap<>();
    }

    public void assignVariable(String name, CKValue value) {
        if(resolveVariable(name).isPresent()) {
            reAssignVariable(name, value);
        } else {
            variables.put(name, value);
        }
    }

    public void assignConstant(String name, CKValue value) {
        if(resolveConstant(name).isPresent()) {
            throw new RuntimeException("Cannot reassign constant " + name);
        } else {
            constants.put(name, value);
        }
    }

    private void reAssignVariable(String name, CKValue value) {
        if(variables.containsKey(name)) {
            // The variable is declared in this scope
            variables.put(name, value);
        }
        else if(!isGlobalScope()) {
            // The variable was not declared in this scope, so let
            // the parent scope re-assign it
            parent.reAssignVariable(name, value);
        }
    }

    public Optional<CKValue> resolveVariable(String name) {
        CKValue value = variables.get(name);
        if(value != null) {
            // The variable is in this scope
            return Optional.of(value);
        }
        else if(!isGlobalScope()) {
            // Search for the variable in the parent scope
            return parent.resolveVariable(name);
        }
        else {
            // Unknown variable
            return Optional.empty();
        }
    }

    public Optional<CKValue> resolveConstant(String name) {
        CKValue value = constants.get(name);
        if(value != null) {
            // The constant is in this scope
            return Optional.of(value);
        }
        else if(!isGlobalScope()) {
            // Search for the constant in the parent scope
            return parent.resolveConstant(name);
        }
        else {
            // Unknown constant
            return Optional.empty();
        }
    }

    public Optional<CKValue> resolveSpecial(String name, CommandSource source) {
        CKValue value = null;

        if (resolves.containsKey(name)) {
            value = resolves.get(name).resolve(source);
        }

        if (value != null) {
            // The constant is in this scope
            return Optional.of(value);
        } else if (!isGlobalScope()) {
            // Search for the constant in the parent scope
            return parent.resolveSpecial(name, source);
        } else {
            // Unknown special
            return Optional.empty();
        }
    }

    public boolean isGlobalScope() {
        return parent == null;
    }

    public Scope getParent() {
        return parent;
    }

    public void setResolves(Map<String, ScopeResolver> resolves) {
        this.resolves = resolves;
    }
}
