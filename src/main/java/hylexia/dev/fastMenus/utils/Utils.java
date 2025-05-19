package hylexia.dev.fastMenus.utils;

import hylexia.dev.fastMenus.utils.libraries.LoggerFactory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Utils {

    public static void sendMSG(CommandSender sender, String message) {
        sendMSG(sender, List.of(message));
    }

    public static void sendMSG(CommandSender sender, String... messages) {
        sendMSG(sender, List.of(messages));
    }

    public static void sendMSG(CommandSender sender, List<String> messages) {
        for (String message : messages) {
            message = ParseUtils.processMSG(sender, message);

            sender.sendMessage(message);
        }
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(ParseUtils.processMSG(player, title), ParseUtils.processMSG(player, subtitle), fadeIn, stay, fadeOut);
    }

    public static void debug(String s) {
        LoggerFactory.info(s);
    }
}
