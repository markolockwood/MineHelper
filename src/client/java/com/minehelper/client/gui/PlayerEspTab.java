package com.minehelper.client.gui;

import com.minehelper.client.LocalizationManager;
import com.minehelper.client.feature.playeresp.PlayerEspConfig;
import com.minehelper.client.feature.playeresp.PlayerEspFeature;
import com.minehelper.client.feature.mobesp.MobEspConfig;
import com.minehelper.client.feature.mobesp.MobEspFeature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

public class PlayerEspTab extends MineHelperTab {
    private final PlayerEspFeature playerFeature;
    private final MobEspFeature mobFeature;

    // Player ESP widgets
    private StringWidget playerStatusLabel;
    private StringWidget playerModeLabel;
    private StringWidget playerColorModeLabel;
    private StringWidget playerRadiusLabel;
    private StringWidget playerColorLabel;

    // Mob ESP widgets
    private StringWidget mobStatusLabel;
    private StringWidget mobModeLabel;
    private StringWidget mobColorModeLabel;
    private StringWidget mobFilterLabel;
    private StringWidget mobRadiusLabel;
    private StringWidget mobColorLabel;

    public PlayerEspTab(LocalizationManager lang, PlayerEspFeature playerFeature, MobEspFeature mobFeature) {
        super(Component.literal("Player/Mob ESP"), lang);
        this.playerFeature = playerFeature;
        this.mobFeature = mobFeature;
    }

