package com.minehelper.client.feature.autosprint;

import com.minehelper.client.LocalizationManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.*;

public class AutoSprintCommand implements ClientCommandRegistrationCallback {
    private final AutoSprintFeature feature;
    private final LocalizationManager localization;

    public AutoSprintCommand(AutoSprintFeature feature, LocalizationManager localization) {
        this.feature = feature;
        this.localization = localization;
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(
            literal("mhsprint")
                .executes(this::toggle)
        );
    }

    private int toggle(CommandContext<FabricClientCommandSource> ctx) {
        feature.setEnabled(!feature.isEnabled());
        String status = feature.isEnabled()
            ? localization.get("autosprint.enabled")
            : localization.get("autosprint.disabled");
        ctx.getSource().sendFeedback(Component.literal(status));
        return 1;
    }
}
