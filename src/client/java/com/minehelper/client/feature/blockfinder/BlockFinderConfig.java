package com.minehelper.client.feature.blockfinder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minehelper.MineHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BlockFinderConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("mh_blockfinder.json");

    private int scanRadius = 64;
    private float[] highlightColor = {1.0f, 1.0f, 0.0f, 1.0f}; // Bright yellow, fully opaque
    private float lineWidth = 3.0f;
    private int ticksPerScan = 2;
    private int chunksPerScan = 8;

    public int getScanRadius() {
        return scanRadius;
    }

    public void setScanRadius(int scanRadius) {
        this.scanRadius = Math.max(16, Math.min(128, scanRadius));
    }

    public float[] getHighlightColor() {
        return highlightColor;
    }

    public void setHighlightColor(float r, float g, float b, float a) {
        this.highlightColor = new float[]{r, g, b, a};
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    public int getTicksPerScan() {
        return ticksPerScan;
    }

    public int getChunksPerScan() {
        return chunksPerScan;
    }

    public void save() {
        try {
            String json = GSON.toJson(this);
            Files.writeString(CONFIG_PATH, json);
            MineHelper.LOGGER.info("Config saved to {}", CONFIG_PATH);
        } catch (IOException e) {
            MineHelper.LOGGER.error("Failed to save config", e);
        }
    }

    public static BlockFinderConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                BlockFinderConfig config = GSON.fromJson(json, BlockFinderConfig.class);
                MineHelper.LOGGER.info("Config loaded from {}", CONFIG_PATH);
                return config;
            } catch (IOException e) {
                MineHelper.LOGGER.error("Failed to load config, using defaults", e);
            }
        }

        BlockFinderConfig config = new BlockFinderConfig();
        config.save();
        return config;
    }
}
