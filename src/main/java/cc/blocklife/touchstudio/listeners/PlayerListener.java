package cc.blocklife.touchstudio.listeners;

import cc.blocklife.touchstudio.BL_Hub;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        event.setJoinMessage(ChatColor.GREEN + "[+] " + ChatColor.YELLOW + playerName + ChatColor.GREEN + " 加入了服务器");

        // 发送欢迎消息和广告
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage(ChatColor.AQUA + "  ★ " + ChatColor.WHITE + "欢迎来到 " + ChatColor.GREEN + "BlockLife " + ChatColor.WHITE + "服务器！");
        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + "  ✦ " + ChatColor.WHITE + "永久服务器IP: " + ChatColor.LIGHT_PURPLE + "我的世界.net");
        player.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("");

        // 传送到出生点
        player.teleport(getSpawnLocation());

        // 设置无碰撞
        setNoCollision(player);

        // 登录前隐藏所有玩家
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!online.equals(player)) {
                player.hidePlayer(BL_Hub.getInstance(), online);
                online.hidePlayer(BL_Hub.getInstance(), player);
            }
        }

        // 给予药水效果
        if (BL_Hub.getInstance().getConfig().getBoolean("join-effects.enabled", true)) {
            int jumpLevel = BL_Hub.getInstance().getConfig().getInt("join-effects.jump-boost-level", 2);
            int speedLevel = BL_Hub.getInstance().getConfig().getInt("join-effects.speed-level", 2);
            
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, Integer.MAX_VALUE, jumpLevel - 1, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, speedLevel - 1, false, false));
        }
    }

    // 设置玩家无碰撞
    private void setNoCollision(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam("noCollision");
        if (team == null) {
            team = scoreboard.registerNewTeam("noCollision");
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        }
        team.addEntry(player.getName());
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

    // 虚空保护
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!BL_Hub.getInstance().getConfig().getBoolean("void-protection.enabled", true)) {
            return;
        }
        
        Player player = event.getPlayer();
        int minY = BL_Hub.getInstance().getConfig().getInt("void-protection.min-y", -64);
        
        if (player.getLocation().getY() < minY) {
            player.teleport(getSpawnLocation());
        }
    }

    private Location getSpawnLocation() {
        String worldName = BL_Hub.getInstance().getConfig().getString("spawn.world", "world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            world = Bukkit.getWorlds().get(0);
        }
        
        double x = BL_Hub.getInstance().getConfig().getDouble("spawn.x", 0.5);
        double y = BL_Hub.getInstance().getConfig().getDouble("spawn.y", 100.0);
        double z = BL_Hub.getInstance().getConfig().getDouble("spawn.z", 0.5);
        float yaw = (float) BL_Hub.getInstance().getConfig().getDouble("spawn.yaw", 0.0);
        float pitch = (float) BL_Hub.getInstance().getConfig().getDouble("spawn.pitch", 0.0);
        
        return new Location(world, x, y, z, yaw, pitch);
    }
}
