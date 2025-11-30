package cc.blocklife.touchstudio.listeners;

import cc.blocklife.touchstudio.BL_Hub;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

public class WorldListener implements Listener {

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        if (!BL_Hub.getInstance().getConfig().getBoolean("disable-water-flow", true)) {
            return;
        }
        
        Material type = event.getBlock().getType();
        if (type == Material.WATER || type == Material.LAVA) {
            event.setCancelled(true);
        }
    }
}
