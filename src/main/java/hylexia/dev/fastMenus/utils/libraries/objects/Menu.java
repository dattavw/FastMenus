package hylexia.dev.fastMenus.utils.libraries.objects;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class Menu {

    @Getter
    private final Inventory inventory;

    @Getter
    private final MenuItem[] items;
    private long lastExecutionTime;

    private static final long COOLDOWN = 20L * 5;

    public Menu(String title, int size) {
        this.inventory = Bukkit.createInventory(null, size, title);
        this.items = new MenuItem[size];
    }

    public void setItem(int slot, MenuItem menuItem) {
        inventory.setItem(slot, menuItem.getItemStack());
        items[slot] = menuItem;
    }

    public void setItem(int[] slot, MenuItem menuItem) {
        for (int i : slot) {
            inventory.setItem(i, menuItem.getItemStack());
            items[i] = menuItem;
        }
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public void handleClick(Player player, int slot, @NotNull ClickType click) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastExecutionTime >= COOLDOWN) {
            if (slot >= 0 && slot < items.length && items[slot] != null) {
                MenuItem item = items[slot];
                item.executeAction(player, click);
                this.lastExecutionTime = currentTime;
            }
        }
    }

    public void runOpen(Player player) {
        player.playSound(player.getLocation(), Sound.ITEM_BUNDLE_INSERT, 1, 0);
        this.open(player);
    }
}