package hylexia.dev.fastMenus.commands.subcommands;

import hylexia.dev.fastMenus.FastMenus;
import hylexia.dev.fastMenus.commands.MainCommand;
import hylexia.dev.fastMenus.objects.FastCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadSubCommand extends FastCommand {
    private final MainCommand parentCommand;
    private final FastMenus fastMenus;

    public ReloadSubCommand(MainCommand parentCommand) {
        this.parentCommand = parentCommand;
        this.fastMenus = FastMenus.getInstance();
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        if (!commandSender.hasPermission("fastmenus.reload")) {
            commandSender.sendMessage("§cNo tienes permiso para ejecutar este comando.");
            return true;
        }

        fastMenus.getMenuManager().reloadMenus();
        commandSender.sendMessage("§aLos menús de FastMenus han sido recargados.");
        return true;
    }

    @Override
    public List<String> getCompletions(CommandSender commandSender, String[] args) {
        return List.of();
    }
}