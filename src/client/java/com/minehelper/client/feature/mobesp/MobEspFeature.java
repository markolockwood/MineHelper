package com.minehelper.client.feature.mobesp;

import com.minehelper.client.LocalizationManager;
import com.minehelper.client.feature.Feature;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;

public class MobEspFeature implements Feature {
    public static final String ID = "mobesp";

    private final LocalizationManager lang;
    private final MobEspConfig config;
    private final MobEspRenderer renderer;
    private boolean enabled = false;

    public MobEspFeature(LocalizationManager lang) {
        this.lang = lang;
        this.config = MobEspConfig.load();
        this.renderer = new MobEspRenderer(config);
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
            enabled ? lang.get("mobesp.enabled") : lang.get("mobesp.disabled")
        ));
    }

    public MobEspConfig getConfig() { return config; }
}
