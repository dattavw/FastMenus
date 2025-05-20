package hylexia.dev.fastMenus.actions;

import hylexia.dev.fastMenus.FastMenus;
import hylexia.dev.fastMenus.objects.Action;
import hylexia.dev.fastMenus.utils.Utils;

public class OpenMenuAction extends Action {

    @Override
    public String getFormat() {
        return "open_menu";
    }

    @Override
    public void execute(String[] args) {

        String targetString;
        String menuIdentifier = args[0];

        // input: @a;menuName
        if (args.length > 2) {
            targetString = args[0];
            menuIdentifier = args[1];
        } else { // input: menuName
            targetString = getOwner().getName();
            menuIdentifier = args[0];
        }

        boolean b = FastMenus.getInstance().getFastMenuManager().existMenu(menuIdentifier);
        if (!b) {
            Utils.sendMSG(getOwner(), "[p] &cEl menú con identificador '" + menuIdentifier + "' no existe.");
            return;
        }

        String finalMenuIdentifier = menuIdentifier;
        fetchTarget(targetString).forEach(player -> {
            FastMenus.getInstance().getFastMenuManager().openMenu(player, finalMenuIdentifier);
        });

    }
}
