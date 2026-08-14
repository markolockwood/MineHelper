package com.minehelper.client.feature.keybind;

import com.minehelper.client.gui.KeyBindListScreen;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.*;

public class KeyBindCommand implements ClientCommandRegistrationCallback {
    private final KeyBindFeature feature;

    public KeyBindCommand(KeyBindFeature feature) {
        this.feature = feature;
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(
            literal("mhbind")
                .executes(ctx -> {
                    Minecraft mc = Minecraft.getInstance();
                    mc.execute(() -> mc.setScreen(new KeyBindListScreen(feature)));
                    return 1;
                })
        );
    }
}
