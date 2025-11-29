package cc.blocklife.touchstudio.listeners;

import cc.blocklife.touchstudio.BL_Hub;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        event.setJoinMessage(ChatColor.GREEN + "[+] " + ChatColor.YELLOW + playerName + ChatColor.GREEN + " 加入了服务器");

        // 隐藏玩家功能
        if (BL_Hub.getInstance().getConfig().getBoolean("hide-players", true)) {
            // 对新加入的玩家隐藏所有在线玩家
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.equals(player)) {
                    player.hidePlayer(BL_Hub.getInstance(), online);
                    online.hidePlayer(BL_Hub.getInstance(), player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();
        event.setQuitMessage(ChatColor.RED + "[-] " + ChatColor.YELLOW + playerName + ChatColor.RED + " 退出了服务器");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        // 如果聊天锁定开启，取消所有聊天消息
        if (BL_Hub.getInstance().getConfig().getBoolean("chat-locked", false)) {
            event.setCancelled(true);
        }
    }
}