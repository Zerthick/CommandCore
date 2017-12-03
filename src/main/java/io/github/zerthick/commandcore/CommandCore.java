package io.github.zerthick.commandcore;

import com.google.inject.Inject;
import io.github.zerthick.commandcore.antlr.CKValue;
import io.github.zerthick.commandcore.service.CommandSkriptService;
import io.github.zerthick.commandcore.service.MyCommandSkriptService;
import io.github.zerthick.commandcore.skript.Skript;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Plugin(
        id = "commandcore",
        name = "CommandCore",
        description = "Core Library for CommandSkript Plugins",
        version = "1.0.0",
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
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    private MyCommandSkriptService myCommandSkriptService;

    public Logger getLogger() {
        return logger;
    }

    public PluginContainer getInstance() {
        return instance;
    }

    @Listener
    public void onServerInit(GameInitializationEvent event) {

        myCommandSkriptService = new MyCommandSkriptService(loadSkripts());

        Sponge.getServiceManager().setProvider(this, CommandSkriptService.class, myCommandSkriptService);
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {

        CommandSkriptService skriptService = Sponge.getServiceManager().provide(CommandSkriptService.class).get();

        Sponge.getCommandManager().register(this,
                CommandSpec.builder()
                        .arguments(GenericArguments.remainingRawJoinedStrings(Text.of("Expression")))
                        .executor((src, args) -> {
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
                        .permission("commandcore.command.cktest")
                        .description(Text.of("Test the output of a single expression in CommandSkript"))
                        .build(), "cktest");

        Sponge.getCommandManager().register(this,
                CommandSpec.builder()
                        .arguments(GenericArguments.choices(Text.of("skript"), skriptService::getSkripts, s -> s),
                                GenericArguments.optional(GenericArguments.remainingRawJoinedStrings(Text.of("args"))))
                        .executor((src, args) -> {

                            Optional<String> skriptOptional = args.getOne(Text.of("skript"));
                            if (skriptOptional.isPresent()) {
                                Optional<String> skriptArgsOptional = args.getOne(Text.of("args"));

                                String[] skriptArgs = skriptArgsOptional.orElse("").split("\\s+");

                                skriptService.executeSkript(skriptOptional.get(), src, skriptArgs, logger);
                            }

                            return CommandResult.success();
                        })
                        .permission("commandcore.command.ckexec")
                        .description(Text.of("Execute a script file written in CommandSkript"))
                        .build(), "ckexec");


        getLogger().info(
                instance.getName() + " version " + instance.getVersion().orElse("")
                        + " enabled!");

    }

    @Listener
    public void onPluginsReload(GameReloadEvent event) {
        myCommandSkriptService.setSkripts(loadSkripts());
    }

    private Set<Skript> loadSkripts() {

        //Load skript files
        Set<Skript> skripts = new HashSet<>();
        try {
            skripts.addAll(Files.walk(configDir)
                    .filter(Files::isRegularFile)
                    .filter(path -> com.google.common.io.Files.getFileExtension(path.getFileName().toString()).equals("ck"))
                    .map(Skript::new).collect(Collectors.toSet()));
        } catch (IOException e) {
            logger.error("Error loading skript files: " + e.getMessage());
        }

        return skripts;
    }

}
