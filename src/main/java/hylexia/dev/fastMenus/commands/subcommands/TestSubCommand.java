package hylexia.dev.fastMenus.commands.subcommands;

import hylexia.dev.fastMenus.FastMenus;
import hylexia.dev.fastMenus.commands.MainCommand;
import hylexia.dev.fastMenus.managers.FastMenuManager;
import hylexia.dev.fastMenus.objects.FastCommand;
import hylexia.dev.fastMenus.objects.FastMenu;
import hylexia.dev.fastMenus.utils.Utils;
import hylexia.dev.fastMenus.utils.libraries.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TestSubCommand extends FastCommand {

    private MainCommand parentCommand;
    private FastMenuManager fastMenuManager;

    public TestSubCommand(MainCommand mainCommand) {
        this.parentCommand = mainCommand;
        this.fastMenuManager = FastMenus.getInstance().getFastMenuManager();
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        if (!hasPermission(commandSender)) {
            return dontPermission(commandSender);
        }

        if (!(commandSender instanceof Player)) {
            Utils.sendMSG(commandSender, "[p] &cEste comando solo puede ser ejecutado por jugadores.");
            return true;
        }

        Player player = (Player) commandSender;
        String type = args[0];
        String input = args[1];

        ItemFactory itemFactory = new ItemFactory(Material.PLAYER_HEAD, "&eItem de prueba");
        if (type.equalsIgnoreCase("player")) {
            itemFactory.setHeadPlayer(input);
        } else if (type.equalsIgnoreCase("url")) {
            itemFactory.setHeadUrl(input);
        } else if (type.equalsIgnoreCase("base64")) {
            itemFactory.setHeadBase64(input);
        }

        ItemStack build = itemFactory.build();
        player.getInventory().addItem(build);

        return true;
    }

    @Override
    public List<String> getCompletions(CommandSender commandSender, String[] args) {
        if (args.length == 1) {
            return List.of("player", "url", "base64");
        } else if (args.length == 2) {
            return List.of("write...");
        }
        return List.of();
    }
}