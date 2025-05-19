package hylexia.dev.fastMenus;

import hylexia.dev.fastMenus.commands.MainCommand;
import hylexia.dev.fastMenus.managers.FastMenuManager;
import hylexia.dev.fastMenus.objects.MenuTask;
import hylexia.dev.fastMenus.utils.configuration.Configuration;
import hylexia.dev.fastMenus.utils.configuration.ConfigurationManager;
import hylexia.dev.fastMenus.utils.libraries.LoggerFactory;
import hylexia.dev.fastMenus.managers.MenuFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.util.logging.Logger;

@Slf4j
@Getter
public final class FastMenus extends JavaPlugin {

    @Getter
    private static FastMenus instance;

    private ConfigurationManager configurationManager;
    private Configuration configuration;

    private MenuFactory menuFactory;
    private LoggerFactory loggerFactory;

    private FastMenuManager fastMenuManager;

    private boolean debug;
    private String prefix;
    private MenuTask task;
    private int updateInterval;

    @Override
    public void onEnable() {
        instance = this;

        configurationManager = new ConfigurationManager(this);
        configuration = configurationManager.getConfig("config.yml");

        menuFactory = new MenuFactory(this, configuration.getInt("update-interval"));
        loggerFactory = new LoggerFactory(this);
        loggerFactory.disabled = !configuration.getBoolean("debug");

        fastMenuManager = new FastMenuManager(this);

        getCommand("fastmenus").setExecutor(new MainCommand());
        getCommand("fastmenus").setTabCompleter(new MainCommand());

        reload();

        task = new MenuTask(this);
        task.runTaskTimer(this, 0L, 1L);

        LoggerFactory.success("&aSe ha cargado con éxito la versión &7".formatted(getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        LoggerFactory.success("&cSe ha desactivado con éxito");
    }

    public void reload() {
        reloadConfig();

        prefix = configuration.getString("prefix", "&e&lFastMenus &8»&r");
        debug = configuration.getBoolean("debug", false);
        updateInterval = configuration.getInt("update-interval");

        LoggerFactory.disabled = !debug;
        LoggerFactory.prefix = prefix;

        fastMenuManager.reloadMenus();

        LoggerFactory.success("Se ha cargado la configuración con éxito.");
        LoggerFactory.info("debugMode: " + debug);
        LoggerFactory.info("updateInterval: " + updateInterval);
    }
}