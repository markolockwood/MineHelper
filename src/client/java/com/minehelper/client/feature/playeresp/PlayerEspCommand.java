package com.minehelper.client.feature.playeresp;

import com.minehelper.client.LocalizationManager;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.PlayerTeam;

import java.util.List;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class PlayerEspCommand implements ClientCommandRegistrationCallback {
    private final PlayerEspFeature feature;
    private final LocalizationManager lang;

    public PlayerEspCommand(PlayerEspFeature feature, LocalizationManager lang) {
        this.feature = feature;
        this.lang = lang;
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext ctx) {
        dispatcher.register(literal("mhesp")
            .executes(c -> {
                feature.toggleWithChatFeedback(c.getSource().getClient());
                return 1;
            })
            .then(literal("debug").executes(c -> debugColors(c.getSource())))
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
                            feature.getConfig().setRenderMode(PlayerEspConfig.RenderMode.AABB);
                        } else if ("glow".equalsIgnoreCase(m)) {
                            feature.getConfig().setRenderMode(PlayerEspConfig.RenderMode.GLOW);
                        } else {
                            c.getSource().sendError(Component.literal("§cUnknown mode: " + m));
                            return 0;
                        }
                        feature.getConfig().save();
                        c.getSource().sendFeedback(Component.literal(lang.get("playeresp.mode.set", m)));
                        return 1;
                    })))
            .then(literal("colormode")
                .then(argument("colormode", word())
                    .suggests((c, b) -> { b.suggest("solid"); b.suggest("team"); return b.buildFuture(); })
                    .executes(c -> {
                        String m = getString(c, "colormode");
                        if ("solid".equalsIgnoreCase(m)) {
                            feature.getConfig().setColorMode(PlayerEspConfig.ColorMode.SOLID);
                        } else if ("team".equalsIgnoreCase(m)) {
                            feature.getConfig().setColorMode(PlayerEspConfig.ColorMode.TEAM);
                        } else {
                            c.getSource().sendError(Component.literal("§cUnknown color mode: " + m));
                            return 0;
                        }
                        feature.getConfig().save();
                        c.getSource().sendFeedback(Component.literal(lang.get("playeresp.colormode.set", m)));
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
                                c.getSource().sendFeedback(Component.literal(lang.get("playeresp.color.set", r, g, b)));
                                return 1;
                            })))))
            .then(literal("radius")
                .then(argument("radius", integer(16, 256))
                    .executes(c -> {
                        int r = getInteger(c, "radius");
                        feature.getConfig().setScanRadius(r);
                        feature.getConfig().save();
                        c.getSource().sendFeedback(Component.literal(lang.get("playeresp.radius.set", r)));
                        return 1;
                    }))));
    }

    private int debugColors(FabricClientCommandSource src) {
        Minecraft mc = src.getClient();
        if (mc.player == null || mc.getConnection() == null || mc.level == null) {
            src.sendFeedback(Component.literal("§cNot in game"));
            return 0;
        }

        // Debug self
        debugPlayer(src, mc.player, "SELF");

        // Debug up to 2 other players
        List<AbstractClientPlayer> others = mc.level.players().stream()
            .filter(p -> p != mc.player)
            .limit(2)
            .toList();

        if (others.isEmpty()) {
            src.sendFeedback(Component.literal("§7No other players nearby"));
        }
        for (AbstractClientPlayer p : others) {
            debugPlayer(src, p, "OTHER");
        }

        return 1;
    }

    private void debugPlayer(FabricClientCommandSource src, AbstractClientPlayer player, String label) {
        Minecraft mc = src.getClient();
        src.sendFeedback(Component.literal("§e--- " + label + ": §b" + player.getScoreboardName() + " ---"));

        // Source 1: Entity.getTeamColor()
        int teamColor = player.getTeamColor();
        src.sendFeedback(Component.literal("§7Entity.getTeamColor(): §f#" + String.format("%06X", teamColor & 0xFFFFFF)));

        // Source 2: Entity.getTeam()
        PlayerTeam entityTeam = player.getTeam();
        if (entityTeam != null) {
            src.sendFeedback(Component.literal("§7Entity.getTeam(): §f" + entityTeam.getName()
                + " color=" + entityTeam.getColor().name()
                + " colorInt=" + entityTeam.getColor().getColor()));
        } else {
            src.sendFeedback(Component.literal("§7Entity.getTeam(): §cnull"));
        }

        // Source 3: PlayerInfo from connection
        var connection = mc.getConnection();
        if (connection != null) {
            PlayerInfo info = connection.getPlayerInfo(player.getUUID());
            if (info != null) {
                // Source 3a: PlayerInfo.getTeam()
                PlayerTeam infoTeam = info.getTeam();
                if (infoTeam != null) {
                    src.sendFeedback(Component.literal("§7PlayerInfo.getTeam(): §f" + infoTeam.getName()
                        + " color=" + infoTeam.getColor().name()
                        + " colorInt=" + infoTeam.getColor().getColor()));
                } else {
                    src.sendFeedback(Component.literal("§7PlayerInfo.getTeam(): §cnull"));
                }

                // Source 3b: TabListDisplayName style
                var displayName = info.getTabListDisplayName();
                if (displayName != null) {
                    var style = displayName.getStyle();
                    var textColor = style.getColor();
                    src.sendFeedback(Component.literal("§7TabName: §f\"" + displayName.getString() + "\""));
                    src.sendFeedback(Component.literal("§7TabName.style.color: §f"
                        + (textColor != null ? "#" + String.format("%06X", textColor.getValue()) : "null")));

                    // Source 3c: siblings color
                    if (!displayName.getSiblings().isEmpty()) {
                        var sibling = displayName.getSiblings().get(0);
                        var siblingColor = sibling.getStyle().getColor();
                        src.sendFeedback(Component.literal("§7TabName.sibling[0].color: §f"
                            + (siblingColor != null ? "#" + String.format("%06X", siblingColor.getValue()) : "null")));
                    }
                } else {
                    src.sendFeedback(Component.literal("§7TabListDisplayName: §cnull"));
                }
            } else {
                src.sendFeedback(Component.literal("§7PlayerInfo: §cnull"));
            }
        }
    }
}
