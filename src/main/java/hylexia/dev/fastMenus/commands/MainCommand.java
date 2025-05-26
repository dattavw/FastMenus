package hylexia.dev.fastMenus.commands;

import hylexia.dev.fastMenus.commands.subcommands.ListSubCommand;
import hylexia.dev.fastMenus.commands.subcommands.OpenSubCommand;
import hylexia.dev.fastMenus.commands.subcommands.ReloadSubCommand;
import hylexia.dev.fastMenus.commands.subcommands.TestSubCommand;
import hylexia.dev.fastMenus.objects.FastCommand;
import hylexia.dev.fastMenus.utils.Utils;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class MainCommand implements CommandExecutor, TabCompleter {

    private final Map<String, FastCommand> subCommands = new HashMap<>();

    public MainCommand() {
        registerSubCommand("open", new OpenSubCommand(this));
        registerSubCommand("reload", new ReloadSubCommand(this));
        registerSubCommand("list", new ListSubCommand(this));
        registerSubCommand("test", new TestSubCommand(this));
    }

    public void registerSubCommand(String name, FastCommand subCommand) {
        subCommands.put(name.toLowerCase(), subCommand);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!FastCommand.hasPermission(commandSender)) {
            FastCommand.dontPermission(commandSender);
            return false;
        }

        if (args.length == 0) {
            Utils.sendMSG(commandSender, "[p] &cDebes especificar un subcomando. Usa: /" + label + " <subcomando>");
            return true;
        }

        String subCommandName = args[0].toLowerCase();
        if (subCommands.containsKey(subCommandName)) {
            FastCommand subCommand = subCommands.get(subCommandName);
            String[] subArgs = java.util.Arrays.copyOfRange(args, 1, args.length);
            return subCommand.execute(commandSender, subArgs);
        } else {
            Utils.sendMSG(commandSender, "[p] &cEl subcomando '" + subCommandName + "' no existe. Usa: /" + label + " <subcomando>");
            return true;
        }
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!FastCommand.hasPermission(commandSender)) {
            return List.of();
        }

        if (args.length == 1) {
            return subCommands.keySet().stream()
                    .filter(name -> name.startsWith(args[0].toLowerCase()))
                    .toList();
        } else if (args.length >= 2) {
            String subCommandName = args[0].toLowerCase();
            if (subCommands.containsKey(subCommandName)) {
                FastCommand subCommand = subCommands.get(subCommandName);
                String[] subArgs = java.util.Arrays.copyOfRange(args, 1, args.length);
                return subCommand.getCompletions(commandSender, subArgs);
            }
        }
        return List.of();
    }
}