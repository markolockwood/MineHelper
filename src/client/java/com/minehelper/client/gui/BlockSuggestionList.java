package com.minehelper.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class BlockSuggestionList extends ObjectSelectionList<BlockSuggestionList.Entry> {
    private final Consumer<Block> onSelect;

    public BlockSuggestionList(Minecraft mc, int width, int height, int y, Consumer<Block> onSelect) {
        super(mc, width, height, y, 20);
        this.onSelect = onSelect;
    }

    public void updateSuggestions(String search) {
        clearEntries();
        String lowerSearch = search.toLowerCase();

        if (!lowerSearch.isEmpty()) {
            for (Identifier id : BuiltInRegistries.BLOCK.keySet()) {
                if (id.getPath().contains(lowerSearch)) {
                    Block block = BuiltInRegistries.BLOCK.get(id).orElseThrow().value();
                    addEntry(new Entry(block, id));
                }
            }
        }
    }

    public class Entry extends ObjectSelectionList.Entry<Entry> {
        private final Block block;
        private final Identifier id;

        public Entry(Block block, Identifier id) {
            this.block = block;
            this.id = id;
        }

        @Override
        public Component getNarration() {
            return Component.literal(id.getPath());
        }

        @Override
        public void extractContent(GuiGraphicsExtractor extractor, int index, int y, boolean hovered, float partialTick) {
            Minecraft mc = Minecraft.getInstance();
            int color = hovered ? 0xFFFF00 : 0xFFFFFF;
            String text = id.getPath();
            // Use content coordinates: y parameter is relative to content area
            int renderX = getContentX() + 5;
            int renderY = getContentY() + 2;
            com.minehelper.MineHelper.LOGGER.info("Rendering entry: {} at contentX={}, contentY={}, getX={}, getY={}",
                text, getContentX(), getContentY(), getX(), getY());
            extractor.text(mc.font, text, renderX, renderY, color);
        }

        @Override
        public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean withinBounds) {
            if (withinBounds && event.button() == 0) {
                onSelect.accept(block);
                return true;
            }
            return false;
        }
    }
}
