package com.logisticscraft.logisticsapi.listeners;

import com.logisticscraft.logisticsapi.block.LogisticBlock;
import com.logisticscraft.logisticsapi.block.LogisticBlockCache;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import javax.inject.Inject;
import java.util.Map.Entry;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ChunkEventListener implements Listener {

    @Inject
    private LogisticBlockCache blockCache;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkLoad(ChunkLoadEvent event) {
        //TODO:BlockLoading
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (val data : blockCache.getLogisticBlocksInChunk(event.getChunk())) {
            blockCache.unloadLogisticBlock(data.getKey(), true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldLoad(WorldLoadEvent event) {
        blockCache.registerWorld(event.getWorld());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent event) {
        blockCache.unregisterWorld(event.getWorld());
    }

}
