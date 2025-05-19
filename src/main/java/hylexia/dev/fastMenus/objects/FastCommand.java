package hylexia.dev.fastMenus.objects;

import hylexia.dev.fastMenus.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class FastCommand {

    public abstract boolean execute(CommandSender commandSender, String[] args);

    public abstract List<String> getCompletions(CommandSender commandSender, String[] args);

    public static boolean hasPermission(CommandSender commandSender) {
        return commandSender.hasPermission("fastmenus.admin");
    }

    public static boolean dontPermission(CommandSender commandSender) {
        if (!hasPermission(commandSender)) {
            Utils.sendMSG(commandSender, "[p] &cNo tienes permiso para ejecutar este comando.");
            return false;
        }

        return true;
    }
}
