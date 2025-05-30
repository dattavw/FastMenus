package hylexia.dev.fastMenus.actions;

import hylexia.dev.fastMenus.objects.Action;

public class PlayerChatAction extends Action {
    @Override
    public String getFormat() {
        return "player_chat";
    }

    @Override
    public void execute(String[] args) {
        String command = join(args);

        getOwner().chat(command);
    }
}
