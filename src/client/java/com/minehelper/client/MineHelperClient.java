package com.minehelper.client;

import com.minehelper.MineHelper;
import com.minehelper.client.feature.FeatureManager;
import com.minehelper.client.feature.autoclicker.AutoClickerCommand;
import com.minehelper.client.feature.autoclicker.AutoClickerFeature;
import com.minehelper.client.feature.autosprint.AutoSprintCommand;
import com.minehelper.client.feature.autosprint.AutoSprintFeature;
import com.minehelper.client.feature.blockfinder.BlockFinderCommand;
import com.minehelper.client.feature.blockfinder.BlockFinderFeature;
import com.minehelper.client.feature.playeresp.PlayerEspCommand;
import com.minehelper.client.feature.playeresp.PlayerEspFeature;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

public class MineHelperClient implements ClientModInitializer {
    private static FeatureManager featureManager;
    private static BlockFinderFeature blockFinderFeature;
    private static PlayerEspFeature playerEspFeature;
    private static AutoSprintFeature autoSprintFeature;
    private static AutoClickerFeature autoClickerFeature;
    private static KeyMapping blockFinderToggleKey;
    private static LocalizationManager localization;
    private static String modVersion;

    @Override
    public void onInitializeClient() {
        MineHelper.LOGGER.info("Initializing MineHelper Client");

        modVersion = FabricLoader.getInstance()
            .getModContainer(MineHelper.MOD_ID)
            .map(c -> c.getMetadata().getVersion().getFriendlyString())
            .orElse("unknown");

        localization = new LocalizationManager();
        featureManager = new FeatureManager();

        blockFinderFeature = new BlockFinderFeature(localization);
        featureManager.register(blockFinderFeature);

        playerEspFeature = new PlayerEspFeature(localization);
        featureManager.register(playerEspFeature);

        autoSprintFeature = new AutoSprintFeature(localization);
        featureManager.register(autoSprintFeature);

        autoClickerFeature = new AutoClickerFeature(localization);
        featureManager.register(autoClickerFeature);

        ClientCommandRegistrationCallback.EVENT.register(
            new BlockFinderCommand(blockFinderFeature.getScanner(), blockFinderFeature.getConfig(), localization)
        );
        ClientCommandRegistrationCallback.EVENT.register(
            new PlayerEspCommand(playerEspFeature, localization)
        );
        ClientCommandRegistrationCallback.EVENT.register(
            new AutoSprintCommand(autoSprintFeature, localization)
        );
        ClientCommandRegistrationCallback.EVENT.register(
            new AutoClickerCommand(autoClickerFeature, localization)
        );
        ClientCommandRegistrationCallback.EVENT.register(
            new MineHelperCommand(localization, modVersion)
        );

        blockFinderToggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.minehelper.toggle_blockfinder",
            InputConstants.UNKNOWN.getValue(),
            KeyMapping.Category.MISC
        ));

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.player != null) {
                client.player.sendSystemMessage(Component.literal(localization.get("welcome.title", modVersion)));
                client.player.sendSystemMessage(Component.literal(localization.get("welcome.help")));
            }
        });

        LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(featureManager::renderAll);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (blockFinderToggleKey.consumeClick()) {
                blockFinderFeature.toggleWithChatFeedback(client);
            }
            if (client.player != null) {
                featureManager.tickAll(client);
            }
        });

        MineHelper.LOGGER.info("MineHelper Client initialized");
    }

    public static FeatureManager getFeatureManager()       { return featureManager; }
    public static BlockFinderFeature getBlockFinderFeature() { return blockFinderFeature; }
    public static PlayerEspFeature getPlayerEspFeature()   { return playerEspFeature; }
    public static AutoSprintFeature getAutoSprintFeature() { return autoSprintFeature; }
    public static AutoClickerFeature getAutoClickerFeature() { return autoClickerFeature; }
    public static LocalizationManager getLocalization()    { return localization; }
    public static String getModVersion()                   { return modVersion; }
}
