package org.xjcraft.misc.feature.tps;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.xjcraft.misc.XJCraftMisc;
import org.xjcraft.misc.feature.AFeature;
import org.xjcraft.misc.feature.tps.event.TPSEvent;

/**
 * 计算 TPS
 *
 * @author Cat73
 */
public class TPS extends AFeature {
    /**
     * 最新的 TPS(可能大于 20)
     */
    @Getter
    private double tps = 20.0;
    /**
     * 上次执行时间
     */
    private long lastTime = System.currentTimeMillis() - 1;
    /**
     * 检查间隔
     */
    private final int checkRate = 1000;

    public void enable() {
        var plugin = XJCraftMisc.plugin();

        plugin.getServer().getScheduler().runTaskTimer(plugin, this::tickLoop, 1, checkRate);
    }

    /**
     * 按配置的 tick period 执行的 loop
     */
    public void tickLoop() {
        // 计算最新的 TPS
        var nowTime = System.currentTimeMillis();
        var time = nowTime - this.lastTime;
        this.lastTime = nowTime;
        var tps = 1000.0d * checkRate / time;
        this.tps = tps;

        Bukkit.getPluginManager().callEvent(new TPSEvent(tps, Math.min(tps, 20.0)));

    }
}
