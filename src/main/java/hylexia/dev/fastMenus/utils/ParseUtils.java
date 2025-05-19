package hylexia.dev.fastMenus.utils;

import hylexia.dev.fastMenus.FastMenus;
import hylexia.dev.fastMenus.utils.libraries.TinyFactory;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseUtils {

    public static String colorize(String s) {
        return colorize(null, s);
    }

    public static List<String> colorize(CommandSender sender, List<String> messages) {
        return messages.stream().map(s -> colorize(sender, s)).toList();
    }

    private static final Pattern GRADIENT_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6})");

    public static String colorize(CommandSender sender, String input) {
        if (input == null || input.isEmpty()) return "";

        input = PlaceholderAPI.setPlaceholders(sender instanceof Player player ? player : null, input);

        Matcher matcher = GRADIENT_PATTERN.matcher(input);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String color = matcher.group(1);
            matcher.appendReplacement(buffer, "§x" +
                    "§" + color.charAt(0) +
                    "§" + color.charAt(1) +
                    "§" + color.charAt(2) +
                    "§" + color.charAt(3) +
                    "§" + color.charAt(4) +
                    "§" + color.charAt(5));
        }
        matcher.appendTail(buffer);

        String gradientProcessed = buffer.toString();
        matcher = HEX_PATTERN.matcher(gradientProcessed);
        buffer = new StringBuffer();

        while (matcher.find()) {
            String color = matcher.group(1);
            matcher.appendReplacement(buffer, "§x" +
                    "§" + color.charAt(0) +
                    "§" + color.charAt(1) +
                    "§" + color.charAt(2) +
                    "§" + color.charAt(3) +
                    "§" + color.charAt(4) +
                    "§" + color.charAt(5));
        }
        matcher.appendTail(buffer);

        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    public static String gradient(String text, String fromHex, String toHex) {
        if (text == null || text.isEmpty()) return "";

        fromHex = fromHex.replace("#", "");
        toHex = toHex.replace("#", "");

        int[] rgb1 = hexToRGB(fromHex);
        int[] rgb2 = hexToRGB(toHex);

        StringBuilder result = new StringBuilder();
        int length = text.length();

        for (int i = 0; i < length; i++) {
            double ratio = (double) i / (length - 1);
            int red = (int) (rgb1[0] * (1 - ratio) + rgb2[0] * ratio);
            int green = (int) (rgb1[1] * (1 - ratio) + rgb2[1] * ratio);
            int blue = (int) (rgb1[2] * (1 - ratio) + rgb2[2] * ratio);

            String hex = String.format("#%02x%02x%02x", red, green, blue);
            result.append(colorize(hex + text.charAt(i)));
        }

        return result.toString();
    }

    private static int[] hexToRGB(String hex) {
        return new int[]{
                Integer.parseInt(hex.substring(0, 2), 16),
                Integer.parseInt(hex.substring(2, 4), 16),
                Integer.parseInt(hex.substring(4, 6), 16)
        };
    }

    public static String processMSG(@NotNull CommandSender player, String text) {
        return processMSG(player, List.of(text)).getFirst();
    }

    public static List<String> processMSG(@NotNull CommandSender player, String... text) {
        return processMSG(player, text);
    }

    public static List<String> processMSG(@NotNull CommandSender commandSender, List<String> text) {
        List<String> result = new ArrayList<>();

        for (String msg : text) {
            msg = msg.replace("[p]", FastMenus.getInstance().getPrefix());
            msg = msg.replace("%player%", (commandSender != null ? commandSender.getName() : "<null>"));
            msg = msg.replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()));
            msg = msg.replace("%dev%", FastMenus.getInstance().getTask().globalTick + "");
            msg = parseTiny(msg);
            msg = colorize(commandSender, msg);
            result.add(msg);
        }

 /*       if (commandSender != null) {
            commandSender.sendMessage("Parseando para " + commandSender.getName() + " - " + result);
        }*/
        return result;
    }

    public static String parseTiny(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        boolean inTiny = false;
        int length = text.length();

        for (int i = 0; i < length; i++) {
            char currentChar = text.charAt(i);

            if (i + 5 < length && text.substring(i, i + 6).equals("[tiny]")) {
                inTiny = true;
                i += 5; // Skip "[tiny]"
            } else if (i + 6 < length && text.substring(i, i + 7).equals("[/tiny]")) {
                inTiny = false;
                i += 6; // Skip "[/tiny]"
            } else {
                if (inTiny) {
                    result.append(TinyFactory.generate(String.valueOf(currentChar)));
                } else {
                    result.append(currentChar);
                }
            }
        }

        return result.toString();
    }
}