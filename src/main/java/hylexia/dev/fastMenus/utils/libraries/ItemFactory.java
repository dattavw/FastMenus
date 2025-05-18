package hylexia.dev.studio.utils.libraries;

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

import static hylexia.dev.studio.utils.Utils.colorize;


@SuppressWarnings("ALL")
@Getter
public class ItemFactory {
    private Material material;
    private String displayName;
    private Player player;
    private List<String> lore = new ArrayList<>();
    private final Map<Enchantment, Integer> enchantmentIntegerMap = new HashMap<>();
    private Color leatherColor;
    private int customModelData;
    private String headPlayerName;
    private String headUrl;
    private boolean hideAll;
    private boolean unbreakable;
    private int amount = 1;  // Default amount to 1
    private final Map<EquipmentSlot, Map<String, AttributeModifier>> slotAttributes = new HashMap<>();

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
            for (Enchantment enchantment : itemMeta.getEnchants().keySet()) {
                this.enchantmentIntegerMap.put(enchantment, itemMeta.getEnchantLevel(enchantment));
            }
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

/*        // Copiar atributos
        for (Attribute attribute : Attribute.values()) {
            if (itemMeta.hasAttributeModifiers().) {
                for (AttributeModifier modifier : itemMeta.getAttributeModifiers(attribute)) {
                    this.slotAttributes.computeIfAbsent(modifier.getSlot(), k -> new HashMap<>()).put(attribute.name(), modifier);
                }
            }
        }*/

        this.amount = itemStack.getAmount();

        if (itemMeta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) {
            this.hideAll = true;
        }

        this.unbreakable = itemMeta.isUnbreakable();
    }


    public ItemFactory player(Player player) {
        this.player = player;
        return this;
    }

    public ItemFactory addEnchant(Enchantment enchantment, int power) {
        if (enchantment == null || power < 1) {
            throw new IllegalArgumentException("Enchantment cannot be null and power must be greater than 0.");
        }
        enchantmentIntegerMap.put(enchantment, power);
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

    public ItemFactory setCustomModelData(int data) {
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
        if (lore == null) {
            throw new IllegalArgumentException("Lore cannot be null.");
        }
        this.lore = new ArrayList<>(lore);
        return this;
    }

    public ItemFactory setLore(String... lore) {
        if (lore == null || lore.length == 0) {
            throw new IllegalArgumentException("Lore cannot be null or empty.");
        }
        this.lore = Arrays.asList(lore);
        return this;
    }

    public ItemFactory addAttribute(Attribute attribute, AttributeModifier modifier, EquipmentSlot slot) {
        if (attribute == null || modifier == null || slot == null) {
            throw new IllegalArgumentException("Attribute, modifier, and slot cannot be null.");
        }
        slotAttributes.computeIfAbsent(slot, k -> new HashMap<>()).put(attribute.name(), modifier);
        return this;
    }

    public ItemFactory addLore(String... addLore) {
        if (addLore == null || addLore.length == 0) {
            throw new IllegalArgumentException("Lore to add cannot be null or empty.");
        }
        lore.addAll(Arrays.asList(addLore));
        return this;
    }

    public ItemFactory addLore(List<String> addLore) {
        if (addLore.isEmpty()) addLore = new ArrayList<>();

        lore.addAll(addLore);
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

    public ItemStack build() {
        ItemStack itemStack = new ItemStack(material);

        if (headUrl != null && material == Material.PLAYER_HEAD) {
            itemStack = SkullFactory.itemFromUrl(headUrl);
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            throw new IllegalStateException("ItemMeta cannot be null.");
        }

        itemMeta.setDisplayName(colorize(player, displayName));

        if (customModelData > 0) {
            itemMeta.setCustomModelData(customModelData);
        }

        if (lore != null && !lore.isEmpty()) {
            itemMeta.setLore(colorize(player, lore));
        }

        for (Map.Entry<EquipmentSlot, Map<String, AttributeModifier>> slotEntry : slotAttributes.entrySet()) {
            EquipmentSlot slot = slotEntry.getKey();
            Map<String, AttributeModifier> attributes = slotEntry.getValue();
            for (Map.Entry<String, AttributeModifier> entry : attributes.entrySet()) {
                AttributeModifier attributeModifier = entry.getValue();
                itemMeta.addAttributeModifier(Attribute.valueOf(entry.getKey()), attributeModifier);
            }
        }

        for (Map.Entry<Enchantment, Integer> entry : enchantmentIntegerMap.entrySet()) {
            itemMeta.addEnchant(entry.getKey(), entry.getValue(), true);
        }

        itemMeta.setUnbreakable(unbreakable);

        if (hideAll) {
            itemMeta.addItemFlags(ItemFlag.values());
        }

        if (material == Material.LEATHER_HELMET || material == Material.LEATHER_CHESTPLATE ||
                material == Material.LEATHER_LEGGINGS || material == Material.LEATHER_BOOTS) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemMeta;
            if (leatherColor != null) {
                leatherArmorMeta.setColor(leatherColor);
            }
            itemStack.setItemMeta(leatherArmorMeta);

        } else if (material == Material.PLAYER_HEAD) {
            SkullMeta skullMeta = (SkullMeta) itemMeta;
            if (headPlayerName != null) {
                skullMeta.setOwner(headPlayerName);
            }
            itemStack.setItemMeta(skullMeta);

        } else {
            itemStack.setItemMeta(itemMeta);
        }

        itemStack.setAmount(amount);
        return itemStack;
    }
}
