package org.xjcraft.misc.feature.tpsconfigure.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.xjcraft.misc.feature.tpsconfigure.TpsConfigure;

import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class EntitySpawnListener  implements Listener {
    private final TpsConfigure baseFeature;

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (ThreadLocalRandom.current().nextDouble() < this.baseFeature.getNowRate().getSpawnsSuccessRate()) {
            return; // 生成成功
        }

        // 生成失败
        event.setCancelled(true);
    }
}
