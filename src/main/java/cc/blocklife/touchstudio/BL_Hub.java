package cc.blocklife.touchstudio;

import org.bukkit.plugin.java.JavaPlugin;
import cc.blocklife.touchstudio.managers.PluginManager;
import cc.blocklife.touchstudio.listeners.PlayerListener;
import cc.blocklife.touchstudio.listeners.CommandListener;
import cc.blocklife.touchstudio.listeners.AuthListener;
import cc.blocklife.touchstudio.listeners.WorldListener;

public class BL_Hub extends JavaPlugin {

    private static BL_Hub instance;

    public static BL_Hub getInstance() {
        return instance;
    }
    
    @Override
    public void onEnable() {
        instance = this;

        // 保存默认配置文件
        saveDefaultConfig();
        
        // 注册BungeeCord通道
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        
        // Initialize managers
        PluginManager.getInstance().initialize();
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new CommandListener(), this);
        getServer().getPluginManager().registerEvents(new AuthListener(), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);
        
        getLogger().info("BL_Hub has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("BL_Hub has been disabled!");
    }
    
}