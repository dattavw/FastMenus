package hylexia.dev.fastMenus.actions;

import hylexia.dev.fastMenus.objects.FastMenuAction;
import hylexia.dev.fastMenus.utils.Utils;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerTitleAction extends FastMenuAction {

    @Override
    public String getFormat() {
        return "player_title";
    }

    @Override
    public void execute(String[] args) {
        String joined = join(args);
        String[] parts = joined.split(";");
        Player player = getOwner();

        if (parts.length >= 2) {
            String targetString = parts[0].trim();
            String title = parts[1].trim();
            String subtitle = parts.length > 2 ? parts[2].trim() : "";

            int fadeIn = parts.length > 3 ? parseIntOrDefault(parts[3].trim(), 10) : 10;
            int stay = parts.length > 4 ? parseIntOrDefault(parts[4].trim(), 30) : 30;
            int fadeOut = parts.length > 5 ? parseIntOrDefault(parts[5].trim(), 10) : 10;

            List<Player> targets = fetchTarget(targetString);

            if (targets.isEmpty()) targets.add(player);

            for (Player target : targets) {
                Utils.sendTitle(target, title, subtitle, fadeIn, stay, fadeOut);
            }

        } else {
            player.sendMessage("§cError: La acción 'player_title' requiere al menos 2 argumentos separados por ';': [<objetivo>];<título>;<subtítulo>;[fadeIn];[stay];[fadeOut].");
        }
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}