    @Override
    protected void buildWidgets(ScreenRectangle tabArea) {
        int cx = tabArea.left() + tabArea.width() / 2;
        int y  = tabArea.top() + 10;
        Minecraft mc = Minecraft.getInstance();

        // === PLAYER ESP SECTION ===
        widgets.add(new StringWidget(cx - 150, y, 300, 10, Component.literal("§l--- Player ESP ---"), mc.font));
        y += 15;

        playerStatusLabel = new StringWidget(cx - 150, y, 300, 10, getPlayerStatusText(), mc.font);
        widgets.add(playerStatusLabel);
        widgets.add(Button.builder(Component.literal(lang.get("playeresp.toggle")), b -> {
            playerFeature.setEnabled(!playerFeature.isEnabled());
            playerStatusLabel.setMessage(getPlayerStatusText());
        }).bounds(cx - 150, y + 15, 100, 20).build());

        y += 40;
        playerModeLabel = new StringWidget(cx - 150, y, 300, 10, getPlayerModeText(), mc.font);
        widgets.add(playerModeLabel);
        widgets.add(Button.builder(Component.literal("AABB"), b -> {
            playerFeature.getConfig().setRenderMode(PlayerEspConfig.RenderMode.AABB);
            playerFeature.getConfig().save();
            playerModeLabel.setMessage(getPlayerModeText());
        }).bounds(cx - 150, y + 15, 80, 20).build());
        widgets.add(Button.builder(Component.literal("Glow"), b -> {
            playerFeature.getConfig().setRenderMode(PlayerEspConfig.RenderMode.GLOW);
            playerFeature.getConfig().save();
            playerModeLabel.setMessage(getPlayerModeText());
        }).bounds(cx - 65, y + 15, 80, 20).build());

        y += 40;
        playerColorModeLabel = new StringWidget(cx - 150, y, 300, 10, getPlayerColorModeText(), mc.font);
        widgets.add(playerColorModeLabel);
        widgets.add(Button.builder(Component.literal("Solid"), b -> {
            playerFeature.getConfig().setColorMode(PlayerEspConfig.ColorMode.SOLID);
            playerFeature.getConfig().save();
            playerColorModeLabel.setMessage(getPlayerColorModeText());
        }).bounds(cx - 150, y + 15, 80, 20).build());
        widgets.add(Button.builder(Component.literal("Team"), b -> {
            playerFeature.getConfig().setColorMode(PlayerEspConfig.ColorMode.TEAM);
            playerFeature.getConfig().save();
            playerColorModeLabel.setMessage(getPlayerColorModeText());
        }).bounds(cx - 65, y + 15, 80, 20).build());

        y += 40;
        playerColorLabel = new StringWidget(cx - 150, y, 300, 10, getPlayerColorText(), mc.font);
        widgets.add(playerColorLabel);
        widgets.add(Button.builder(Component.literal("R-"), b -> adjustPlayerColor(0, -10)).bounds(cx - 150, y + 15, 30, 20).build());
        widgets.add(Button.builder(Component.literal("R+"), b -> adjustPlayerColor(0, +10)).bounds(cx - 115, y + 15, 30, 20).build());
        widgets.add(Button.builder(Component.literal("G-"), b -> adjustPlayerColor(1, -10)).bounds(cx - 75, y + 15, 30, 20).build());
        widgets.add(Button.builder(Component.literal("G+"), b -> adjustPlayerColor(1, +10)).bounds(cx - 40, y + 15, 30, 20).build());
        widgets.add(Button.builder(Component.literal("B-"), b -> adjustPlayerColor(2, -10)).bounds(cx, y + 15, 30, 20).build());
        widgets.add(Button.builder(Component.literal("B+"), b -> adjustPlayerColor(2, +10)).bounds(cx + 35, y + 15, 30, 20).build());

        y += 40;
        playerRadiusLabel = new StringWidget(cx - 60, y, 120, 20, getPlayerRadiusText(), mc.font);
        widgets.add(playerRadiusLabel);
        widgets.add(Button.builder(Component.literal("-"), b -> adjustPlayerRadius(-16)).bounds(cx - 150, y, 30, 20).build());
        widgets.add(Button.builder(Component.literal("+"), b -> adjustPlayerRadius(16)).bounds(cx + 70, y, 30, 20).build());

        // === MOB ESP SECTION ===
        y += 35;
        widgets.add(new StringWidget(cx - 150, y, 300, 10, Component.literal("§l--- Mob ESP ---"), mc.font));
        y += 15;

        mobStatusLabel = new StringWidget(cx - 150, y, 300, 10, getMobStatusText(), mc.font);
        widgets.add(mobStatusLabel);
        widgets.add(Button.builder(Component.literal(lang.get("mobesp.toggle")), b -> {
            mobFeature.setEnabled(!mobFeature.isEnabled());
            mobStatusLabel.setMessage(getMobStatusText());
        }).bounds(cx - 150, y + 15, 100, 20).build());

        y += 40;
        mobModeLabel = new StringWidget(cx - 150, y, 300, 10, getMobModeText(), mc.font);
        widgets.add(mobModeLabel);
        widgets.add(Button.builder(Component.literal("AABB"), b -> {
            mobFeature.getConfig().setRenderMode(MobEspConfig.RenderMode.AABB);
            mobFeature.getConfig().save();
            mobModeLabel.setMessage(getMobModeText());
        }).bounds(cx - 150, y + 15, 80, 20).build());
        widgets.add(Button.builder(Component.literal("Glow"), b -> {
            mobFeature.getConfig().setRenderMode(MobEspConfig.RenderMode.GLOW);
            mobFeature.getConfig().save();
            mobModeLabel.setMessage(getMobModeText());
        }).bounds(cx - 65, y + 15, 80, 20).build());

        y += 40;
        mobColorModeLabel = new StringWidget(cx - 150, y, 300, 10, getMobColorModeText(), mc.font);
        widgets.add(mobColorModeLabel);
        widgets.add(Button.builder(Component.literal("Solid"), b -> {
            mobFeature.getConfig().setColorMode(MobEspConfig.ColorMode.SOLID);
            mobFeature.getConfig().save();
            mobColorModeLabel.setMessage(getMobColorModeText());
        }).bounds(cx - 150, y + 15, 80, 20).build());
        widgets.add(Button.builder(Component.literal("ByType"), b -> {
            mobFeature.getConfig().setColorMode(MobEspConfig.ColorMode.BY_TYPE);
            mobFeature.getConfig().save();
            mobColorModeLabel.setMessage(getMobColorModeText());
        }).bounds(cx - 65, y + 15, 80, 20).build());

        y += 40;
        mobFilterLabel = new StringWidget(cx - 150, y, 300, 10, getMobFilterText(), mc.font);
        widgets.add(mobFilterLabel);
        widgets.add(Button.builder(Component.literal("All"), b -> {
            mobFeature.getConfig().setMobFilter(MobEspConfig.MobFilter.ALL);
            mobFeature.getConfig().save();
            mobFilterLabel.setMessage(getMobFilterText());
        }).bounds(cx - 150, y + 15, 50, 20).build());
        widgets.add(Button.builder(Component.literal("Hostile"), b -> {
            mobFeature.getConfig().setMobFilter(MobEspConfig.MobFilter.HOSTILE);
            mobFeature.getConfig().save();
            mobFilterLabel.setMessage(getMobFilterText());
        }).bounds(cx - 95, y + 15, 55, 20).build());
        widgets.add(Button.builder(Component.literal("Passive"), b -> {
            mobFeature.getConfig().setMobFilter(MobEspConfig.MobFilter.PASSIVE);
            mobFeature.getConfig().save();
            mobFilterLabel.setMessage(getMobFilterText());
        }).bounds(cx - 35, y + 15, 60, 20).build());

        y += 40;
        mobColorLabel = new StringWidget(cx - 150, y, 300, 10, getMobColorText(), mc.font);
        widgets.add(mobColorLabel);
        widgets.add(Button.builder(Component.literal("R-"), b -> adjustMobColor(0, -10)).bounds(cx - 150, y + 15, 30, 20).build());
        widgets.add(Button.builder(Component.literal("R+"), b -> adjustMobColor(0, +10)).bounds(cx - 115, y + 15, 30, 20).build());
        widgets.add(Button.builder(Component.literal("G-"), b -> adjustMobColor(1, -10)).bounds(cx - 75, y + 15, 30, 20).build());
        widgets.add(Button.builder(Component.literal("G+"), b -> adjustMobColor(1, +10)).bounds(cx - 40, y + 15, 30, 20).build());
        widgets.add(Button.builder(Component.literal("B-"), b -> adjustMobColor(2, -10)).bounds(cx, y + 15, 30, 20).build());
        widgets.add(Button.builder(Component.literal("B+"), b -> adjustMobColor(2, +10)).bounds(cx + 35, y + 15, 30, 20).build());

        y += 40;
        mobRadiusLabel = new StringWidget(cx - 60, y, 120, 20, getMobRadiusText(), mc.font);
        widgets.add(mobRadiusLabel);
        widgets.add(Button.builder(Component.literal("-"), b -> adjustMobRadius(-16)).bounds(cx - 150, y, 30, 20).build());
        widgets.add(Button.builder(Component.literal("+"), b -> adjustMobRadius(16)).bounds(cx + 70, y, 30, 20).build());
    }

