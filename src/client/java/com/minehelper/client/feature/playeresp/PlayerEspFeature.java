package com.minehelper.client.feature.playeresp;

import com.minehelper.client.LocalizationManager;
import com.minehelper.client.feature.Feature;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;

public class PlayerEspFeature implements Feature {
    public static final String ID = "playeresp";

    private final LocalizationManager lang;
    private final PlayerEspConfig config;
    private final PlayerEspRenderer renderer;
    private boolean enabled = false;

    public PlayerEspFeature(LocalizationManager lang) {
        this.lang = lang;
        this.config = PlayerEspConfig.load();
        this.renderer = new PlayerEspRenderer(config);
    }

    @Override public String getId()              { return ID; }
    @Override public boolean isEnabled()         { return enabled; }
    @Override public void setEnabled(boolean v)  { this.enabled = v; }

    @Override
    public void onRender(LevelRenderContext context) {
        if (!enabled) return;
        renderer.render(context.poseStack());
    }

    public void toggleWithChatFeedback(Minecraft client) {
        enabled = !enabled;
        if (client.player == null) return;
        client.player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
            enabled ? lang.get("playeresp.enabled") : lang.get("playeresp.disabled")
        ));
    }

    public PlayerEspConfig getConfig() { return config; }
}
