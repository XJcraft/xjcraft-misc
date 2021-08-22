package org.xjcraft.misc.feature.tpsconfigure;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.xjcraft.misc.XJCraftMisc;
import org.xjcraft.misc.feature.tps.event.TPSEvent;
import org.xjcraft.misc.feature.tpsconfigure.beans.RateConf;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 根据 TPS 自动调整配置
 *
 * @author Cat73
 */
public class TPSConfigure implements Listener {
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
    private RateConf nowRate;
    /**
     * now tps(考虑最大增量后的)
     */
    private double usedTps = 20.0;
    /**
     * 当前 TPS
     */
    private double nowTps = 20.0;

    public TPSConfigure(double maxUp, List<RateConf> rates) {
        this.maxUp = maxUp;
        this.rates = rates;
        this.nowRate = this.rates.get(0);
    }

    private static String fixX(String input) {
        return input.replace("x", ".");
    }

    /**
     * 启用功能
     */
    public static void enable() {
        var plugin = XJCraftMisc.plugin();
        var pluginManager = plugin.getServer().getPluginManager();
        var config = plugin.getConfig();

        // 读取配置
        var maxUp = config.getDouble("tps-configure.max-up");

        var ratesRaw = Objects.requireNonNull(config.getConfigurationSection("tps-configure.rates")).getValues(false);
        var rates = ratesRaw.entrySet().stream()
                .map(e -> new RateConf(Double.parseDouble(fixX(e.getKey())), (Double.parseDouble(String.valueOf(e.getValue())) / 100.0)))
                .sorted(Comparator.comparing(RateConf::getMinTps).reversed())
                .collect(Collectors.toList());

        if (rates.size() == 0) {
            throw new RuntimeException("未配置 TPSConfigure 中的 rates，请检查");
        }

        // 构建实例
        var instance = new TPSConfigure(maxUp, rates);

        // 注册事件
        pluginManager.registerEvents(instance, plugin); // TODO bad code
//        pluginManager.registerEvents(new EntitySpawnListener(instance), plugin);
    }

    /**
     * 按配置的 tick period 执行的 loop
     */
    @EventHandler
    public void onTpsChange(TPSEvent event) {
        this.nowTps = event.getTps();

        // 计算最新的用于控制的 TPS
        if (this.nowTps <= this.usedTps) {
            this.usedTps = this.nowTps;
        } else {
            var inc = this.nowTps - this.usedTps;
            inc = Math.min(inc, this.maxUp);
            this.usedTps = this.usedTps + inc;
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
            if (rate.getMinTps() <= this.usedTps) {
                usedRate = rate;
                break;
            }
        }
        if (this.nowRate != usedRate) {
            Bukkit.getConsoleSender().sendMessage(String.format("nowTps: %.2f, usedTps: %.2f, usedRate: %.2f, rateTps: %.2f", this.nowTps, this.usedTps, usedRate.getRate(), usedRate.getMinTps()));

            this.nowRate = usedRate;

            // 应用配置
            usedRate.apply();
        }
    }
}
