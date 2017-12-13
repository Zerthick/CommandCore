package io.github.zerthick.commandcore.antlr.scope.resolver;

import io.github.zerthick.commandcore.antlr.CKValue;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import java.util.Optional;

public class PlayerBalanceResolver implements ScopeResolver {

    @Override
    public CKValue resolve(CommandSource source) {

        if (source instanceof Player) {
            Player player = (Player) source;

            Optional<EconomyService> economyServiceOptional = Sponge.getServiceManager().provide(EconomyService.class);
            if (economyServiceOptional.isPresent()) {
                EconomyService economyService = economyServiceOptional.get();
                Optional<UniqueAccount> playerAccount = economyService.getOrCreateAccount(player.getUniqueId());
                if (playerAccount.isPresent()) {
                    return new CKValue(playerAccount.get().getBalance(economyService.getDefaultCurrency()));
                }
            }

            return CKValue.NULL;
        }

        return CKValue.NULL;
    }
}
