package com.minehelper.client;

import com.minehelper.client.gui.MineHelperScreen;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class MineHelperCommand implements ClientCommandRegistrationCallback {
    private final LocalizationManager lang;
    private final String version;

    public MineHelperCommand(LocalizationManager lang, String version) {
        this.lang = lang;
        this.version = version;
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(literal("minehelper")
            .executes(ctx -> {
                // Open GUI
                ctx.getSource().getClient().execute(() -> {
                    ctx.getSource().getClient().setScreen(
                        new MineHelperScreen(lang, version,
                            MineHelperClient.getBlockFinderFeature(),
                            MineHelperClient.getPlayerEspFeature(),
                            MineHelperClient.getMobEspFeature(),
                            MineHelperClient.getAutoSprintFeature(),
                            MineHelperClient.getAutoClickerFeature())
                    );
                });
                return 1;
            })
            .then(literal("lang")
                .then(argument("language", StringArgumentType.word())
                    .suggests((ctx, builder) -> {
                        builder.suggest("en");
                        builder.suggest("ru");
                        return builder.buildFuture();
                    })
                    .executes(ctx -> {
                        String language = getString(ctx, "language");
                        if (lang.setLang(language)) {
                            ctx.getSource().sendFeedback(Component.literal(lang.get("lang.changed", language)));
                        } else {
                            ctx.getSource().sendError(Component.literal(lang.get("lang.unknown", language)));
                        }
                        return 1;
                    }))));
    }
}
