package hylexia.dev.fastMenus.actions;

import hylexia.dev.fastMenus.objects.Action;

public class PlayerCommandAction extends Action {
    @Override
    public String getFormat() {
        return "player_cmd";
    }

    @Override
    public void execute(String[] args) {
        String command = join(args);

        if (command.startsWith("/")) command = command.substring(1);

        getOwner().performCommand(command);
    }
}
