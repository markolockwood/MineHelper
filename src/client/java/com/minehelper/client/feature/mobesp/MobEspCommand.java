package com.minehelper.client.feature.mobesp;

import com.minehelper.client.LocalizationManager;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class MobEspCommand implements ClientCommandRegistrationCallback {
    private final MobEspFeature feature;
    private final LocalizationManager lang;

    public MobEspCommand(MobEspFeature feature, LocalizationManager lang) {
        this.feature = feature;
        this.lang = lang;
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext ctx) {
        dispatcher.register(literal("mhmobesp")
            .executes(c -> {
                feature.toggleWithChatFeedback(c.getSource().getClient());
                return 1;
            })
            .then(literal("toggle").executes(c -> {
                feature.toggleWithChatFeedback(c.getSource().getClient());
                return 1;
            }))
            .then(literal("mode")
                .then(argument("mode", word())
                    .suggests((c, b) -> { b.suggest("aabb"); b.suggest("glow"); return b.buildFuture(); })
                    .executes(c -> {
                        String m = getString(c, "mode");
                        if ("aabb".equalsIgnoreCase(m)) {
                            feature.getConfig().setRenderMode(MobEspConfig.RenderMode.AABB);
                        } else if ("glow".equalsIgnoreCase(m)) {
                            feature.getConfig().setRenderMode(MobEspConfig.RenderMode.GLOW);
                        } else {
                            c.getSource().sendError(Component.literal("§cUnknown mode: " + m));
                            return 0;
                        }
                        feature.getConfig().save();
                        c.getSource().sendFeedback(Component.literal(lang.get("mobesp.mode.set", m)));
                        return 1;
                    })))
            .then(literal("colormode")
                .then(argument("colormode", word())
                    .suggests((c, b) -> { b.suggest("solid"); b.suggest("bytype"); return b.buildFuture(); })
                    .executes(c -> {
                        String m = getString(c, "colormode");
                        if ("solid".equalsIgnoreCase(m)) {
                            feature.getConfig().setColorMode(MobEspConfig.ColorMode.SOLID);
                        } else if ("bytype".equalsIgnoreCase(m)) {
                            feature.getConfig().setColorMode(MobEspConfig.ColorMode.BY_TYPE);
                        } else {
                            c.getSource().sendError(Component.literal("§cUnknown color mode: " + m));
                            return 0;
                        }
                        feature.getConfig().save();
                        c.getSource().sendFeedback(Component.literal(lang.get("mobesp.colormode.set", m)));
                        return 1;
                    })))
            .then(literal("filter")
                .then(argument("filter", word())
                    .suggests((c, b) -> { b.suggest("all"); b.suggest("hostile"); b.suggest("passive"); return b.buildFuture(); })
                    .executes(c -> {
                        String f = getString(c, "filter");
                        if ("all".equalsIgnoreCase(f)) {
                            feature.getConfig().setMobFilter(MobEspConfig.MobFilter.ALL);
                        } else if ("hostile".equalsIgnoreCase(f)) {
                            feature.getConfig().setMobFilter(MobEspConfig.MobFilter.HOSTILE);
                        } else if ("passive".equalsIgnoreCase(f)) {
                            feature.getConfig().setMobFilter(MobEspConfig.MobFilter.PASSIVE);
                        } else {
                            c.getSource().sendError(Component.literal("§cUnknown filter: " + f));
                            return 0;
                        }
                        feature.getConfig().save();
                        c.getSource().sendFeedback(Component.literal(lang.get("mobesp.filter.set", f)));
                        return 1;
                    })))
            .then(literal("color")
                .then(argument("r", integer(0, 255))
                    .then(argument("g", integer(0, 255))
                        .then(argument("b", integer(0, 255))
                            .executes(c -> {
                                int r = getInteger(c, "r"), g = getInteger(c, "g"), b = getInteger(c, "b");
                                feature.getConfig().setSolidColor(r / 255f, g / 255f, b / 255f, 1f);
                                feature.getConfig().save();
                                c.getSource().sendFeedback(Component.literal(lang.get("mobesp.color.set", r, g, b)));
                                return 1;
                            })))))
            .then(literal("radius")
                .then(argument("radius", integer(16, 256))
                    .executes(c -> {
                        int r = getInteger(c, "radius");
                        feature.getConfig().setScanRadius(r);
                        feature.getConfig().save();
                        c.getSource().sendFeedback(Component.literal(lang.get("mobesp.radius.set", r)));
                        return 1;
                    }))));
    }
}
