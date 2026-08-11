package com.minehelper.client.feature.blockfinder;

import com.minehelper.client.LocalizationManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.*;

public class BlockFinderCommand implements ClientCommandRegistrationCallback {
    private final BlockScanner scanner;
    private final BlockFinderConfig config;
    private final LocalizationManager lang;

    private static final SuggestionProvider<FabricClientCommandSource> BLOCK_SUGGESTIONS = (context, builder) -> {
        return SharedSuggestionProvider.suggestResource(
            BuiltInRegistries.BLOCK.keySet(),
            builder
        );
    };

    public BlockFinderCommand(BlockScanner scanner, BlockFinderConfig config, LocalizationManager lang) {
        this.scanner = scanner;
        this.config = config;
        this.lang = lang;
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(literal("blockfinder")
            .then(literal("set")
                .then(argument("block", StringArgumentType.greedyString())
                    .suggests(BLOCK_SUGGESTIONS)
                    .executes(this::setBlock)))
            .then(literal("check")
                .executes(this::checkBlock))
            .then(literal("toggle")
                .executes(this::toggle))
            .then(literal("clear")
                .executes(this::clear))
            .then(literal("radius")
                .then(argument("radius", integer(16, 128))
                    .executes(this::setRadius)))
            .then(literal("color")
                .then(argument("r", integer(0, 255))
                    .then(argument("g", integer(0, 255))
                        .then(argument("b", integer(0, 255))
                            .executes(this::setColor)))))
            .then(literal("status")
                .executes(this::status)));
    }

    private int setBlock(CommandContext<FabricClientCommandSource> context) {
        try {
            String blockIdStr = getString(context, "block");

            // Parse identifier - handle both "stone" and "minecraft:stone" formats
            Identifier blockId;
            try {
                if (blockIdStr.contains(":")) {
                    blockId = Identifier.parse(blockIdStr);
                } else {
                    // If no namespace, default to minecraft
                    blockId = Identifier.fromNamespaceAndPath("minecraft", blockIdStr);
                }
            } catch (Exception e) {
                context.getSource().sendError(Component.literal("§cInvalid block identifier format: " + blockIdStr));
                return 0;
            }

            var blockRef = BuiltInRegistries.BLOCK.get(blockId);
            if (blockRef.isEmpty()) {
                context.getSource().sendError(Component.literal("§cUnknown block: " + blockIdStr));
                return 0;
            }

            Block block = blockRef.get().value();
            scanner.setTargetBlock(block);
            context.getSource().sendFeedback(Component.literal(lang.get("blockfinder.target.set", blockId.getPath())));
            return Command.SINGLE_SUCCESS;
        } catch (Exception e) {
            context.getSource().sendError(Component.literal("§cError: " + e.getMessage()));
            return 0;
        }
    }

    private int checkBlock(CommandContext<FabricClientCommandSource> context) {
        var client = context.getSource().getClient();
        var hitResult = client.hitResult;

        if (hitResult instanceof net.minecraft.world.phys.BlockHitResult blockHit) {
            var level = client.level;
            if (level != null) {
                var blockPos = blockHit.getBlockPos();
                var blockState = level.getBlockState(blockPos);
                var block = blockState.getBlock();
                Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);

                context.getSource().sendFeedback(Component.literal(
                    lang.get("blockfinder.check.result", blockId.getPath())
                ));
                return Command.SINGLE_SUCCESS;
            }
        }

        context.getSource().sendFeedback(Component.literal(lang.get("blockfinder.check.none")));
        return Command.SINGLE_SUCCESS;
    }

    private int toggle(CommandContext<FabricClientCommandSource> context) {
        scanner.toggle();
        boolean enabled = scanner.isEnabled();

        if (enabled) {
            Block target = scanner.getTargetBlock();
            if (target != null) {
                Identifier blockId = BuiltInRegistries.BLOCK.getKey(target);
                context.getSource().sendFeedback(Component.literal(
                    lang.get("blockfinder.enabled") + "\n" + lang.get("blockfinder.target.set", blockId.getPath())
                ));
            } else {
                context.getSource().sendFeedback(Component.literal(
                    lang.get("blockfinder.enabled") + "\n" + lang.get("blockfinder.target.none")
                ));
            }
        } else {
            context.getSource().sendFeedback(Component.literal(lang.get("blockfinder.disabled")));
        }

        return Command.SINGLE_SUCCESS;
    }

    private int clear(CommandContext<FabricClientCommandSource> context) {
        scanner.clear();
        context.getSource().sendFeedback(Component.literal(lang.get("blockfinder.cleared")));
        return Command.SINGLE_SUCCESS;
    }

    private int setRadius(CommandContext<FabricClientCommandSource> context) {
        int radius = getInteger(context, "radius");
        config.setScanRadius(radius);
        config.save();
        scanner.clear();
        context.getSource().sendFeedback(Component.literal(lang.get("blockfinder.radius.set", radius)));
        return Command.SINGLE_SUCCESS;
    }

    private int setColor(CommandContext<FabricClientCommandSource> context) {
        int r = getInteger(context, "r");
        int g = getInteger(context, "g");
        int b = getInteger(context, "b");

        config.setHighlightColor(r / 255.0f, g / 255.0f, b / 255.0f, 1.0f);
        config.save();
        context.getSource().sendFeedback(Component.literal(lang.get("blockfinder.color.set", r, g, b)));
        return Command.SINGLE_SUCCESS;
    }

    private int status(CommandContext<FabricClientCommandSource> context) {
        boolean enabled = scanner.isEnabled();
        Block target = scanner.getTargetBlock();
        int foundCount = scanner.getFoundBlocks().size();

        context.getSource().sendFeedback(Component.literal(lang.get("blockfinder.status.header")));
        context.getSource().sendFeedback(Component.literal(lang.get("blockfinder.status.enabled",
            enabled ? lang.get("gui.yes") : lang.get("gui.no"))));

        if (target != null) {
            Identifier blockId = BuiltInRegistries.BLOCK.getKey(target);
            context.getSource().sendFeedback(Component.literal(lang.get("blockfinder.status.target", blockId.getPath())));
            context.getSource().sendFeedback(Component.literal(lang.get("blockfinder.status.found", foundCount)));
        } else {
            context.getSource().sendFeedback(Component.literal(lang.get("blockfinder.target.none")));
        }

        context.getSource().sendFeedback(Component.literal(lang.get("blockfinder.status.radius", config.getScanRadius())));

        return Command.SINGLE_SUCCESS;
    }
}
