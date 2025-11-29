package cc.blocklife.touchstudio.listeners;

import cc.blocklife.touchstudio.BL_Hub;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandListener implements Listener {

    private Set<String> getAllowedCommands() {
        List<String> list = BL_Hub.getInstance().getConfig().getStringList("allowed-commands");
        Set<String> commands = new HashSet<>();
        for (String cmd : list) {
            commands.add(cmd.toLowerCase());
        }
        return commands;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().toLowerCase();
        String command = message.substring(1).split(" ")[0];

        if (!getAllowedCommands().contains(command)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommandSend(PlayerCommandSendEvent event) {
        Set<String> allowed = getAllowedCommands();
        event.getCommands().removeIf(cmd -> !allowed.contains(cmd.toLowerCase()));
    }
}
