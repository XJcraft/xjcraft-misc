package org.xjcraft.misc.feature.tpsconfigure.beans;

import lombok.Value;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.xjcraft.misc.util.Refs;

/**
 * 限制配置
 */
@Value
public class RateConf {
    /**
     * 大于多少 TPS 时
     */
    double minTps;
    /**
     * 限制比例
     */
    double rate;

    // bukkit.yml
    // spawn-limits - 生物上限
    int spawnLimitsMonsters;
    int spawnLimitsAnimals;
    int spawnLimitsWaterAnimals;
    int spawnLimitsWaterAmbient;
    int spawnLimitsAmbient;
    // 生物生成成功率
    double spawnsSuccessRate;

    // spigot.yml
    // world-settings.default.entity-activation-range
    int entityActivationRangeAnimals;
    int entityActivationRangeMonsters;
    int entityActivationRangeRaider;
    int entityActivationRangeMisc;
    // world-settings.default.max-tnt-per-tick
    int maxTntTicksPerTick;

    /**
     * 按比例减少值，最小输出为 1
     * @param val 被减少的值
     * @param rate 减少比例
     * @return 减少后的值
     */
    public static int sub(int val, double rate) {
        return (int) Math.max(val * rate, 1);
    }

    public RateConf(double minTps, double rate) {
        this.minTps = minTps;
        this.rate = rate;

        // 初始化限制值
        var server = Bukkit.getServer();
        try {
            var world = Bukkit.getServer().getWorlds().get(0);
            var handle = Refs.getFieldValue(world, "world");
            var spigotConfig = Refs.getFieldValue(handle, "spigotConfig");

            this.spawnLimitsMonsters = sub((int) Refs.getFieldValue(server, "monsterSpawn"), this.rate);
            this.spawnLimitsAnimals = sub((int) Refs.getFieldValue(server, "animalSpawn"), this.rate);
            this.spawnLimitsWaterAnimals = sub((int) Refs.getFieldValue(server, "waterAnimalSpawn"), this.rate);
            this.spawnLimitsWaterAmbient = sub((int) Refs.getFieldValue(server, "waterAmbientSpawn"), this.rate);
            this.spawnLimitsAmbient = sub((int) Refs.getFieldValue(server, "ambientSpawn"), this.rate);
            this.spawnsSuccessRate = this.rate;
            this.entityActivationRangeAnimals = sub((int) Refs.getFieldValue(spigotConfig, "animalActivationRange"), this.rate);
            this.entityActivationRangeMonsters = sub((int) Refs.getFieldValue(spigotConfig, "monsterActivationRange"), this.rate);
            this.entityActivationRangeRaider = sub((int) Refs.getFieldValue(spigotConfig, "raiderActivationRange"), this.rate);
            this.entityActivationRangeMisc = sub((int) Refs.getFieldValue(spigotConfig, "miscActivationRange"), this.rate);
            this.maxTntTicksPerTick = sub((int) Refs.getFieldValue(spigotConfig, "maxTntTicksPerTick"), this.rate);
        } catch (Exception e) {
            throw new RuntimeException("初始化失败了，请检查反射代码，也许是版本兼容问题", e);
        }
    }

    /**
     * 应用此配置
     */
    public void apply() {
        var server = Bukkit.getServer();
        try {
            Refs.setFieldValue(server, "monsterSpawn", this.spawnLimitsMonsters);
            Refs.setFieldValue(server, "animalSpawn", this.spawnLimitsAnimals);
            Refs.setFieldValue(server, "waterAnimalSpawn", this.spawnLimitsWaterAnimals);
            Refs.setFieldValue(server, "waterAmbientSpawn", this.spawnLimitsWaterAmbient);
            Refs.setFieldValue(server, "ambientSpawn", this.spawnLimitsAmbient);
            for (World world : Bukkit.getServer().getWorlds()) {
                var handle = Refs.getFieldValue(world, "world");
                var spigotConfig = Refs.getFieldValue(handle, "spigotConfig");

                Refs.setFieldValue(spigotConfig, "animalActivationRange", this.entityActivationRangeAnimals);
                Refs.setFieldValue(spigotConfig, "monsterActivationRange", this.entityActivationRangeMonsters);
                Refs.setFieldValue(spigotConfig, "raiderActivationRange", this.entityActivationRangeRaider);
                Refs.setFieldValue(spigotConfig, "miscActivationRange", this.entityActivationRangeMisc);
                Refs.setFieldValue(spigotConfig, "maxTntTicksPerTick", this.maxTntTicksPerTick);
            }
        } catch (Exception e) {
            throw new RuntimeException("初始化失败了，请检查反射代码，也许是版本兼容问题", e);
        }
    }
}
