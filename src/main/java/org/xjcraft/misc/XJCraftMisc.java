package org.xjcraft.misc;

import org.bukkit.plugin.java.JavaPlugin;
import org.xjcraft.misc.listeners.DenyAnyCommandListener;

public class XJCraftMisc extends JavaPlugin {
    @Override
    public void onEnable() {
        var pluginManager = this.getServer().getPluginManager();
        var config = this.getConfig();

        // 禁止一切命令执行
        if (config.getBoolean("deny-any-command.enable")) {
            pluginManager.registerEvents(new DenyAnyCommandListener(this), this);
        }
    }
}
