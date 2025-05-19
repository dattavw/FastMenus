package hylexia.dev.fastMenus.commands.subcommands;

import hylexia.dev.fastMenus.FastMenus;
import hylexia.dev.fastMenus.commands.MainCommand;
import hylexia.dev.fastMenus.managers.FastMenuManager;
import hylexia.dev.fastMenus.objects.FastCommand;
import hylexia.dev.fastMenus.objects.FastMenu;
import hylexia.dev.fastMenus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class OpenSubCommand extends FastCommand {

    private MainCommand parentCommand;
    private FastMenuManager fastMenuManager;

    public OpenSubCommand(MainCommand mainCommand) {
        this.parentCommand = mainCommand;
        this.fastMenuManager = FastMenus.getInstance().getFastMenuManager();
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        if (!hasPermission(commandSender)) {
            return dontPermission(commandSender);
        }

        if (args.length == 0) {
            Utils.sendMSG(commandSender, "[p] &CDebes especificar el identificador del menú.");
            return true;
        }

        String menuIdentifier = args[0];
        FastMenu menu = fastMenuManager.getMenu(menuIdentifier);
        if (menu == null) {
            Utils.sendMSG(commandSender, "[p] &cNo existe el menú con el identificador '" + menuIdentifier + "'");
            return true;
        }
        Player a = (Player) commandSender;

        if (args.length > 1) {
            Player b = Bukkit.getPlayer(args[1]);
            if (b != null) {
                a = b;
                Utils.sendMSG(commandSender, "[p] &aAbriendo el menú " + menuIdentifier + " para el jugador '" + a.getName() + "'");

            } else {
                Utils.sendMSG(commandSender, "[p] &cNo existe el jugador '" + args[1] + "'");

                return true;
            }
        }

        fastMenuManager.open(a, menu, args);
        return false;
    }

    @Override
    public List<String> getCompletions(CommandSender commandSender, String[] args) {
        if (args.length == 1) {
            return fastMenuManager.getMenuIdentifiers();
        } else if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return List.of();
    }
}
