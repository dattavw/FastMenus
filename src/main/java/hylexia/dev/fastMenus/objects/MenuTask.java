package hylexia.dev.fastMenus.objects;

import hylexia.dev.fastMenus.FastMenus;
import org.bukkit.scheduler.BukkitRunnable;

public class MenuTask extends BukkitRunnable {

    private final FastMenus plugin;

    public MenuTask(FastMenus plugin) {
        this.plugin = plugin;
    }

    public int globalTick = 0;

    @Override
    public void run() {
        plugin.getMenuFactory().onTick(globalTick);

        globalTick++;
    }
}
