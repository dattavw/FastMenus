package hylexia.dev.fastMenus.utils.libraries.objects;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MenuItem {
    private final ItemStack itemStack;

    private final Runnable leftClickAction;
    private final Runnable rightClickAction;

    public MenuItem(ItemStack itemStack, Runnable leftClickAction, Runnable rightClickAction) {
        this.itemStack = itemStack;
        this.leftClickAction = leftClickAction;
        this.rightClickAction = rightClickAction;
    }

    public MenuItem(ItemStack itemStack, Runnable action) {
        this.itemStack = itemStack;
        this.leftClickAction = action;
        this.rightClickAction = action;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Runnable getLeftClickAction() {
        return leftClickAction;
    }

    public Runnable getRightClickAction() {
        return rightClickAction;
    }


    public void executeAction(Player player, @NotNull ClickType clickType) {
        if (getItemStack() != null && getItemStack().getType() != Material.AIR) {
            player.playSound(player.getLocation(), Sound.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_ON, 0.5f, 2);
        }

        if (clickType.isLeftClick() && leftClickAction != null) {
            this.leftClickAction.run();
        }

        if (clickType.isRightClick() && rightClickAction != null) {
            this.rightClickAction.run();
        }
    }
}