package cc.blocklife.touchstudio.listeners;

import cc.blocklife.touchstudio.BL_Hub;
import fr.xephi.authme.events.LoginEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Collections;

public class AuthListener implements Listener {

    // 检查是否是传送钟
    private boolean isTeleportClock(ItemStack item) {
        if (item == null || item.getType() != Material.CLOCK) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasLore();
    }

    @EventHandler
    public void onLogin(LoginEvent event) {
        Player player = event.getPlayer();

        // 登录成功后显示所有玩家
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!online.equals(player)) {
                // 只显示已登录的玩家
                if (fr.xephi.authme.api.v3.AuthMeApi.getInstance().isAuthenticated(online)) {
                    player.showPlayer(BL_Hub.getInstance(), online);
                    online.showPlayer(BL_Hub.getInstance(), player);
                }
            }
        }
        
        // 检查是否开启自动传送
        if (BL_Hub.getInstance().getConfig().getBoolean("auto-teleport.enabled", false)) {
            String serverName = BL_Hub.getInstance().getConfig().getString("auto-teleport.server", "SMP");
            sendToServer(player, serverName);
            return;
        }
        
        // 检查玩家背包是否已有传送钟
        if (!hasClockInInventory(player)) {
            // 给予传送钟
            ItemStack clock = new ItemStack(Material.CLOCK);
            ItemMeta meta = clock.getItemMeta();
            if (meta != null) {
                String itemName = BL_Hub.getInstance().getConfig().getString("teleport-item.name", "&a点击传送到SMP服务器");
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));
                meta.setLore(Collections.singletonList(ChatColor.GRAY + "右键点击传送"));
                clock.setItemMeta(meta);
            }
            player.getInventory().setItem(4, clock); // 放在快捷栏中间
        }
    }

    // 检查玩家背包是否已有传送钟
    private boolean hasClockInInventory(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (isTeleportClock(item)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.CLOCK) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            
            // 获取配置的服务器名称
            String serverName = BL_Hub.getInstance().getConfig().getString("teleport-item.server", "SMP");
            
            // 通过BungeeCord传送玩家
            sendToServer(player, serverName);
        }
    }

    private void sendToServer(Player player, String serverName) {
        try {
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(byteArray);
            out.writeUTF("Connect");
            out.writeUTF(serverName);
            player.sendPluginMessage(BL_Hub.getInstance(), "BungeeCord", byteArray.toByteArray());
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "传送失败，请稍后再试");
        }
    }

    // 禁止在背包中移动钟
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();
        
        if (isTeleportClock(current) || isTeleportClock(cursor)) {
            event.setCancelled(true);
        }
    }

    // 禁止丢弃钟
    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        if (isTeleportClock(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }
}