    // Player ESP methods
    private void adjustPlayerColor(int channel, int delta) {
        float[] c = playerFeature.getConfig().getSolidColor().clone();
        c[channel] = Math.max(0f, Math.min(1f, c[channel] + delta / 255f));
        playerFeature.getConfig().setSolidColor(c[0], c[1], c[2], 1f);
        playerFeature.getConfig().save();
        playerColorLabel.setMessage(getPlayerColorText());
    }

    private void adjustPlayerRadius(int delta) {
        playerFeature.getConfig().setScanRadius(playerFeature.getConfig().getScanRadius() + delta);
        playerFeature.getConfig().save();
        playerRadiusLabel.setMessage(getPlayerRadiusText());
    }

    private Component getPlayerStatusText() {
        return Component.literal(lang.get("playeresp.status",
            playerFeature.isEnabled() ? lang.get("gui.yes") : lang.get("gui.no")));
    }

    private Component getPlayerModeText() {
        return Component.literal(lang.get("playeresp.mode",
            playerFeature.getConfig().getRenderMode().name()));
    }

    private Component getPlayerColorModeText() {
        return Component.literal(lang.get("playeresp.colormode",
            playerFeature.getConfig().getColorMode().name()));
    }

    private Component getPlayerColorText() {
        float[] c = playerFeature.getConfig().getSolidColor();
        int r = (int)(c[0] * 255), g = (int)(c[1] * 255), b = (int)(c[2] * 255);
        return Component.literal(lang.get("playeresp.color", r, g, b));
    }

    private Component getPlayerRadiusText() {
        return Component.literal(lang.get("playeresp.radius", playerFeature.getConfig().getScanRadius()));
    }

    // Mob ESP methods
    private void adjustMobColor(int channel, int delta) {
        float[] c = mobFeature.getConfig().getSolidColor().clone();
        c[channel] = Math.max(0f, Math.min(1f, c[channel] + delta / 255f));
        mobFeature.getConfig().setSolidColor(c[0], c[1], c[2], 1f);
        mobFeature.getConfig().save();
        mobColorLabel.setMessage(getMobColorText());
    }

    private void adjustMobRadius(int delta) {
        mobFeature.getConfig().setScanRadius(mobFeature.getConfig().getScanRadius() + delta);
        mobFeature.getConfig().save();
        mobRadiusLabel.setMessage(getMobRadiusText());
    }

    private Component getMobStatusText() {
        return Component.literal(lang.get("mobesp.status",
            mobFeature.isEnabled() ? lang.get("gui.yes") : lang.get("gui.no")));
    }

    private Component getMobModeText() {
        return Component.literal(lang.get("mobesp.mode",
            mobFeature.getConfig().getRenderMode().name()));
    }

    private Component getMobColorModeText() {
        return Component.literal(lang.get("mobesp.colormode",
            mobFeature.getConfig().getColorMode().name()));
    }

    private Component getMobFilterText() {
        return Component.literal(lang.get("mobesp.filter",
            mobFeature.getConfig().getMobFilter().name()));
    }

    private Component getMobColorText() {
        float[] c = mobFeature.getConfig().getSolidColor();
        int r = (int)(c[0] * 255), g = (int)(c[1] * 255), b = (int)(c[2] * 255);
        return Component.literal(lang.get("mobesp.color", r, g, b));
    }

    private Component getMobRadiusText() {
        return Component.literal(lang.get("mobesp.radius", mobFeature.getConfig().getScanRadius()));
    }
}
