package io.github.zerthick.commandcore.antlr.scope.resolver;

import io.github.zerthick.commandcore.antlr.CKValue;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

public class PlayerUUIDResolver implements ScopeResolver {
    @Override
    public CKValue resolve(CommandSource source) {
        if (source instanceof Player) {
            Player player = (Player) source;
            String uuid = player.getUniqueId().toString();
            return new CKValue(uuid);
        }
        return CKValue.NULL;
    }
}
