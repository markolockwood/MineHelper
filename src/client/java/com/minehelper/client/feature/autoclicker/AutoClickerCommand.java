package com.minehelper.client.feature.autoclicker;

import com.minehelper.client.LocalizationManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.*;

public class AutoClickerCommand implements ClientCommandRegistrationCallback {
    private final AutoClickerFeature feature;
    private final LocalizationManager localization;

    public AutoClickerCommand(AutoClickerFeature feature, LocalizationManager localization) {
        this.feature = feature;
        this.localization = localization;
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(
            literal("mhclick")
                .executes(this::toggle)
        );
    }

    private int toggle(CommandContext<FabricClientCommandSource> ctx) {
        feature.setEnabled(!feature.isEnabled());
        String status = feature.isEnabled()
            ? localization.get("autoclicker.enabled")
            : localization.get("autoclicker.disabled");
        ctx.getSource().sendFeedback(Component.literal(status));
        return 1;
    }
}
