package com.minehelper.client.feature.playeresp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minehelper.MineHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.player.AbstractClientPlayer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PlayerEspConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("mh_playeresp.json");

    public enum RenderMode { AABB, GLOW }
    public enum ColorMode  { SOLID, TEAM }

    private int   radius     = 64;
    private float lineWidth  = 2.0f;
    private RenderMode renderMode = RenderMode.AABB;
    private ColorMode  colorMode  = ColorMode.SOLID;
    private float[] solidColor = {1.0f, 0.0f, 0.0f, 1.0f};

    public int       getScanRadius()  { return radius; }
    public float     getLineWidth()   { return lineWidth; }
    public RenderMode getRenderMode() { return renderMode; }
    public ColorMode  getColorMode()  { return colorMode; }
    public float[]   getSolidColor()  { return solidColor; }

    public void setScanRadius(int v)         { radius    = Math.max(16, Math.min(256, v)); }
    public void setLineWidth(float v)        { lineWidth = v; }
    public void setRenderMode(RenderMode m)  { renderMode = m; }
    public void setColorMode(ColorMode m)    { colorMode  = m; }
    public void setSolidColor(float r, float g, float b, float a) {
        solidColor = new float[]{r, g, b, a};
    }

    // Shared by both the mixin (for glow mode) and the renderer (for AABB mode).
    public float[] resolveColor(AbstractClientPlayer player) {
        if (colorMode == ColorMode.TEAM) {
            // Entity.getTeamColor() returns the team color directly (works even when TabListDisplayName is null)
            int rgb = player.getTeamColor();
            return new float[]{
                ((rgb >> 16) & 0xFF) / 255f,
                ((rgb >>  8) & 0xFF) / 255f,
                ( rgb        & 0xFF) / 255f,
                1.0f
            };
        }
        return solidColor;
    }

    public void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (IOException e) {
            MineHelper.LOGGER.error("Failed to save PlayerESP config", e);
        }
    }

    public static PlayerEspConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                PlayerEspConfig cfg = GSON.fromJson(Files.readString(CONFIG_PATH), PlayerEspConfig.class);
                if (cfg != null) return cfg;
            } catch (IOException e) {
                MineHelper.LOGGER.error("Failed to load PlayerESP config", e);
            }
        }
        PlayerEspConfig cfg = new PlayerEspConfig();
        cfg.save();
        return cfg;
    }
}

