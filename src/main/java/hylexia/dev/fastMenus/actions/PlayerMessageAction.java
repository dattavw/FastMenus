package hylexia.dev.fastMenus.actions;

import hylexia.dev.fastMenus.objects.FastMenuAction;
import hylexia.dev.fastMenus.utils.Utils;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerMessageAction extends FastMenuAction {
    @Override
    public String getFormat() {
        return "player_msg";
    }

    @Override
    public void execute(String[] args) {
        String joined = join(args);
        String[] parts = joined.split(";");
        Player sender = getOwner();

        if (parts.length >= 1) {
            String targetString = parts[0].trim();
            String message = parts.length > 1 ? parts[1].trim() : "";

            List<Player> targets = fetchTarget(targetString);

            if (targets.isEmpty()) {
                Utils.sendMSG(sender, message);
            } else {
                for (Player target : targets) {
                    Utils.sendMSG(target, message);
                }
            }
        } else {
            sender.sendMessage("§cError: La acción 'player_msg' requiere al menos el mensaje.");
        }
    }
}