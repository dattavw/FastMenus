package hylexia.dev.fastMenus.utils.libraries;

import hylexia.dev.fastMenus.utils.ParseUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class LoggerFactory {

    public final Plugin plugin;
    public static String prefix = "[...] ";
    public static boolean disabled = false;

    public LoggerFactory(Plugin plugin) {
        this.plugin = plugin;

        if (plugin != null) {
            prefix = "[" + plugin.getName() + "] ";
        }
    }

    public static void log(LogType type, String message) {
        redirect(type, message);
    }

    public static void info(String message) {
        LogType logType = LogType.INFO;
        redirect(logType, message);
    }

    public static void warn(String message) {
        LogType logType = LogType.WARNING;
        redirect(logType, message);
    }

    public static void success(String message) {
        LogType logType = LogType.SUCCESS;
        redirect(logType, message);
    }

    public static void error(String message) {
        LogType logType = LogType.ERROR;
        redirect(logType, message);
    }

    public static void redirect(LogType type, String message) {
        if (disabled) return;

        var ref = new Object() {
            String fixedMessage = type.color + prefix + " " + type.color + message;
        };
        ref.fixedMessage = ParseUtils.colorize(ref.fixedMessage);

        Bukkit.getConsoleSender().sendMessage(ref.fixedMessage);
        Bukkit.getOnlinePlayers().stream().filter(player -> player.isOp() && player.getGameMode().isInvulnerable()).forEach(player -> player.sendMessage(ref.fixedMessage));
    }


    public enum LogType {
        INFO("&e"),
        WARNING("&6"),
        SUCCESS("&a"),
        ERROR("&c");

        private final String color;

        LogType(String color) {
            this.color = color;
        }
    }
}