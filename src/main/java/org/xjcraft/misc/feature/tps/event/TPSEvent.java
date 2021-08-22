package org.xjcraft.misc.feature.tps.event;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * TPS 更新事件
 * @author Cat73
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class TPSEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /**
     * 当前的真实 TPS(可能大于 20)
     */
    double realTps;
    /**
     * 当前 TPS
     */
    double tps;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
