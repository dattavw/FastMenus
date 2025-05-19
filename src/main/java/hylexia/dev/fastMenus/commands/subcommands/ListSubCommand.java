package hylexia.dev.fastMenus.commands.subcommands;

import hylexia.dev.fastMenus.FastMenus;
import hylexia.dev.fastMenus.commands.MainCommand;
import hylexia.dev.fastMenus.objects.FastCommand;
import hylexia.dev.fastMenus.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ListSubCommand extends FastCommand {
    private final MainCommand parentCommand;
    private final FastMenus fastMenus;

    public ListSubCommand(MainCommand parentCommand) {
        this.parentCommand = parentCommand;
        this.fastMenus = FastMenus.getInstance();
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        if (!hasPermission(commandSender)) {
            return dontPermission(commandSender);
        }

        Utils.sendMSG(commandSender, "","&f Los menús de &a&lFastMenus&f son:");
        for (String menuIdentifier : fastMenus.getFastMenuManager().getMenuIdentifiers()) {
            Utils.sendMSG(commandSender, "&8  » &e" + menuIdentifier);
        }
        Utils.sendMSG(commandSender, "");

        return true;
    }

    @Override
    public List<String> getCompletions(CommandSender commandSender, String[] args) {
        return List.of();
    }
}