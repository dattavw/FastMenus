package hylexia.dev.studio.utils.libraries;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ALL")
public class MenuFactory implements Listener {
    private final JavaPlugin plugin;
    private final Map<Player, Menu> openMenus = new HashMap<>();
    private final Map<Player, Runnable> menuRunnables = new HashMap<>();
    public static int updatedInt = 1;

    public MenuFactory(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        startMenuUpdater();
    }


    public <T extends Menu> void open(Player player, Class<T> menuClass) {
        try {
            T menu = menuClass.getDeclaredConstructor().newInstance();
            menu.runOpen(player);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create menu instance", e);
        }
    }


    public void createMenu(Player player, String title, int size, boolean filled) {
        Menu menu = new Menu(title, size);

        if (filled) {
            for (int i = 0; i < size; i++) {
                if (i < 9 || i >= size - 9 || i % 9 == 0 || i % 9 == 8) {
                    menu.setItem(i, new MenuItem(new ItemFactory(Material.GRAY_STAINED_GLASS_PANE, "&f").build(), () -> {
                    }));
                }
            }
        }

        player.playSound(player.getLocation(),Sound.ITEM_BUNDLE_INSERT,1,0);
        player.openInventory(menu.getInventory());
        openMenus.put(player, menu);
    }

    public void setItem(Player player, int slot, ItemStack itemStack, Runnable action) {
        if (openMenus.containsKey(player)) {
            Menu menu = openMenus.get(player);
            if (slot >= 0 && slot < menu.getInventory().getSize()) {
                menu.setItem(slot, new MenuItem(itemStack, action));
            }
        }
    }

    public void setItem(Player player, int[] slots, ItemStack itemStack, Runnable action) {
        if (openMenus.containsKey(player)) {
            Menu menu = openMenus.get(player);
            for (int slot : slots) {
                if (slot >= 0 && slot < menu.getInventory().getSize()) {
                    menu.setItem(slot, new MenuItem(itemStack, action));
                }
            }
        }
    }
    public void setItem(Player player, int[] slots, MenuItem menuItem) {
        if (openMenus.containsKey(player)) {
            Menu menu = openMenus.get(player);
            for (int slot = 0; slot < slots.length; slot++) {
                if (slot >= 0 && slot < menu.getInventory().getSize()) {
                    menu.setItem(slot, menuItem);
                }
            }
        }
    }

    public void setItem(Player player, int slot, MenuItem menuItem) {
        if (openMenus.containsKey(player)) {
            Menu menu = openMenus.get(player);
            if (slot >= 0 && slot < menu.getInventory().getSize()) {
                menu.setItem(slot, menuItem);
            }
        }
    }

    public void setContents(Player player, Runnable runnable) {
        runnable.run();
        menuRunnables.put(player, runnable);
    }

    public void startMenuUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                updatedInt++;
                for (Player player : menuRunnables.keySet()) {
                    Runnable runnable = menuRunnables.get(player);
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20L);
    }

    public void forceUpdateMenu(Player player) {
        Menu menu = openMenus.get(player);
        if (menu != null) {
            Runnable runnable = menuRunnables.get(player);
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (openMenus.containsKey(player)) {
            Menu menu = openMenus.get(player);
            menu.handleClick(player, event.getRawSlot());
            forceUpdateMenu(player);
            event.setCancelled(true);
        }
    }

    public void closeInventoryActions(Player player) {
        openMenus.remove(player);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            closeInventoryActions(player);
        }
    }

    public void removeMenu(Player player) {
        openMenus.remove(player);
    }

    public static int slot(int x, int y) {
        return (y - 1) * 9 + (x - 1);
    }

    public static void addListToMenu(Player player, MenuFactory menu, List<Object> list, int page, int[] allowedSlots, ItemStack itemStack, Runnable runnable) {
        for (int i = 0; i < allowedSlots.length; i++) {
            int slot = allowedSlots[i];
            int index = page * allowedSlots.length + i;
            if (index < list.size()) {
                Object altura = list.get(index);
                menu.setItem(player, slot, itemStack, runnable);
            }
        }
    }

    public static Object getFromList(List list, int index) {
        Object o = list.get(index);
        return o;
    }

    public void createMenu(Player player, String s, int i, boolean b, Runnable content) {
        createMenu(player,s,i,b);
        setContents(player, content);
    }


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

        public void handleClick(Player player, int slot) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - this.lastExecutionTime >= COOLDOWN) {
                if (slot >= 0 && slot < items.length && items[slot] != null) {
                    items[slot].executeAction(player);
                    this.lastExecutionTime = currentTime;
                }
            }
        }

        public void runOpen(Player player) {
            player.playSound(player.getLocation(), Sound.ITEM_BUNDLE_INSERT, 1, 0);
            this.open(player);
        }
    }

    public static class MenuItem {
        private final ItemStack itemStack;
        private final Runnable action;

        public MenuItem(ItemStack itemStack, Runnable action) {
            this.itemStack = itemStack;
            this.action = action;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public Runnable getAction() {
            return action;
        }

        public void executeAction(Player player) {
            if (getItemStack() != null && getItemStack().getType() != Material.AIR) {
                player.playSound(player.getLocation(), Sound.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_ON, 0.5f, 2);
            }
            this.action.run();
        }
    }
}