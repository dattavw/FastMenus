package hylexia.dev.fastMenus.actions;

import hylexia.dev.fastMenus.objects.Action;
import org.bukkit.Bukkit;

public class ConsoleAction extends Action {

    @Override
    public String getFormat() {
        return "console";
    }

    @Override
    public void execute(String[] args) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), join(args));
    }
}
