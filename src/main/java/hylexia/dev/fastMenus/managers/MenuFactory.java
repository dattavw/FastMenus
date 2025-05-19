package hylexia.dev.fastMenus.managers;

import hylexia.dev.fastMenus.utils.libraries.ItemFactory;
import hylexia.dev.fastMenus.utils.libraries.objects.Menu;
import hylexia.dev.fastMenus.utils.libraries.objects.MenuItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class MenuFactory implements Listener {
    private final JavaPlugin plugin;
    private final Map<Player, Menu> openMenus = new HashMap<>();
    private final Map<Player, Runnable> menuUpdaters = new HashMap<>();

    private int updateInterval;

    public MenuFactory(JavaPlugin plugin, int updateInterval) {
        this.plugin = plugin;
        this.updateInterval = updateInterval;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void onTick(int globalTick) {
        if (globalTick % updateInterval == 0) {
            for (Player player : new HashMap<>(menuUpdaters).keySet()) {
                if (player.isOnline() && openMenus.containsKey(player)) {
                    Runnable updater = menuUpdaters.get(player);
                    if (updater != null) {
                        try {
                            updater.run();
                            player.updateInventory();
                        } catch (Exception e) {
                            e.printStackTrace();
                            menuUpdaters.remove(player);
                        }
                    }
                } else {
                    menuUpdaters.remove(player);
                }
            }
        }
    }

    public void createMenu(Player player, String title, int size, boolean filled, Runnable content) {
        if (player == null) return;

        Menu menu = new Menu(title, size);
        if (filled) {
            ItemStack borderItem = new ItemFactory(Material.GRAY_STAINED_GLASS_PANE, "&f").build();
            for (int i = 0; i < size; i++) {
                if (i < 9 || i >= size - 9 || i % 9 == 0 || i % 9 == 8) {
                    menu.setItem(i, new MenuItem(borderItem, () -> {
                    }));
                }
            }
        }

        player.playSound(player.getLocation(), Sound.ITEM_BUNDLE_INSERT, 1.0f, 0.0f);
        player.openInventory(menu.getInventory());
        player.updateInventory();
        openMenus.put(player, menu);
        menuUpdaters.put(player, content);
        player.updateInventory();

        forceUpdateMenu(player);

    }

    private void setItemInternal(Player player, int slot, MenuItem menuItem) {
        if (player == null) return;

        openMenus.computeIfPresent(player, (p, menu) -> {
            if (slot >= 0 && slot < menu.getInventory().getSize()) {
                menu.setItem(slot, menuItem);
            }
            return menu;
        });
    }

    public void setItem(Player player, int slot, ItemStack itemStack, Runnable leftClickAction, Runnable rightClickAction) {
        if (player == null) return;
        setItemInternal(player, slot, new MenuItem(itemStack, leftClickAction, rightClickAction));
    }

    public void setItem(Player player, int slot, ItemStack itemStack, Runnable action) {
        if (player == null) return;
        setItemInternal(player, slot, new MenuItem(itemStack, action));
    }

    public void setItem(Player player, int[] slots, ItemStack itemStack, Runnable action) {
        if (player == null) return;
        MenuItem menuItem = new MenuItem(itemStack, action);
        for (int slot : slots) {
            setItemInternal(player, slot, menuItem);
        }
    }

    public void setItem(Player player, int[] slots, MenuItem[] menuItems) {
        if (player == null) return;
        int length = Math.min(slots.length, menuItems.length);
        for (int i = 0; i < length; i++) {
            setItemInternal(player, slots[i], menuItems[i]);
        }
    }

    public void setItem(Player player, int slot, MenuItem menuItem) {
        if (player == null) return;
        setItemInternal(player, slot, menuItem);
    }

    public void updateContents(Player player, Runnable updater) {
        if (player == null) return;
        if (openMenus.containsKey(player)) {
            menuUpdaters.put(player, updater);
        }
    }

    public void forceUpdateMenu(Player player) {
        if (player == null) return;
        Runnable updater = menuUpdaters.get(player);
        if (updater != null && openMenus.containsKey(player)) {
            updater.run();
            player.updateInventory();
        }
    }

    public void closeMenu(Player player) {
        if (player == null) return;
        openMenus.remove(player);
        menuUpdaters.remove(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            if (openMenus.containsKey(player)) {
                event.setCancelled(true);
                openMenus.get(player).handleClick(player, event.getRawSlot(), event.getClick());
                player.updateInventory();
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            closeMenu(player);
        }
    }

    public static int getSlot(int x, int y) {
        return (y - 1) * 9 + (x - 1);
    }
}