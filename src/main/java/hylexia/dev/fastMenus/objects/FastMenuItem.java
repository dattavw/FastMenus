package hylexia.dev.fastMenus.objects;

import hylexia.dev.fastMenus.utils.ParseUtils;
import hylexia.dev.fastMenus.utils.Utils;
import hylexia.dev.fastMenus.utils.libraries.ItemFactory;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public class FastMenuItem {

    private int xSlot;
    private int ySlot;

    private String displayName;
    private final List<Material> materials;
    private List<String> lore;

    private Set<ItemFlag> itemFlags = new HashSet<>();
    private Integer customModelData;

    private String headUrl;
    private String headPlayerName;

    private boolean hideFlags = false;

    private Runnable leftClickAction;
    private Runnable rightClickAction;

    private int currentMaterialIndex = 0;
    private String lowerCaseIdentifier;

    public FastMenuItem(int xSlot, int ySlot, String displayName, List<Material> material, List<String> lore) {
        this.xSlot = xSlot;
        this.ySlot = ySlot;
        this.displayName = displayName;
        this.materials = material;
        this.lore = new ArrayList<>(lore);
        this.lowerCaseIdentifier = (xSlot + "," + ySlot).toLowerCase();
    }

    public FastMenuItem(int xSlot, int ySlot, String displayName, Material material, List<String> lore) {
        this.xSlot = xSlot;
        this.ySlot = ySlot;
        this.displayName = displayName;
        this.materials = List.of(material);
        this.lore = new ArrayList<>(lore);
        this.lowerCaseIdentifier = (xSlot + "," + ySlot).toLowerCase();
    }

    public FastMenuItem setItemFlags(Set<ItemFlag> itemFlags) {
        this.itemFlags = new HashSet<>(itemFlags);
        return this;
    }

    public Material getRandomMaterial() {
        if (materials.isEmpty()) {
            return Material.AIR;
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(materials.size());
        return materials.get(randomIndex);
    }

    public Material getMaterial() {
        return materials.size() > currentMaterialIndex ? materials.get(currentMaterialIndex) : materials.getFirst();
    }

    public ItemFactory getItem(Player player) {
        ItemFactory itemFactory = new ItemFactory(getRandomMaterial(), displayName);
        itemFactory.setDisplayName(ParseUtils.processMSG(player, displayName));
        itemFactory.setLore(ParseUtils.processMSG(player, lore));

        if (customModelData != null) {
            itemFactory.setCustomModelData(customModelData);
        }

        if (hideFlags) {
            itemFactory.hideAll(true);
        } else if (!itemFlags.isEmpty()) {
            itemFactory.withFlags(itemFlags);
        }

        if (headUrl != null) {
            itemFactory.setHeadUrl(headUrl);
        }

        if (headPlayerName != null) {
            itemFactory.setHeadPlayer(headPlayerName);
        }

        itemFactory.setDisplayName(displayName);

        return itemFactory;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }
}