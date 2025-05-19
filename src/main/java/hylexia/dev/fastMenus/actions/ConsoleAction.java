package hylexia.dev.fastMenus.actions;

import hylexia.dev.fastMenus.objects.FastMenuAction;
import hylexia.dev.fastMenus.objects.FastMenuItem;
import hylexia.dev.fastMenus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.io.BukkitObjectInputStream;

public class ConsoleAction extends FastMenuAction {

    @Override
    public String getFormat() {
        return "console";
    }

    @Override
    public void execute(String[] args) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), join(args));
    }
}
