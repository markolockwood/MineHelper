package com.minehelper.client.feature;

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Central registry for {@link Feature}s. A single tick listener and a single
 * render listener are wired up in {@code MineHelperClient}, and both fan out
 * to every registered feature from here - so adding a new feature never
 * requires touching the event wiring again.
 */
public class FeatureManager {
    private final List<Feature> features = new ArrayList<>();

    public void register(Feature feature) {
        features.add(feature);
    }

    public List<Feature> getFeatures() {
        return Collections.unmodifiableList(features);
    }

    public void tickAll(Minecraft client) {
        for (Feature feature : features) {
            feature.onTick(client);
        }
    }

    public void renderAll(LevelRenderContext context) {
        for (Feature feature : features) {
            feature.onRender(context);
        }
    }
}
