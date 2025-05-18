package hylexia.dev.studio.utils.libraries;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

public class SkullFactory {

    private SkullFactory() {}

    public static ItemStack createSkull() {
        return new ItemStack(Material.PLAYER_HEAD);
    }

    public static ItemStack itemFromName(String name) {
        ItemStack item = createSkull();
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        meta.setOwningPlayer(player);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack itemFromUuid(UUID id) {
        ItemStack item = createSkull();
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        OfflinePlayer player = Bukkit.getOfflinePlayer(id);
        meta.setOwningPlayer(player);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack itemFromUrl(String url) {
        if (url.contains("http://textures.minecraft.net/texture/")) {
            url = url.replace("http://textures.minecraft.net/texture/", "");
        }
        return itemFromTextureUrl("http://textures.minecraft.net/texture/" + url);
    }

    public static ItemStack itemFromBase64(String base64) {
        String decodedJson = new String(Base64.getDecoder().decode(base64));
        int start = decodedJson.indexOf("http");
        int end = decodedJson.indexOf("\"", start);
        String textureUrl = decodedJson.substring(start, end);
        return itemFromTextureUrl(textureUrl);
    }

    public static ItemStack itemFromTextureUrl(String textureUrl) {
        ItemStack skull = createSkull();
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        try {
            PlayerProfile profile = convertToPaperProfile(Bukkit.createPlayerProfile(UUID.randomUUID()));
            PlayerTextures textures = profile.getTextures();
            textures.setSkin(new URL(textureUrl));
            profile.setTextures(textures);

            meta.setPlayerProfile(profile);
            skull.setItemMeta(meta);
            return skull;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void blockWithUrl(Block block, String url) {
        if (url.contains("http://textures.minecraft.net/texture/")) {
            url = url.replace("http://textures.minecraft.net/texture/", "");
        }

        String fullUrl = "http://textures.minecraft.net/texture/" + url;
        try {
            block.setType(Material.PLAYER_HEAD, false);
            Skull state = (Skull) block.getState();

            PlayerProfile profile = convertToPaperProfile(Bukkit.createPlayerProfile(UUID.randomUUID()));
            profile.getTextures().setSkin(new URL(fullUrl));
            state.setPlayerProfile(profile);
            state.update(false, false);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
	public static PlayerProfile convertToPaperProfile(org.bukkit.profile.PlayerProfile profile) {
        return (PlayerProfile) profile;
    }
}
