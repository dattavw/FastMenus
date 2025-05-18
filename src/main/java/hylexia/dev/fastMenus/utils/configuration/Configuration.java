package hylexia.dev.studio.utils.configuration;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Configuration extends YamlConfiguration {
    private final File file;

    public Configuration(File file) {
        this.file = file;
        reload();
    }

    public void reload() {
        try {
            load(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to reload " + file.getName(), e);
        }
    }

    public void save() {
        try {
            super.save(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save " + file.getName(), e);
        }
    }

    public void setIfNotExists(String path, Object value) {
        if (!contains(path)) {
            set(path, value);
            save();
        }
    }

    public void set(@NotNull String path, Object value) {
        super.set(path, value);
        save();
    }

    public Location getLocation(@NotNull String path) {
        String loc = getString(path + ".location");
        if (loc == null) return null;

        String[] parts = loc.split(";");
        if (parts.length != 6) return null;

        World world = Bukkit.getWorld(parts[5]);
        if (world == null) return null;

        try {
            return new Location(
                    world,
                    Double.parseDouble(parts[0]),
                    Double.parseDouble(parts[1]),
                    Double.parseDouble(parts[2]),
                    Float.parseFloat(parts[3]),
                    Float.parseFloat(parts[4])
            );
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void setLocation(String path, Location loc) {
        String locString = String.format("%f;%f;%f;%f;%f;%s",
                loc.getX(), loc.getY(), loc.getZ(),
                loc.getPitch(), loc.getYaw(),
                loc.getWorld().getName()
        );
        set(path + ".location", locString);
    }

    public Float getFloat(String path, float def) {
        setIfNotExists(path, def);
        return (float) getDouble(path, def);
    }

    public Float getFloat(String path) {
        if (!contains(path)) {
            return null;
        }
        return (float) getDouble(path);
    }

    public Sound getSound(String path, Sound def) {
        String name = getString(path, def.name());
        try {
            return Sound.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return def;
        }
    }

    @Override
    public String getString(String path, String def) {
        setIfNotExists(path, def);
        return super.getString(path, def);
    }

    @Override
    public boolean getBoolean(@NotNull String path, boolean def) {
        setIfNotExists(path, def);
        return super.getBoolean(path, def);
    }

    @Override
    public int getInt(@NotNull String path, int def) {
        setIfNotExists(path, def);
        return super.getInt(path, def);
    }

}