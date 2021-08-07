package org.xjcraft.misc.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.xjcraft.misc.XJCraftMisc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 禁止一切命令执行的 Listener
 * <p>无视 OP 权限，对所有玩家一视同仁</p>
 * <p>有一个绕过机制，输入密码可绕过此限制，当前 Session 下有效</p>
 * <p>有一个白名单机制，运行特定命令被绕过</p>
 *
 * @author Cat73
 */
public class DenyAnyCommandListener implements Listener {
    private final boolean lockCB;
    /**
     * 绕过机制的密码
     */
    private final String unlockPassword;
    /**
     * 白名单命令
     */
    private final List<String> whiteList;
    /**
     * 已解锁的玩家列表
     */
    private final Set<String> unlockedPlayers = new HashSet<>();

    public DenyAnyCommandListener(XJCraftMisc plugin) {
        var config = plugin.getConfig();

        this.lockCB = config.getBoolean("deny-any-command.lock-cb");
        this.unlockPassword = config.getString("deny-any-command.unlock-password");
        this.whiteList = config.getStringList("deny-any-command.white-list");
    }

    // ==== event ====

    /**
     * 禁指令
     */
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        var player = event.getPlayer();

        // 白名单判定
        if (inUnlockedList(player)) {
            return;
        }

        // 解析指令
        var msg = event.getMessage();
        if (msg.startsWith("/")) {
            msg = msg.substring(1);
        }
        var params = msg.split(" ");

        // 判断解锁指令
        // 非注册指令方案，此方案被 Bukkit 描述为一定不要这么做
        // 但好处是解锁后，如果有其它插件亦注册了此指令，其可以无冲的正常使用
        if (params[0].equals("unlock")) {
            if (params[1].equals(this.unlockPassword)) {
                this.unlock(player);
                player.sendMessage("解锁成功.");
                event.setCancelled(true);
                return;
            }
        }

        // 判断白名单指令
        if (this.whiteList.contains(params[0])) {
            return;
        }

        // 默认失败
        player.sendMessage("您的指令被拒绝，请使用 /unlock <密码> 解锁.");
        event.setCancelled(true);
    }

    /**
     * 重新上线删除解锁状态
     */
    @EventHandler
    public void onPlayerExit(PlayerJoinEvent event) {
        this.lock(event.getPlayer());
    }

    /**
     * 禁编辑 CB
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        var player = event.getPlayer();

        // 功能启用 && 右键操作
        if (!this.lockCB || (event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        // 是 CB
        var block = event.getClickedBlock();
        if (block == null || !block.getType().name().contains("COMMAND")) {
            return;
        }

        // 白名单判定
        if (inUnlockedList(player)) {
            return;
        }

        // 默认失败
        player.sendMessage("您的操作被拒绝，请使用 /unlock <密码> 解锁.");
        event.getPlayer().closeInventory();
        event.setCancelled(true);
    }

    /**
     * 禁编辑 CB 矿车
     */
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        var player = event.getPlayer();

        // 功能启用 && 操作的 CB
        if (!this.lockCB || (event.getRightClicked().getType() != EntityType.MINECART_COMMAND)) {
            return;
        }

        // 白名单判定
        if (inUnlockedList(player)) {
            return;
        }

        // 默认失败
        player.sendMessage("您的操作被拒绝，请使用 /unlock <密码> 解锁.");
        event.getPlayer().closeInventory();
        event.setCancelled(true);
    }

    // ==== misc ====

    private void unlock(Player player) {
        this.unlockedPlayers.add(player.getName());
    }

    private void lock(Player player) {
        this.unlockedPlayers.remove(player.getName());
    }

    private boolean inUnlockedList(Player player) {
        return unlockedPlayers.contains(player.getName());
    }
}
