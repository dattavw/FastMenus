package hylexia.dev.fastMenus.actions;

import hylexia.dev.fastMenus.objects.FastMenuAction;

public class PlayerChatAction extends FastMenuAction {
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
