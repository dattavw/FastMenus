package hylexia.dev.fastMenus.actions;

import hylexia.dev.fastMenus.objects.Action;

public class CloseMenuAction extends Action {
    @Override
    public String getFormat() {
        return "close";
    }

    @Override
    public void execute(String[] args) {
        getOwner().closeInventory();
    }
}
