package hylexia.dev.fastMenus.utils.libraries;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

public class SkullFactory {

    private SkullFactory() {
    }

    public static ItemStack createSkull() {
        return new ItemStack(Material.PLAYER_HEAD);
    }

    public static ItemStack itemFromName(String name) {
        if (name == null || name.isEmpty()) {
            return createSkull();
        }

        ItemStack item = createSkull();
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta == null) {
            return item;
        }

        try {
            if (isPaperServer()) {
                PlayerProfile profile = (PlayerProfile) Bukkit.createPlayerProfile(name);
                meta.setPlayerProfile(profile);
            } else {
                setNameWithReflection(meta, name);
            }
            item.setItemMeta(meta);
            return item;
        } catch (Exception e) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(name);
            meta.setOwningPlayer(player);
            item.setItemMeta(meta);
            return item;
        }
    }

    public static ItemStack itemFromUuid(UUID id) {
        ItemStack item = createSkull();
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(id);
            meta.setOwningPlayer(player);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack itemFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return createSkull();
        }

        if (url.startsWith("http://textures.minecraft.net/texture/")) {
            return itemFromTextureUrl(url);
        }

        if (url.contains("textures.minecraft.net/texture/")) {
            String textureId = url.substring(url.lastIndexOf("/") + 1);
            return itemFromTextureUrl("http://textures.minecraft.net/texture/" + textureId);
        }

        if (url.length() == 64 && url.matches("[a-fA-F0-9]+")) {
            return itemFromTextureUrl("http://textures.minecraft.net/texture/" + url);
        }

        return itemFromTextureUrl(url);
    }

    public static ItemStack itemFromBase64(String base64) {
        if (base64 == null || base64.isEmpty()) {
            return createSkull();
        }

        try {
            String decodedJson = new String(Base64.getDecoder().decode(base64));

            int urlStart = decodedJson.indexOf("\"url\":\"") + 7;
            if (urlStart == 6) {
                urlStart = decodedJson.indexOf("http");
            }

            if (urlStart == -1) {
                return createSkull();
            }

            int urlEnd = decodedJson.indexOf("\"", urlStart);
            if (urlEnd == -1) {
                urlEnd = decodedJson.length();
            }

            String textureUrl = decodedJson.substring(urlStart, urlEnd);
            return itemFromTextureUrl(textureUrl);

        } catch (Exception e) {
            return itemFromTextureUrlWithBase64(base64);
        }
    }

    public static ItemStack itemFromTextureUrl(String textureUrl) {
        if (textureUrl == null || textureUrl.isEmpty()) {
            return createSkull();
        }

        ItemStack skull = createSkull();
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta == null) {
            return skull;
        }

        try {
            if (isPaperServer()) {
                PlayerProfile profile = (PlayerProfile) Bukkit.createPlayerProfile(UUID.randomUUID());
                profile.getTextures().setSkin(new URL(textureUrl));
                meta.setPlayerProfile(profile);
            } else {
                setTextureWithReflection(meta, textureUrl);
            }

            skull.setItemMeta(meta);
            return skull;

        } catch (Exception e) {
            try {
                return itemFromTextureUrlWithBase64(createBase64FromUrl(textureUrl));
            } catch (Exception ex) {
                return skull;
            }
        }
    }

    private static ItemStack itemFromTextureUrlWithBase64(String base64) {
        ItemStack skull = createSkull();
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta == null) {
            return skull;
        }

        try {
            if (isPaperServer()) {
                PlayerProfile profile = (PlayerProfile) Bukkit.createPlayerProfile(UUID.randomUUID());
                profile.getProperties().add(new ProfileProperty("textures", base64));
                meta.setPlayerProfile(profile);
            } else {
                setTextureWithReflectionBase64(meta, base64);
            }

            skull.setItemMeta(meta);
            return skull;

        } catch (Exception e) {
            return skull;
        }
    }

    public static void blockWithUrl(Block block, String url) {
        if (url == null || url.isEmpty()) {
            return;
        }

        if (url.contains("http://textures.minecraft.net/texture/")) {
            url = url.replace("http://textures.minecraft.net/texture/", "");
        }

        String fullUrl = "http://textures.minecraft.net/texture/" + url;

        try {
            block.setType(Material.PLAYER_HEAD, false);
            Skull state = (Skull) block.getState();

            if (isPaperServer()) {
                PlayerProfile profile = (PlayerProfile) Bukkit.createPlayerProfile(UUID.randomUUID());
                profile.getTextures().setSkin(new URL(fullUrl));
                state.setPlayerProfile(profile);
            }

            state.update(false, false);

        } catch (Exception e) {
            try {
                block.setType(Material.PLAYER_HEAD, false);
                Skull state = (Skull) block.getState();

                if (isPaperServer()) {
                    PlayerProfile profile = (PlayerProfile) Bukkit.createPlayerProfile(UUID.randomUUID());
                    profile.getProperties().add(new ProfileProperty("textures", createBase64FromUrl(fullUrl)));
                    state.setPlayerProfile(profile);
                    state.update(false, false);
                }

            } catch (Exception ex) {
                // Silent fail
            }
        }
    }

    private static boolean isPaperServer() {
        try {
            Class.forName("com.destroystokyo.paper.profile.PlayerProfile");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static void setTextureWithReflection(SkullMeta meta, String url) throws Exception {
        Field profileField = meta.getClass().getDeclaredField("profile");
        profileField.setAccessible(true);

        Object profile = profileField.get(meta);
        if (profile == null) {
            profile = Class.forName("com.mojang.authlib.GameProfile")
                    .getConstructor(UUID.class, String.class)
                    .newInstance(UUID.randomUUID(), null);
            profileField.set(meta, profile);
        }

        Object properties = profile.getClass().getMethod("getProperties").invoke(profile);
        String base64 = createBase64FromUrl(url);

        Object property = Class.forName("com.mojang.authlib.properties.Property")
                .getConstructor(String.class, String.class)
                .newInstance("textures", base64);

        properties.getClass().getMethod("put", Object.class, Object.class)
                .invoke(properties, "textures", property);
    }

    private static void setTextureWithReflectionBase64(SkullMeta meta, String base64) throws Exception {
        Field profileField = meta.getClass().getDeclaredField("profile");
        profileField.setAccessible(true);

        Object profile = profileField.get(meta);
        if (profile == null) {
            profile = Class.forName("com.mojang.authlib.GameProfile")
                    .getConstructor(UUID.class, String.class)
                    .newInstance(UUID.randomUUID(), null);
            profileField.set(meta, profile);
        }

        Object properties = profile.getClass().getMethod("getProperties").invoke(profile);

        Object property = Class.forName("com.mojang.authlib.properties.Property")
                .getConstructor(String.class, String.class)
                .newInstance("textures", base64);

        properties.getClass().getMethod("put", Object.class, Object.class)
                .invoke(properties, "textures", property);
    }

    private static void setNameWithReflection(SkullMeta meta, String name) throws Exception {
        Field profileField = meta.getClass().getDeclaredField("profile");
        profileField.setAccessible(true);

        Object profile = Class.forName("com.mojang.authlib.GameProfile")
                .getConstructor(UUID.class, String.class)
                .newInstance(null, name);

        profileField.set(meta, profile);
    }

    private static String createBase64FromUrl(String url) {
        String json = "{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}";
        return Base64.getEncoder().encodeToString(json.getBytes());
    }
}