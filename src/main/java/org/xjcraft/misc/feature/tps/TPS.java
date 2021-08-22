package org.xjcraft.misc.feature.tps;

import org.bukkit.Bukkit;
import org.xjcraft.misc.XJCraftMisc;
import org.xjcraft.misc.feature.tps.event.TPSEvent;

public class TPS {
    /**
     * 上次执行时间
     */
    private long lastTime = System.currentTimeMillis() - 1;

    public static void enable() {
        var plugin = XJCraftMisc.plugin();

        // 构建实例
        var instance = new TPS();

        plugin.getServer().getScheduler().runTaskTimer(plugin, instance::tickLoop, 1, 100);
    }

    /**
     * 按配置的 tick period 执行的 loop
     */
    public void tickLoop() {
        // 计算最新的 TPS
        var nowTime = System.currentTimeMillis();
        var time = nowTime - this.lastTime;
        this.lastTime = nowTime;
        var tps = 1000.0 / ((double) time / 100.0);

        Bukkit.getPluginManager().callEvent(new TPSEvent(tps, Math.min(tps, 20.0)));

    }
}
