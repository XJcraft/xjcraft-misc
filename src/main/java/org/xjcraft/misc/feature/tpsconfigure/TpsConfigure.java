package org.xjcraft.misc.feature.tpsconfigure;

import lombok.Getter;
import org.xjcraft.misc.XJCraftMisc;
import org.xjcraft.misc.feature.tpsconfigure.beans.RateConf;
import org.xjcraft.misc.feature.tpsconfigure.listener.EntitySpawnListener;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 根据 TPS 自动调整配置
 *
 * @author Cat73
 */
public class TpsConfigure {
    /**
     * 检测间隔
     */
    private final int split;
    /**
     * 最大单次增量
     */
    private final double maxUp;
    /**
     * 配置的调整比例
     */
    private final List<RateConf> rates;
    /**
     * 当前使用的配置
     */
    @Getter
    private volatile RateConf nowRate;
    /**
     * now tps(考虑最大增量后的)
     */
    private volatile double tps = 20.0;
    /**
     * 上次执行时间
     */
    private volatile long lastTime = -1;

    public TpsConfigure(int split, double maxUp, List<RateConf> rates) {
        this.split = split;
        this.maxUp = maxUp;
        this.rates = rates;
        this.nowRate = this.rates.get(0);
    }

    /**
     * 启用功能
     */
    public static void enable() {
        var plugin = XJCraftMisc.plugin();
        var pluginManager = plugin.getServer().getPluginManager();
        var config = plugin.getConfig();

        // 读取配置
        var period = config.getInt("tps-configure.split");
        var maxUp = config.getInt("tps-configure.max-up");
        var ratesRaw = Objects.requireNonNull(config.getConfigurationSection("tps-configure.rates")).getValues(false);
        var rates = ratesRaw.entrySet().stream()
                .map(e -> new RateConf(Double.parseDouble(e.getKey()), (Double.parseDouble(String.valueOf(e.getValue())) / 100.0)))
                .sorted(Comparator.comparing(RateConf::getMinTps).reversed())
                .collect(Collectors.toList());

        // 构建实例
        var instance = new TpsConfigure(period, maxUp, rates);

        // 注册定时器
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, instance::tickLoop, 0, period);
        // 注册事件
        pluginManager.registerEvents(new EntitySpawnListener(instance), plugin);
    }

    /**
     * 按配置的 tick period 执行的 loop
     */
    public void tickLoop() {
        // 计算最新的 TPS
        var nowTime = System.currentTimeMillis();
        if (this.lastTime < 0) {
            this.lastTime = nowTime;
            return;
        }
        var time = nowTime - this.lastTime;
        this.lastTime = nowTime;
        var nowTps = Math.min(1000 / ((double) time / (double) this.split), 20.0);

        // 计算最新的用于控制的 TPS
        if (nowTps <= this.tps) {
            this.tps = nowTps;
        } else {
            var inc = nowTps - this.tps;
            inc = Math.min(inc, this.maxUp);
            this.tps = this.tps + inc;
        }

        // 刷新配置
        this.apply();
    }

    /**
     * 刷新配置
     */
    private void apply() {
        // 找到使用的配置
        RateConf usedRate = this.rates.get(this.rates.size() - 1);
        for (var rate : this.rates) {
            if (rate.getMinTps() <= this.tps) {
                usedRate = rate;
                break;
            }
        }
        this.nowRate = usedRate;

        // 应用配置
        usedRate.apply();
    }
}