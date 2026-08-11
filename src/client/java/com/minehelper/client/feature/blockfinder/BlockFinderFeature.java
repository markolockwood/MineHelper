package com.minehelper.client.feature.blockfinder;

import com.minehelper.client.LocalizationManager;
import com.minehelper.client.feature.Feature;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class BlockFinderFeature implements Feature {
    public static final String ID = "blockfinder";

    private final LocalizationManager lang;
    private final BlockFinderConfig config;
    private final BlockScanner scanner;
    private final BlockHighlightRenderer renderer;

    public BlockFinderFeature(LocalizationManager lang) {
        this.lang = lang;
        this.config = BlockFinderConfig.load();
        this.scanner = new BlockScanner(config);
        this.renderer = new BlockHighlightRenderer(scanner, config);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean isEnabled() {
        return scanner.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        scanner.setEnabled(enabled);
    }

    @Override
    public void onTick(Minecraft client) {
        scanner.tick(client);
    }

    @Override
    public void onRender(LevelRenderContext context) {
        renderer.render(context);
    }

    public BlockScanner getScanner() {
        return scanner;
    }

    public BlockFinderConfig getConfig() {
        return config;
    }

    /**
     * Toggles scanning the same way {@code /blockfinder toggle} does, and
     * reports the new state to chat. Used by the toggle keybind, which has
     * no command source to report feedback through.
     */
    public void toggleWithChatFeedback(Minecraft client) {
        scanner.toggle();
        if (client.player == null) {
            return;
        }

        if (scanner.isEnabled()) {
            if (scanner.getTargetBlock() != null) {
                Identifier blockId = BuiltInRegistries.BLOCK.getKey(scanner.getTargetBlock());
                client.player.sendSystemMessage(Component.literal(
                    lang.get("blockfinder.enabled") + "\n" + lang.get("blockfinder.target.set", blockId.getPath())
                ));
            } else {
                client.player.sendSystemMessage(Component.literal(
                    lang.get("blockfinder.enabled") + "\n" + lang.get("blockfinder.target.none")
                ));
            }
        } else {
            client.player.sendSystemMessage(Component.literal(lang.get("blockfinder.disabled")));
        }
    }
}
