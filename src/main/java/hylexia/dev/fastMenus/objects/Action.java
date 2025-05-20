package hylexia.dev.fastMenus.objects;

import hylexia.dev.fastMenus.utils.MenuParser;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Action {

    private String[] actionArgs;
    private Player owner;

    public abstract String getFormat();

    public abstract void execute(String[] args);

    public void setArguments(String[] actionArgs) {
        this.actionArgs = actionArgs;
    }

    public void runExecute(Player player, String[] actionArgs) {
        String[] parsedArgs = new String[actionArgs.length];
        for (int i = 0; i < actionArgs.length; i++) {
            parsedArgs[i] = MenuParser.parsePlayer(player, actionArgs[i]);
        }

        this.owner = player;
        this.actionArgs = parsedArgs;

        execute(parsedArgs);
    }

    public String parse(String toParse) {
        return MenuParser.parsePlayer(owner, toParse);
    }

    public int getInt(String arg) {
        return Integer.parseInt(arg);
    }

    public String join(String[] args) {
        return String.join(" ", args);
    }

    public double getDouble(String arg) {
        return Double.parseDouble(arg);
    }

    public List<Player> fetchTarget(String input) {
        List<Player> targets = new ArrayList<>();

        switch (input) {
            case "@a":
                targets.addAll(owner.getServer().getOnlinePlayers());
                break;
            case "@p":
                Player nearest = null;
                double minDistSq = Double.MAX_VALUE;
                Location ownerLoc = owner.getLocation();
                for (Player onlinePlayer : owner.getServer().getOnlinePlayers()) {
                    double distSq = onlinePlayer.getLocation().distanceSquared(ownerLoc);
                    if (distSq < minDistSq) {
                        minDistSq = distSq;
                        nearest = onlinePlayer;
                    }
                }
                if (nearest != null) {
                    targets.add(nearest);
                }
                break;
            case "@r":
                List<Player> onlinePlayers = new ArrayList<>(owner.getServer().getOnlinePlayers());
                if (!onlinePlayers.isEmpty()) {
                    int randomIndex = (int) (Math.random() * onlinePlayers.size());
                    targets.add(onlinePlayers.get(randomIndex));
                }
                break;
            case "@e":
                // This would target all entities, not just players.
                // Depending on your needs, you might want to filter for Player entities.
                for (Entity entity : owner.getWorld().getEntities()) {
                    if (entity instanceof Player) {
                        targets.add((Player) entity);
                    }
                }
                break;
            default:
                // If it's not a selector, try to find a player by name
                Player targetPlayer = owner.getServer().getPlayer(input);
                if (targetPlayer != null) {
                    targets.add(targetPlayer);
                }
                break;
        }

        return targets;
    }
}