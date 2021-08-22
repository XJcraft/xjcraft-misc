package org.xjcraft.misc;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.plugin.java.JavaPlugin;
import org.xjcraft.misc.feature.denyanycommand.DenyAnyCommand;
import org.xjcraft.misc.feature.tps.TPS;
import org.xjcraft.misc.feature.tpsconfigure.TPSConfigure;

public class XJCraftMisc extends JavaPlugin {
    @Getter
    @Accessors(fluent = true)
    private static XJCraftMisc plugin;

    public XJCraftMisc() {
        XJCraftMisc.plugin = this;
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        var config = this.getConfig();

        // base feature
        TPS.enable();

        // 禁止一切命令执行
        if (config.getBoolean("deny-any-command.enable")) {
            DenyAnyCommand.enable();
        }
        // 根据 TPS 自动调整配置
        if (config.getBoolean("tps-configure.enable")) {
            TPSConfigure.enable();
        }
    }
}
