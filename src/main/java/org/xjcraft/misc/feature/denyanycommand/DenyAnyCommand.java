package org.xjcraft.misc.feature.denyanycommand;

import org.xjcraft.misc.XJCraftMisc;
import org.xjcraft.misc.feature.denyanycommand.listener.DenyAnyCommandListener;

/**
 * 禁止一切命令执行
 * <p>无视 OP 权限，对所有玩家一视同仁</p>
 * <p>有一个绕过机制，输入密码可绕过此限制，当前 Session 下有效</p>
 * <p>有一个白名单机制，运行特定命令被绕过</p>
 *
 * @author Cat73
 */
public class DenyAnyCommand {
    /**
     * 启用功能
     */
    public static void enable() {
        var plugin = XJCraftMisc.plugin();
        var pluginManager = plugin.getServer().getPluginManager();

        // 注册事件
        pluginManager.registerEvents(new DenyAnyCommandListener(plugin), plugin);
    }
}
