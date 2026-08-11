package com.minehelper.client.gui;

import com.minehelper.client.LocalizationManager;
import com.minehelper.client.feature.playeresp.PlayerEspConfig;
import com.minehelper.client.feature.playeresp.PlayerEspFeature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

public class PlayerEspTab extends MineHelperTab {
    private final PlayerEspFeature feature;
    private StringWidget statusLabel;
    private StringWidget modeLabel;
    private StringWidget colorModeLabel;
    private StringWidget radiusLabel;
    private StringWidget colorLabel;

    public PlayerEspTab(LocalizationManager lang, PlayerEspFeature feature) {
        super(Component.literal("PlayerESP"), lang);
        this.feature = feature;
    }

    @Override
    protected void buildWidgets(ScreenRectangle tabArea) {
        int cx = tabArea.left() + tabArea.width() / 2;
        int y  = tabArea.top() + 20;
        Minecraft mc = Minecraft.getInstance();

        // Status
        statusLabel = new StringWidget(cx - 150, y, 300, 10,
            getStatusText(), mc.font);
        widgets.add(statusLabel);

        // Toggle
        widgets.add(Button.builder(Component.literal(lang.get("playeresp.toggle")), b -> {
            feature.setEnabled(!feature.isEnabled());
            statusLabel.setMessage(getStatusText());
        }).bounds(cx - 150, y + 15, 100, 20).build());

        // Render mode
        modeLabel = new StringWidget(cx - 150, y + 50, 300, 10,
            getModeText(), mc.font);
        widgets.add(modeLabel);

        widgets.add(Button.builder(Component.literal("AABB"), b -> {
            feature.getConfig().setRenderMode(PlayerEspConfig.RenderMode.AABB);
            feature.getConfig().save();
            modeLabel.setMessage(getModeText());
        }).bounds(cx - 150, y + 65, 80, 20).build());

        widgets.add(Button.builder(Component.literal("Glow"), b -> {
            feature.getConfig().setRenderMode(PlayerEspConfig.RenderMode.GLOW);
            feature.getConfig().save();
            modeLabel.setMessage(getModeText());
        }).bounds(cx - 65, y + 65, 95, 20).build());

        // Color mode
        colorModeLabel = new StringWidget(cx - 150, y + 100, 300, 10,
            getColorModeText(), mc.font);
        widgets.add(colorModeLabel);

        widgets.add(Button.builder(Component.literal("Solid"), b -> {
            feature.getConfig().setColorMode(PlayerEspConfig.ColorMode.SOLID);
            feature.getConfig().save();
            colorModeLabel.setMessage(getColorModeText());
        }).bounds(cx - 150, y + 115, 80, 20).build());

        widgets.add(Button.builder(Component.literal("Team Color"), b -> {
            feature.getConfig().setColorMode(PlayerEspConfig.ColorMode.TEAM);
            feature.getConfig().save();
            colorModeLabel.setMessage(getColorModeText());
        }).bounds(cx - 65, y + 115, 100, 20).build());

        // Solid color R/G/B
        colorLabel = new StringWidget(cx - 150, y + 150, 300, 10,
            getColorText(), mc.font);
        widgets.add(colorLabel);

        // R
        widgets.add(Button.builder(Component.literal("R-"), b -> adjustColor(0, -10)).bounds(cx - 150, y + 165, 30, 20).build());
        widgets.add(Button.builder(Component.literal("R+"), b -> adjustColor(0, +10)).bounds(cx - 115, y + 165, 30, 20).build());
        // G
        widgets.add(Button.builder(Component.literal("G-"), b -> adjustColor(1, -10)).bounds(cx - 75,  y + 165, 30, 20).build());
        widgets.add(Button.builder(Component.literal("G+"), b -> adjustColor(1, +10)).bounds(cx - 40,  y + 165, 30, 20).build());
        // B
        widgets.add(Button.builder(Component.literal("B-"), b -> adjustColor(2, -10)).bounds(cx,       y + 165, 30, 20).build());
        widgets.add(Button.builder(Component.literal("B+"), b -> adjustColor(2, +10)).bounds(cx + 35,  y + 165, 30, 20).build());

        // Radius
        radiusLabel = new StringWidget(cx - 60, y + 200, 120, 20, getRadiusText(), mc.font);
        widgets.add(radiusLabel);
        widgets.add(Button.builder(Component.literal("-"), b -> adjustRadius(-16))
            .bounds(cx - 150, y + 200, 30, 20).build());
        widgets.add(Button.builder(Component.literal("+"), b -> adjustRadius(16))
            .bounds(cx + 70,  y + 200, 30, 20).build());
    }

    private void adjustColor(int channel, int delta) {
        float[] c = feature.getConfig().getSolidColor().clone();
        c[channel] = Math.max(0f, Math.min(1f, c[channel] + delta / 255f));
        feature.getConfig().setSolidColor(c[0], c[1], c[2], 1f);
        feature.getConfig().save();
        colorLabel.setMessage(getColorText());
    }

    private void adjustRadius(int delta) {
        feature.getConfig().setScanRadius(feature.getConfig().getScanRadius() + delta);
        feature.getConfig().save();
        radiusLabel.setMessage(getRadiusText());
    }

    private Component getStatusText() {
        return Component.literal(lang.get("playeresp.status",
            feature.isEnabled() ? lang.get("gui.yes") : lang.get("gui.no")));
    }

    private Component getModeText() {
        return Component.literal(lang.get("playeresp.mode",
            feature.getConfig().getRenderMode().name()));
    }

    private Component getColorModeText() {
        return Component.literal(lang.get("playeresp.colormode",
            feature.getConfig().getColorMode().name()));
    }

    private Component getColorText() {
        float[] c = feature.getConfig().getSolidColor();
        int r = (int)(c[0] * 255), g = (int)(c[1] * 255), b = (int)(c[2] * 255);
        return Component.literal(lang.get("playeresp.color", r, g, b));
    }

    private Component getRadiusText() {
        return Component.literal(lang.get("playeresp.radius", feature.getConfig().getScanRadius()));
    }
}
