package hylexia.dev.fastMenus.objects;

import hylexia.dev.fastMenus.FastMenus;
import hylexia.dev.fastMenus.utils.ParseUtils;
import hylexia.dev.fastMenus.utils.libraries.ItemFactory;
import hylexia.dev.fastMenus.managers.MenuFactory;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public abstract class FastMenu {

    private final FastMenus plugin = FastMenus.getInstance();
    private final MenuFactory menuFactory = plugin.getMenuFactory();

    private Player menuOwner;

    public abstract String getIdentifier();

    public abstract String getTitle();

    public abstract int getSize();

    public boolean isFilled() {
        return false;
    }

    public abstract void onOpen(Player player, String[] args);

    public String lowerCaseIdentifier() {
        return getIdentifier().toLowerCase().replace(".yml", "");
    }

    public void executeMenu(Player player, String[] args) {
        menuOwner = player;
        String title = ParseUtils.processMSG(player, getTitle());
        int size = getSize();
        boolean filled = isFilled();

        menuFactory.createMenu(player, title, size, filled, () -> {
            onOpen(player, args);
        });
    }

    public void setItem(int slot, ItemFactory item, Runnable right, Runnable left) {
        menuFactory.setItem(menuOwner, slot, item.build(), right, left);
    }

    public void setItem(int slot, ItemFactory item, Runnable action) {
        menuFactory.setItem(menuOwner, slot, item.build(), action);
    }

    public int getSlot(int x, int y) {
        return (y - 1) * 9 + (x - 1);
    }
}
