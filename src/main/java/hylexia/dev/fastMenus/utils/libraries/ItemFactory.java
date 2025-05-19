package hylexia.dev.fastMenus.utils.libraries;

import hylexia.dev.fastMenus.utils.ParseUtils;
import hylexia.dev.fastMenus.utils.Utils;
import jdk.jshell.execution.Util;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;


@SuppressWarnings("ALL")
@Getter
public class ItemFactory {
    private Material material;
    private String displayName;
    private Player player;
    private List<String> lore = new ArrayList<>();
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();
    private Color leatherColor;
    private Integer customModelData;
    private String headPlayerName;
    private String headUrl;
    private boolean hideAll;
    private boolean unbreakable;
    private int amount = 1;  // Default amount to 1
    private final Map<EquipmentSlot, Map<String, AttributeModifier>> attributes = new HashMap<>();
    private final Set<ItemFlag> flags = new HashSet<>();

    public ItemFactory(Material material) {
        this.material = material;
        this.displayName = "&f";
    }

    public ItemFactory(Material material, String displayName) {
        this.material = material;
        this.displayName = displayName;
    }

    public ItemFactory(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            throw new IllegalArgumentException("ItemStack cannot be null and must have item meta.");
        }

        this.material = itemStack.getType();
        ItemMeta itemMeta = itemStack.getItemMeta();
        this.displayName = itemMeta.getDisplayName();

        if (itemMeta.hasEnchants()) {
            this.enchantments.putAll(itemMeta.getEnchants());
        }

        if (itemMeta.hasLore()) {
            this.lore = itemMeta.getLore();
        }

        if (itemMeta instanceof LeatherArmorMeta) {
            this.leatherColor = ((LeatherArmorMeta) itemMeta).getColor();
        }

        if (itemMeta instanceof SkullMeta) {
            this.headPlayerName = ((SkullMeta) itemMeta).getOwner();
        }

        if (itemMeta.hasCustomModelData()) {
            this.customModelData = itemMeta.getCustomModelData();
        }

        itemMeta.getAttributeModifiers().forEach((attribute, modifier) -> {
            this.attributes.computeIfAbsent(modifier.getSlot(), k -> new HashMap<>()).put(attribute.name(), modifier);
        });

        this.amount = itemStack.getAmount();
        this.unbreakable = itemMeta.isUnbreakable();
        this.flags.addAll(itemMeta.getItemFlags());
        this.hideAll = this.flags.contains(ItemFlag.HIDE_ATTRIBUTES);
    }


    public ItemFactory player(Player player) {
        this.player = player;
        return this;
    }

    public ItemFactory addEnchant(Enchantment enchantment, int power) {
        if (enchantment == null || power < 1) {
            throw new IllegalArgumentException("Enchantment cannot be null and power must be greater than 0.");
        }
        this.enchantments.put(enchantment, power);
        return this;
    }

    public ItemFactory setHeadPlayer(String name) {
        this.headPlayerName = name;
        return this;
    }

    public ItemFactory setHeadUrl(String url) {
        this.headUrl = url;
        return this;
    }

    public ItemFactory setLeatherColor(Color color) {
        this.leatherColor = color;
        return this;
    }

    public ItemFactory setCustomModelData(Integer data) {
        this.customModelData = data;
        return this;
    }

    public ItemFactory setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ItemFactory setType(Material material) {
        this.material = material;
        return this;
    }

    public ItemFactory setLore(List<String> lore) {
        this.lore = lore == null ? new ArrayList<>() : new ArrayList<>(lore);
        return this;
    }

    public ItemFactory setLore(String... lore) {
        this.lore = lore == null ? new ArrayList<>() : Arrays.asList(lore);
        return this;
    }

    public ItemFactory addAttribute(Attribute attribute, AttributeModifier modifier, EquipmentSlot slot) {
        if (attribute == null || modifier == null || slot == null) {
            throw new IllegalArgumentException("Attribute, modifier, and slot cannot be null.");
        }
        this.attributes.computeIfAbsent(slot, k -> new HashMap<>()).put(attribute.name(), modifier);
        return this;
    }

    public ItemFactory addLore(String... addLore) {
        if (addLore != null) {
            this.lore.addAll(Arrays.asList(addLore));
        }
        return this;
    }

    public ItemFactory addLore(List<String> addLore) {
        if (addLore != null) {
            this.lore.addAll(addLore);
        }
        return this;
    }

    public ItemFactory hideAll(boolean v) {
        this.hideAll = v;
        return this;
    }

    public ItemFactory setAmount(int amount) {
        if (amount < 1 || amount > 64) {
            throw new IllegalArgumentException("Amount must be between 1 and 64.");
        }
        this.amount = amount;
        return this;
    }

    public ItemFactory unbreakable(boolean b) {
        this.unbreakable = b;
        return this;
    }

    public ItemFactory withFlags(Set<ItemFlag> flags) {
        if (flags != null) {
            this.flags.addAll(flags);
        }
        return this;
    }

    public ItemStack build() {

        ItemStack base;
        if (headUrl != null && material == Material.PLAYER_HEAD) {
            base= SkullFactory.itemFromUrl(headUrl);
        }

        base= new ItemStack(material, amount);
        ItemMeta itemMeta = base.getItemMeta();

        if (itemMeta == null) {
            throw new IllegalStateException("ItemMeta cannot be null.");
        }

        String process = ParseUtils.processMSG(player, displayName);
        List<String> processLore = ParseUtils.processMSG(player, lore);

        itemMeta.setDisplayName(process);
        itemMeta.setLore(processLore);
        itemMeta.setUnbreakable(unbreakable);

        if (customModelData != null) {
            itemMeta.setCustomModelData(customModelData);
        }

        attributes.forEach((slot, attributeMap) -> {
            attributeMap.forEach((attributeName, modifier) -> {
                itemMeta.addAttributeModifier(Attribute.valueOf(attributeName), modifier);
            });
        });

        enchantments.forEach((enchantment, level) -> {
            itemMeta.addEnchant(enchantment, level, true);
        });

        if (hideAll) {
            itemMeta.addItemFlags(ItemFlag.values());
        } else {
            itemMeta.addItemFlags(flags.toArray(new ItemFlag[0]));
        }

        if (material.name().endsWith("_HEAD") && headPlayerName != null) {
            SkullMeta skullMeta = (SkullMeta) itemMeta;
            skullMeta.setOwner(headPlayerName);
            base.setItemMeta(skullMeta);
        } else if (material.name().startsWith("LEATHER_")) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemMeta;
            if (leatherColor != null) {
                leatherArmorMeta.setColor(leatherColor);
            }
            base.setItemMeta(leatherArmorMeta);
        } else {
            base.setItemMeta(itemMeta);
        }


        return base;
    }

    public void setMaterial(Material nextMaterial) {
        this.material = nextMaterial;
    }
}