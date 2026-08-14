package com.minehelper.client.feature.mobesp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minehelper.MineHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.NeutralMob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MobEspConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("mh_mobesp.json");

    public enum RenderMode { AABB, GLOW }
    public enum ColorMode  { SOLID, BY_TYPE }
    public enum MobFilter  { ALL, HOSTILE, PASSIVE }

    private int   radius     = 64;
    private float lineWidth  = 2.0f;
    private RenderMode renderMode = RenderMode.AABB;
    private ColorMode  colorMode  = ColorMode.BY_TYPE;
    private MobFilter  mobFilter  = MobFilter.ALL;
    private float[] solidColor = {1.0f, 1.0f, 0.0f, 1.0f}; // yellow default

    public int       getScanRadius()  { return radius; }
    public float     getLineWidth()   { return lineWidth; }
    public RenderMode getRenderMode() { return renderMode; }
    public ColorMode  getColorMode()  { return colorMode; }
    public MobFilter  getMobFilter()  { return mobFilter; }
    public float[]   getSolidColor()  { return solidColor; }

    public void setScanRadius(int v)         { radius    = Math.max(16, Math.min(256, v)); }
    public void setLineWidth(float v)        { lineWidth = v; }
    public void setRenderMode(RenderMode m)  { renderMode = m; }
    public void setColorMode(ColorMode m)    { colorMode  = m; }
    public void setMobFilter(MobFilter f)    { mobFilter  = f; }
    public void setSolidColor(float r, float g, float b, float a) {
        solidColor = new float[]{r, g, b, a};
    }

    public boolean shouldRender(LivingEntity mob) {
        return switch (mobFilter) {
            case ALL -> true;
            case HOSTILE -> mob instanceof Enemy;
            case PASSIVE -> !(mob instanceof Enemy);
        };
    }

    // Shared by both the mixin (for glow mode) and the renderer (for AABB mode).
    public float[] resolveColor(LivingEntity mob) {
        if (colorMode == ColorMode.BY_TYPE) {
            if (mob instanceof Enemy) {
                return new float[]{1.0f, 0.0f, 0.0f, 1.0f}; // red for hostile
            } else if (mob instanceof NeutralMob) {
                return new float[]{1.0f, 1.0f, 0.0f, 1.0f}; // yellow for neutral
            } else {
                return new float[]{0.0f, 1.0f, 0.0f, 1.0f}; // green for passive
            }
        }
        return solidColor;
    }

    public void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (IOException e) {
            MineHelper.LOGGER.error("Failed to save MobESP config", e);
        }
    }

    public static MobEspConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                MobEspConfig cfg = GSON.fromJson(Files.readString(CONFIG_PATH), MobEspConfig.class);
                if (cfg != null) return cfg;
            } catch (IOException e) {
                MineHelper.LOGGER.error("Failed to load MobESP config", e);
            }
        }
        MobEspConfig cfg = new MobEspConfig();
        cfg.save();
        return cfg;
    }
}
