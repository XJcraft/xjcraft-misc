package org.xjcraft.misc;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.plugin.java.JavaPlugin;
import org.xjcraft.misc.feature.denyanycommand.DenyAnyCommand;
import org.xjcraft.misc.feature.tps.TPS;
import org.xjcraft.misc.feature.tpsconfigure.TPSConfigure;

/**
 * 插件主类
 * @author Cat73
 */
public class XJCraftMisc extends JavaPlugin {
    /**
     * 插件的静态实例，方便操作用
     */
    @Getter @Accessors(fluent = true)
    private static XJCraftMisc plugin;

    // ==== features ====

    @Getter
    private final TPS tpsFeature = new TPS();
    @Getter
    private final DenyAnyCommand denyAnyCommandFeature = new DenyAnyCommand();
    @Getter
    private final TPSConfigure tpsConfigureFeature = new TPSConfigure();

    public XJCraftMisc() {
        XJCraftMisc.plugin = this;
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        var config = this.getConfig();

        // base feature
        // 计算 TPS
        this.tpsFeature.enableFeature();

        // 禁止一切命令执行
        if (config.getBoolean("deny-any-command.enable")) {
            this.denyAnyCommandFeature.enableFeature();
        }
        // 根据 TPS 自动调整配置
        if (config.getBoolean("tps-configure.enable")) {
            this.tpsConfigureFeature.enableFeature();
        }
    }
}
