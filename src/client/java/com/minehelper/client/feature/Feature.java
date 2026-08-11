package com.minehelper.client.feature;

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;

/**
 * A self-contained client-side capability (block finder, auto sprint, player
 * ESP, ...). Implementations own their own state/config and decide for
 * themselves whether they're currently active - {@link FeatureManager} calls
 * every registered feature's hooks unconditionally each tick/frame, and each
 * feature is responsible for no-op'ing quickly when disabled.
 */
public interface Feature {
    String getId();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    default void onTick(Minecraft client) {
    }

    default void onRender(LevelRenderContext context) {
    }
}
