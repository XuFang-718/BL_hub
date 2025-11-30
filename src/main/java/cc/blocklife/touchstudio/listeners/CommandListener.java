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

    // 需要隐藏密码的指令
    private static final Set<String> PASSWORD_COMMANDS = new HashSet<>(java.util.Arrays.asList(
            "l", "login", "register", "reg"
    ));

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().toLowerCase();
        String command = message.substring(1).split(" ")[0];

        if (!getAllowedCommands().contains(command)) {
            event.setCancelled(true);
        }
    }

    // 阻止登录指令消息发送到聊天框 (防止密码泄露)
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(org.bukkit.event.player.AsyncPlayerChatEvent event) {
        String msg = event.getMessage().toLowerCase();
        // 如果消息以登录指令开头，取消发送
        for (String cmd : PASSWORD_COMMANDS) {
            if (msg.startsWith("/" + cmd + " ") || msg.equals("/" + cmd)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onCommandSend(PlayerCommandSendEvent event) {
        Set<String> allowed = getAllowedCommands();
        event.getCommands().removeIf(cmd -> !allowed.contains(cmd.toLowerCase()));
    }
}
