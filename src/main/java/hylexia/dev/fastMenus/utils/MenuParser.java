package hylexia.dev.fastMenus.utils;

import hylexia.dev.fastMenus.FastMenus;
import hylexia.dev.fastMenus.objects.FastMenu;
import hylexia.dev.fastMenus.objects.Action;
import hylexia.dev.fastMenus.objects.FastMenuItem;
import hylexia.dev.fastMenus.utils.configuration.Configuration;
import hylexia.dev.fastMenus.utils.libraries.ItemFactory;
import hylexia.dev.fastMenus.utils.libraries.LoggerFactory;
import hylexia.dev.fastMenus.managers.MenuFactory;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MenuParser {

    public static FastMenu parse(String identifier, Configuration configuration) {
        String title = configuration.getString("config.title");
        Integer size = parseSize(configuration.getString("config.size"));
        Boolean filled = configuration.getBoolean("config.filled");

        if (title == null || size == null || filled == null) {
            LoggerFactory.error("Error: Configuración del menú inválida en el identificador: " + identifier);
            throw new IllegalArgumentException("La configuración del menú no es válida en el identificador: " + identifier);
        }

        FastMenu menu = new FastMenu() {
            private final List<FastMenuItem> items = new ArrayList<>();
            private final boolean isMenuFilled = filled;
            private MenuFactory menuFactory = getMenuFactory();

            @Override
            public String getIdentifier() {
                return identifier;
            }

            @Override
            public String getTitle() {
                return title;
            }

            @Override
            public boolean isFilled() {
                return isMenuFilled;
            }

            @Override
            public int getSize() {
                return size;
            }

            @Override
            public void onOpen(Player player, String[] args) {
                parseItems(configuration, player);
                for (FastMenuItem menuItem : items) {
                    String[] slotCoords = (menuItem.getLowerCaseIdentifier()).split(";");
                    for (String coord : slotCoords) {
                        String[] parts = coord.split(",");
                        if (parts.length == 2) {
                            try {
                                int x = Integer.parseInt(parts[0].trim());
                                int y = Integer.parseInt(parts[1].trim());
                                int slot = MenuFactory.getSlot(x, y);
                                ItemFactory itemStack = menuItem.getItem(player);
                                menuFactory.setItem(player, slot, itemStack.build(), menuItem.getLeftClickAction(), menuItem.getRightClickAction());
                            } catch (NumberFormatException e) {
                                LoggerFactory.error("Error al parsear las coordenadas del ítem desplazado: " + coord + " en " + menuItem.getDisplayName() + ": " + e);
                            }
                        } else {
                            LoggerFactory.warn("Formato de coordenadas inválido para el ítem desplazado: " + coord + " en " + menuItem.getDisplayName() + ". Debe ser 'x,y'.");
                        }
                    }
                }
            }

            private void parseItems(Configuration config, Player player) {
                ConfigurationSection itemsSection = config.getConfigurationSection("items");
                if (itemsSection != null) {
                    for (String key : itemsSection.getKeys(false)) {
                        ConfigurationSection itemConfig = itemsSection.getConfigurationSection(key);
                        if (itemConfig != null) {
                            FastMenuItem item = parseItem(player, itemConfig);
                            if (item != null) {
                                String[] coords = key.split(";");
                                if (coords.length > 0) {
                                    String[] firstCoord = coords[0].split(",");
                                    if (firstCoord.length == 2) {
                                        try {
                                            int x = Integer.parseInt(firstCoord[0].trim());
                                            int y = Integer.parseInt(firstCoord[1].trim());
                                            item.setXSlot(x);
                                            item.setYSlot(y);
                                            item.setLowerCaseIdentifier(key.toLowerCase());
                                            items.add(item);
                                        } catch (NumberFormatException e) {
                                            LoggerFactory.error("Error al parsear las coordenadas base del ítem con clave: " + key + ": " + e);
                                        }
                                    } else {
                                        LoggerFactory.warn("Formato de coordenadas base inválido para el ítem con clave: " + key + ". Debe ser 'x,y'.");
                                    }
                                } else {
                                    LoggerFactory.warn("No se encontraron coordenadas para el ítem con clave: " + key + ".");
                                }
                            } else {
                                LoggerFactory.warn("No se pudo parsear el ítem con clave: " + key + ".");
                            }
                        }
                    }
                }
            }
        };
        return menu;
    }

    public static FastMenuItem parseItem(Player player, @Nullable ConfigurationSection itemConfig) {
        if (itemConfig == null) {
            LoggerFactory.warn("Se recibió una sección de configuración de ítem nula.");
            return null;
        }

        Object materialObj = itemConfig.get("item");
        String name = itemConfig.getString("name");
        List<String> lore = itemConfig.getStringList("lore");
        @Nullable ConfigurationSection actionConfig = itemConfig.getConfigurationSection("action");
        Integer customModelData = itemConfig.getInt("flags.customModelData");
        String headUrl = itemConfig.getString("flags.headUrl");
        String headPlayer = itemConfig.getString("flags.headPlayer");
        Boolean hideFlags = itemConfig.getBoolean("flags.hideFlags", false);
        Set<ItemFlag> itemFlags = parseItemFlags(itemConfig.getStringList("flags"));

        List<Material> materials = new ArrayList<>();
        if (materialObj instanceof String) {
            Material material = Material.getMaterial(((String) materialObj).toUpperCase());
            if (material == null) {
                LoggerFactory.warn("Material inválido: " + materialObj);
                return null;
            }
            materials.add(material);
        } else if (materialObj instanceof List) {
            for (Object obj : (List<?>) materialObj) {
                if (obj instanceof String) {
                    Material material = Material.getMaterial(((String) obj).toUpperCase());
                    if (material == null) {
                        LoggerFactory.warn("Material inválido en la lista: " + obj);
                        continue;
                    }
                    materials.add(material);
                } else {
                    LoggerFactory.warn("Elemento inválido en la lista de materiales: " + obj.getClass().getName());
                }
            }
            if (materials.isEmpty()) {
                LoggerFactory.warn("La lista de materiales está vacía o contiene solo materiales inválidos.");
                return null;
            }
        } else {
            LoggerFactory.warn("Formato inválido para 'item'. Debe ser una cadena o una lista de cadenas.");
            return null;
        }

        String parsedName = ParseUtils.processMSG(player, name);
        List<String> parsedLore = ParseUtils.processMSG(player, lore);

        FastMenuItem menuItem = new FastMenuItem(0, 0, parsedName, materials, lore);
        menuItem.setCustomModelData(customModelData);
        menuItem.setItemFlags(itemFlags);
        menuItem.setHeadUrl(headUrl);
        if (headPlayer != null) {
            menuItem.setHeadPlayerName(headPlayer.replace("%player%", player.getName()));
        }
        menuItem.setHideFlags(hideFlags);
        menuItem.setDisplayName(parsedName);
        menuItem.setLore(parsedLore);

        if (actionConfig != null) {

            final Runnable voidRunnable = () -> {
            };

            menuItem.setRightClickAction(voidRunnable);
            menuItem.setLeftClickAction(voidRunnable);

            @Nullable List<String> anyActionList = actionConfig.getStringList("any");
            @Nullable List<String> rightActionList = actionConfig.getStringList("right");
            @Nullable List<String> leftActionList = actionConfig.getStringList("left");

            if (anyActionList != null && !anyActionList.isEmpty()) {
                Runnable runnable = () -> executeActions(player, anyActionList);
                menuItem.setLeftClickAction(runnable);
                menuItem.setRightClickAction(runnable);
            }

            if (rightActionList != null && !rightActionList.isEmpty()) {
                menuItem.setRightClickAction(() -> executeActions(player, rightActionList));
            }

            if (leftActionList != null && !leftActionList.isEmpty()) {
                menuItem.setLeftClickAction(() -> executeActions(player, leftActionList));
            }
        }

        return menuItem;
    }

    private static void executeActions(Player player, List<String> actions) {
        if (actions != null) {
            for (String actionString : actions) {
                Action action = FastMenus.getInstance().getFastMenuManager().getAction(actionString);
                if (action != null) {
                    String[] args = actionString.split(":", 2);
                    String[] actionArgs = (args.length > 1) ? args[1].trim().split(" ") : new String[0];
                    action.runExecute(player, actionArgs);
                }
            }
        }
    }

    private static Set<ItemFlag> parseItemFlags(List<String> flagsList) {
        Set<ItemFlag> flags = new HashSet<>();
        if (flagsList != null) {
            for (String flagStr : flagsList) {
                String trimmedFlag = flagStr.trim().toUpperCase();
                if (!trimmedFlag.startsWith("HEADURL(") && !trimmedFlag.startsWith("HEADPLAYER(") && !trimmedFlag.startsWith("CUSTOMMODELDATA(") && !trimmedFlag.startsWith("HIDEFLAGS(")) {
                    try {
                        ItemFlag flag = ItemFlag.valueOf(trimmedFlag);
                        flags.add(flag);
                    } catch (IllegalArgumentException e) {
                        LoggerFactory.warn("Flag inválida: " + trimmedFlag);
                    }
                }
            }
        }
        return flags;
    }

    public static Integer parseSize(String size) {
        if (size == null) return null;
        String[] parts = size.split("\\*");
        if (parts.length != 2) {
            throw new IllegalArgumentException("El formato del tamaño del menú no es válido. Debe ser 'x*y'");
        }
        try {
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            return x * y;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Los valores de ancho y alto del menú deben ser números enteros.");
        }
    }

    public static int parseSlot(String slot) {
        if (slot == null) return -1;
        String[] parts = slot.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("El formato de la posición del elemento no es válido. Debe ser 'x,y'");
        }
        try {
            int x = Integer.parseInt(parts[0].trim());
            int y = Integer.parseInt(parts[1].trim());
            return MenuFactory.getSlot(x, y);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Los valores de la posición del elemento deben ser números enteros.");
        }
    }

    public static String parsePlayer(Player player, String string) {
        if (player != null) string = ParseUtils.processMSG(player, string);

        return string;
    }
}