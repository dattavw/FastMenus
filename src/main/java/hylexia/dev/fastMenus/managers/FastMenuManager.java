package hylexia.dev.fastMenus.managers;

import hylexia.dev.fastMenus.FastMenus;
import hylexia.dev.fastMenus.objects.FastMenu;
import hylexia.dev.fastMenus.objects.FastMenuAction;
import hylexia.dev.fastMenus.utils.MenuParser;
import hylexia.dev.fastMenus.utils.ReflectionsUtil;
import hylexia.dev.fastMenus.utils.Utils;
import hylexia.dev.fastMenus.utils.configuration.Configuration;
import hylexia.dev.fastMenus.utils.configuration.ConfigurationManager;
import hylexia.dev.fastMenus.utils.libraries.LoggerFactory;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FastMenuManager {

    private final FastMenus fastMenus;
    private final List<FastMenu> menus = new ArrayList<>();
    private List<FastMenuAction> actions = new ArrayList<>();
    private final File menusDirectory;
    private final ConfigurationManager configurationManager;

    public FastMenuManager(FastMenus fastMenus) {
        this.fastMenus = fastMenus;
        this.menusDirectory = new File(fastMenus.getDataFolder(), "menus");
        this.configurationManager = fastMenus.getConfigurationManager();
        this.actions = new ArrayList<>(ReflectionsUtil.getInstancesFromPackage(FastMenuAction.class, "hylexia.dev.fastMenus.actions"));

        actions.forEach(action -> {
            LoggerFactory.warn("ℹ️ Acción cargada: " + action.getClass().getSimpleName());
        });

        if (!menusDirectory.exists()) {
            if (menusDirectory.mkdirs()) {
                LoggerFactory.info("📁 Directorio de menús creado en: " + menusDirectory.getAbsolutePath());
            } else {
                LoggerFactory.error("☒ ¡Error! No se pudo crear el directorio de menús.");
                return;
            }
        }

        loadMenus();
    }

    public FastMenuAction getAction(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        String[] parts = input.split(":", 2);
        if (parts.length != 2) {
            LoggerFactory.warn("⚠️ ¡Advertencia! Formato de acción inválido: '" + input + "'. Debe ser 'nombreDeAccion:argumentos'.");
            return null;
        }

        String actionName = parts[0].trim().toLowerCase();
        String arguments = parts[1].trim();
        String[] actionArgs = arguments.split(" ");

        for (FastMenuAction action : actions) {
            if (action.getFormat().trim().toLowerCase().equals(actionName)) {
                try {
                    FastMenuAction newActionInstance = action.getClass().getDeclaredConstructor().newInstance();
                    newActionInstance.setArguments(actionArgs);
                    return newActionInstance;
                } catch (Exception e) {
                    LoggerFactory.error("☒ ¡Error! No se pudo instanciar la acción '" + actionName + "': " + e.getMessage());
                    return null;
                }
            }
        }

        LoggerFactory.warn("⚠️ ¡Advertencia! No se encontró la acción con el nombre: '" + actionName + "'.");
        return null;
    }

    private void loadMenus() {
        File[] files = menusDirectory.listFiles();
        if (files == null) {
            LoggerFactory.warn("⚠️ ¡Advertencia! No se encontraron archivos en el directorio de menús.");
            return;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".yml")) {
                String identifier = file.getName();
                LoggerFactory.info("ℹ️ Cargando menú: '" + identifier + "'...");

                Configuration configuration = configurationManager.getConfig(new File(menusDirectory, identifier));
                FastMenu parsedMenu = parse(identifier, configuration);

                if (parsedMenu != null) {
                    menus.add(parsedMenu);
                    LoggerFactory.success("☑ Menú '" + identifier + "' cargado exitosamente.");
                } else {
                    LoggerFactory.error("☒ ¡Error! Fallo al parsear el menú: '" + identifier + "'.");
                }
            }
        }
    }

    private FastMenu parse(String identifier, Configuration configuration) {
        if (configuration == null || configuration.isEmpty()) {
            LoggerFactory.warn("⚠️ ¡Advertencia! La configuración del menú está vacía o es nula.");
            return null;
        }
        try {
            return MenuParser.parse(identifier, configuration);
        } catch (Exception e) {
            LoggerFactory.error("☒ ¡Error! Durante el parseo de la configuración del menú: " + e.getMessage());
            return null;
        }
    }

    public List<FastMenu> getMenus() {
        return new ArrayList<>(menus);
    }

    public FastMenu getMenu(String identifier) {
        identifier = identifier + ".yml";
        String finalIdentifier = identifier;

        return menus.stream()
                .filter(menu -> menu.getIdentifier().equalsIgnoreCase(finalIdentifier))
                .findFirst()
                .orElse(null);
    }

    public void openMenu(Player player, String identifier) {
        FastMenu menu = getMenu(identifier);
        if (menu != null) {
            menu.executeMenu(player, new String[0]);
        } else {
            Utils.sendMSG(player, "&c¡Error! El menú con identificador '" + identifier + "' no existe.");
        }
    }

    public List<String> getMenuIdentifiers() {
        return menus.stream()
                .map(FastMenu::lowerCaseIdentifier)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    public void open(Player commandSender, FastMenu menu, String[] args) {
        menu.executeMenu(commandSender, args);
    }

    public void reloadMenus() {
        LoggerFactory.info("🔄 Recargando la configuración de los menús...");
        menus.clear();

        File[] files = menusDirectory.listFiles();
        if (files == null) {
            LoggerFactory.warn("⚠️ No se encontraron archivos en el directorio de menús durante la recarga.");
            return;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".yml")) {
                String identifier = file.getName();
                LoggerFactory.info("ℹ Cargando menú durante la recarga: '" + identifier + "'...");

                Configuration configuration = configurationManager.getConfig(new File(menusDirectory, identifier));
                FastMenu parsedMenu = parse(identifier, configuration);

                if (parsedMenu != null) {
                    menus.add(parsedMenu);
                    LoggerFactory.success("☑ Menú '" + identifier + "' recargado exitosamente.");
                } else {
                    LoggerFactory.error("☒ ¡Error! Fallo al parsear el menú durante la recarga: '" + identifier + "'.");
                }
            }
        }
        LoggerFactory.info("☑ Recarga de menús completada. Se cargaron " + menus.size() + " menús.");
    }
}