package hylexia.dev.fastMenus.actions;

import hylexia.dev.fastMenus.objects.Action;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerSoundAction extends Action {

    @Override
    public String getFormat() {
        return "player_sound";
    }

    @Override
    public void execute(String[] args) {
        String joined = join(args);
        String[] parts = joined.split(";");
        Player sender = getOwner();
        if (sender == null) return;

        if (parts.length >= 1) {
            String targetString = parts[0].trim();
            String soundName = parts.length > 1 ? parts[1].trim().toUpperCase() : "";
            float volume = parts.length > 2 ? parseFloatOrDefault(parts[2].trim(), 1.0f) : 1.0f;
            float pitch = parts.length > 3 ? parseFloatOrDefault(parts[3].trim(), 1.0f) : 1.0f;

            List<Player> targets = fetchTarget(targetString);

            if (targets.isEmpty()) {
                try {
                    Sound sound = Sound.valueOf(soundName);
                    sender.playSound(sender.getLocation(), sound, volume, pitch);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("§cError: Sonido '" + soundName + "' no válido.");
                }
            } else {
                for (Player target : targets) {
                    try {
                        Sound sound = Sound.valueOf(soundName);
                        target.playSound(target.getLocation(), sound, volume, pitch);
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage("§cError: Sonido '" + soundName + "' no válido.");
                        break;
                    }
                }
            }
        } else {
            sender.sendMessage("§cError: La acción 'player_sound' requiere al menos el objetivo y el nombre del sonido.");
        }
    }

    private float parseFloatOrDefault(String value, float defaultValue) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}