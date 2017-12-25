package com.logisticscraft.logisticsapi.block;

import com.logisticscraft.logisticsapi.data.SafeBlockLocation;
import com.logisticscraft.logisticsapi.persistence.PersistenceStorage;
import de.tr7zw.itemnbtapi.NBTCompound;
import de.tr7zw.itemnbtapi.NBTFile;
import lombok.Getter;
import lombok.NonNull;
import lombok.Synchronized;
import lombok.val;
import org.bukkit.Chunk;
import org.bukkit.World;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class LogisticWorldStorage {

    @Inject
    private PersistenceStorage persistence;

    @Getter
    private World world;
    @Getter
    private NBTFile nbtFile;

    public LogisticWorldStorage(@NonNull final World world) throws IOException {
        this.world = world;
        nbtFile = new NBTFile(new File(world.getWorldFolder(), "logisticblocks.nbt"));
        nbtFile.addCompound("chunks");
    }

    public void save() throws IOException {
        nbtFile.save();
    }

    @Synchronized
    public Optional<Set<? extends LogisticBlock>> getLogisticBlocksInChunk(@NonNull final Chunk chunk) {
        val chunks = nbtFile.getCompound("chunks");

        val chunkData = chunks.getCompound(chunk.getX() + ";" + chunk.getZ());
        if (chunkData == null) return Optional.empty();

        val blocks = new HashSet<LogisticBlock>();
        for (String key : chunkData.getKeys()) {
            val blockData = chunkData.getCompound(key);
            blocks.add(persistence.loadObject(LogisticBlock.class, blockData));
        }

        if (blocks.isEmpty()) return Optional.empty(); // Just in case
        return Optional.of(blocks);
    }

    @Synchronized
    public void removeLogisticBlock(@NonNull final LogisticBlock logisticBlock) {
        val chunks = nbtFile.getCompound("chunks");

        val location = logisticBlock.getLocation();
        val chunkData = chunks.getCompound(location.getChunkX() + ";" + location.getChunkZ());
        if (chunkData == null) return;

        if (chunkData.hasKey(location.getX() + ";" + location.getY() + ";" + location.getZ())) {
            chunkData.removeKey(location.getX() + ";" + location.getY() + ";" + location.getZ());
        }
        if (chunkData.getKeys().size() == 0) {
            chunks.removeKey(location.getChunkX() + ";" + location.getChunkZ());
        }
    }

    @Synchronized
    public void saveLogisticBlock(@NonNull final LogisticBlock logisticBlock) {
        val chunks = nbtFile.getCompound("chunks");

        val location = logisticBlock.getLocation();
        val chunkData = chunks.addCompound(location.getChunkX() + ";" + location.getChunkZ());
        val blockData = chunkData.addCompound(location.getX() + ";" + location.getY() + ";" + location.getZ());
        persistence.saveFields(logisticBlock, blockData);
        //TODO: Save blocktype, energy levels, Blockdata etc.
    }

    //TODO: Loading blocks

}
