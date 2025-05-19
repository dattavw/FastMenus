package hylexia.dev.fastMenus.utils.configuration;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ConfigurationManager {
    private final JavaPlugin plugin;
    private final Cache<String, Configuration> configCache;

    public ConfigurationManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configCache = CacheBuilder.newBuilder()
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .build();
    }

    public Configuration getConfig(File file) {
        if (file == null || !file.exists()) {
            return null;
        }

        return new Configuration(file);
    }

    public Configuration getConfig(@NonNull String path) {
        try {
            return configCache.get(path, () -> loadConfiguration(path));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void reloadConfig(@NonNull String path) {
        configCache.invalidate(path);
        getConfig(path);
    }

    public void reloadAll() {
        configCache.invalidateAll();
    }

    private Configuration loadConfiguration(String path) {
        File file = new File(plugin.getDataFolder(), path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(path, false);
        }
        return new Configuration(file);
    }
}
