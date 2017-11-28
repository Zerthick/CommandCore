package io.github.zerthick.commandcore;

import com.google.inject.Inject;
import io.github.zerthick.commandcore.antlr.CKValue;
import io.github.zerthick.commandcore.service.CommandSkriptService;
import io.github.zerthick.commandcore.service.MyCommandSkriptService;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.nio.file.Path;
import java.util.Optional;

@Plugin(
        id = "commandcore",
        name = "CommandCore",
        description = "Core Library for CommandSkript Plugins",
        authors = {
                "Zerthick"
        }
)
public class CommandCore {

    @Inject
    private Logger logger;
    @Inject
    private PluginContainer instance;
    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConfig;

    public Logger getLogger() {
        return logger;
    }

    public PluginContainer getInstance() {
        return instance;
    }

    @Listener
    public void onServerInit(GameInitializationEvent event) {
        Sponge.getServiceManager().setProvider(this, CommandSkriptService.class, new MyCommandSkriptService());
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {

        Sponge.getCommandManager().register(this,
                CommandSpec.builder()
                        .arguments(GenericArguments.remainingRawJoinedStrings(Text.of("Expression")))
                        .executor((src, args) -> {

                            CommandSkriptService skriptService = Sponge.getServiceManager().provide(CommandSkriptService.class).get();

                            Optional<String> expressionOptional = args.getOne(Text.of("Expression"));
                            String expression = expressionOptional.orElse("");

                            try {
                                CKValue value = skriptService.evaluateExpression(expression, src, logger);
                                src.sendMessage(Text.of(value.toString()));
                            } catch (RuntimeException e) {
                                src.sendMessage(Text.of(TextColors.RED, e.getMessage()));
                            }

                            return CommandResult.success();
                        })
                        .build(), "cktest");

        getLogger().info(
                instance.getName() + " version " + instance.getVersion().orElse("")
                        + " enabled!");

    }


    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event, @Getter("getTargetEntity") Player player) {
        // The text message could be configurable, check the docs on how to do so!
        player.sendMessage(Text.of(TextColors.AQUA, TextStyles.BOLD, "Hi " + player.getName()));
    }

}
