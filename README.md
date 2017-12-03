# CommandCore:
CommandCore is a library plugin which allows scripts written in [CommandSkript](https://github.com/Zerthick/CommandSkript)
to be executed within a Minecraft Server Environment.

In order for skripts to be utilized they sould be located within the `~/config/commandcore` directory or in a sub-directory
contained within it.

## Commands:

 * `/cktest <expression>` - Evaluates `expression` and prints the result, useful for debugging scripts
 * `/ckexec <skript>` - Executes `skript`
 
 ## Permissions
 | Permission                      | Use                                                             |
 |:--------------------------------|:----------------------------------------------------------------|
 | `commandcore.command.cktest`    | Allows the player to run `/cktest <expression>`                 |
 | `commandcore.command.ckexec`    | Allows the player to run `/ckexec <skript>`                     |

**Note:** You can reload skript files at runtime using the `/sponge plugins reload` command.

## Writing Skripts
You can view documentation and examples of skripts on the [CommandSkript Wiki](https://github.com/Zerthick/CommandSkript/wiki)

## Support Me
I will **never** charge money for the use of my plugins, however they do require a significant amount of work to maintain and update. If you'd like to show your support and buy me a cup of tea sometime (I don't drink that horrid coffee stuff :P) you can do so [here](https://www.paypal.me/zerthick)
