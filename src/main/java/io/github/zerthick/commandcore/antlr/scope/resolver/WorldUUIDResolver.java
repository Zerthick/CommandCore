package io.github.zerthick.commandcore.antlr.scope.resolver;

import io.github.zerthick.commandcore.antlr.CKValue;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.world.Locatable;

public class WorldUUIDResolver implements ScopeResolver {
    @Override
    public CKValue resolve(CommandSource source) {
        if (source instanceof Locatable) {
            Locatable locatable = (Locatable) source;
            String uuid = locatable.getWorld().getUniqueId().toString();
            return new CKValue(uuid);
        }
        return CKValue.NULL;
    }
}
