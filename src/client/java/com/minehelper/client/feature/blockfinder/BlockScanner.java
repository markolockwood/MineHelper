package com.minehelper.client.feature.blockfinder;

import com.minehelper.MineHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BlockScanner {
    private final BlockFinderConfig config;
    private Block targetBlock;
    private boolean enabled = false;

    private final Set<BlockPos> foundBlocks = ConcurrentHashMap.newKeySet();
    private final Set<Long> scannedChunks = ConcurrentHashMap.newKeySet();

    private final Queue<ChunkTask> chunkQueue = new LinkedList<>();
    private int tickCounter = 0;
    private BlockPos lastPlayerChunkPos = null;

    public BlockScanner(BlockFinderConfig config) {
        this.config = config;
    }

    public void setTargetBlock(Block block) {
        this.targetBlock = block;
        clear();
        MineHelper.LOGGER.info("Target block set to: {}", block);
    }

    public Block getTargetBlock() {
        return targetBlock;
    }

    public void setEnabled(boolean enabled) {
        boolean wasEnabled = this.enabled;
        this.enabled = enabled;
        MineHelper.LOGGER.info("Scanner {}", enabled ? "enabled" : "disabled");

        // Reset the scan cache whenever scanning turns off, so re-enabling
        // starts a fresh scan instead of showing stale highlights from
        // before the player moved. Otherwise the player would have to
        // manually run /blockfinder clear every time.
        if (wasEnabled && !enabled) {
            clear();
        }
    }

    public boolean isEnabled() {
        return enabled && targetBlock != null;
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void clear() {
        foundBlocks.clear();
        scannedChunks.clear();
        chunkQueue.clear();
        lastPlayerChunkPos = null;
        MineHelper.LOGGER.info("Scanner cache cleared");
    }

    public Set<BlockPos> getFoundBlocks() {
        return Collections.unmodifiableSet(foundBlocks);
    }

    public void tick(Minecraft client) {
        if (!isEnabled() || client.level == null || client.player == null) {
            return;
        }

        tickCounter++;
        if (tickCounter < config.getTicksPerScan()) {
            return;
        }
        tickCounter = 0;

        BlockPos playerPos = client.player.blockPosition();
        SectionPos playerChunkPos = SectionPos.of(playerPos);

        if (lastPlayerChunkPos == null || !lastPlayerChunkPos.equals(playerChunkPos.origin())) {
            lastPlayerChunkPos = playerChunkPos.origin();
            queueChunksAroundPlayer(client.level, playerPos);
        }

        processQueue(playerPos);
    }

    private void queueChunksAroundPlayer(Level level, BlockPos playerPos) {
        int chunkRadius = (config.getScanRadius() + 15) / 16;
        SectionPos playerChunk = SectionPos.of(playerPos);

        // Collect chunk offsets and sort by distance to player so scanning
        // radiates outward from the player instead of sweeping row by row.
        List<int[]> offsets = new ArrayList<>();
        for (int cx = -chunkRadius; cx <= chunkRadius; cx++) {
            for (int cz = -chunkRadius; cz <= chunkRadius; cz++) {
                offsets.add(new int[]{cx, cz});
            }
        }
        offsets.sort(Comparator.comparingInt(o -> o[0] * o[0] + o[1] * o[1]));

        for (int[] offset : offsets) {
            int chunkX = playerChunk.x() + offset[0];
            int chunkZ = playerChunk.z() + offset[1];
            long chunkKey = ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);

            if (!scannedChunks.contains(chunkKey)) {
                LevelChunk chunk = level.getChunk(chunkX, chunkZ);
                if (chunk != null) {
                    chunkQueue.offer(new ChunkTask(chunk, chunkX * 16, chunkZ * 16));
                    scannedChunks.add(chunkKey);
                }
            }
        }
    }

    private void processQueue(BlockPos playerPos) {
        int radiusSquared = config.getScanRadius() * config.getScanRadius();
        int processed = 0;

        while (!chunkQueue.isEmpty() && processed < config.getChunksPerScan()) {
            ChunkTask task = chunkQueue.poll();
            if (task == null) break;

            scanChunk(task, playerPos, radiusSquared);
            processed++;
        }
    }

    // Scans a chunk section-by-section (16x16x16 blocks). Each section's
    // palette is checked with maybeHas() first, which is a cheap lookup
    // against the (usually small) set of distinct block states in that
    // section. Only sections that could contain the target block are fully
    // scanned block-by-block. For rare blocks this skips the vast majority
    // of the world almost for free, instead of calling getBlockState() on
    // every single candidate position.
    private void scanChunk(ChunkTask task, BlockPos playerPos, int radiusSquared) {
        LevelChunk chunk = task.chunk;
        LevelChunkSection[] sections = chunk.getSections();
        int minSectionY = chunk.getMinSectionY();

        for (int i = 0; i < sections.length; i++) {
            LevelChunkSection section = sections[i];
            if (section == null || section.hasOnlyAir()) {
                continue;
            }
            if (!section.maybeHas(state -> state.getBlock() == targetBlock)) {
                continue;
            }

            int baseY = (minSectionY + i) * 16;

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        if (section.getBlockState(x, y, z).getBlock() != targetBlock) {
                            continue;
                        }

                        BlockPos pos = new BlockPos(task.baseX + x, baseY + y, task.baseZ + z);
                        if (playerPos.distSqr(pos) <= radiusSquared) {
                            foundBlocks.add(pos);
                        }
                    }
                }
            }
        }
    }

    private record ChunkTask(LevelChunk chunk, int baseX, int baseZ) {
    }
}
