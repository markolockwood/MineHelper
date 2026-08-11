package com.minehelper.client.gui;

import com.minehelper.client.LocalizationManager;
import com.minehelper.client.feature.blockfinder.BlockFinderFeature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class BlockFinderTab extends MineHelperTab {
    private static final int SLOTS_PER_ROW = 8;
    private static final int ROWS = 5;
    private static final int MAX_SUGGESTIONS = SLOTS_PER_ROW * ROWS; // 40 slots
    private final BlockFinderFeature feature;
    private final List<BlockSlotWidget> slotWidgets = new ArrayList<>();
    private final List<Block> currentSuggestions = new ArrayList<>();
    private EditBox searchBox;
    private StringWidget statusLabel;
    private StringWidget radiusLabel;

    public BlockFinderTab(LocalizationManager lang, BlockFinderFeature feature) {
        super(Component.literal("BlockFinder"), lang);
        this.feature = feature;
    }

    @Override
    protected void buildWidgets(ScreenRectangle tabArea) {
        int centerX = tabArea.left() + tabArea.width() / 2;
        int startY = tabArea.top() + 20;
        Minecraft mc = Minecraft.getInstance();

        widgets.add(new StringWidget(
            centerX - 150, startY, 300, 10,
            Component.literal(lang.get("gui.blockfinder.search")),
            mc.font
        ));

        widgets.add(new StringWidget(
            centerX - 150, startY + 10, 300, 10,
            Component.literal(lang.get("gui.blockfinder.doubleclick")),
            mc.font
        ));

        searchBox = new EditBox(mc.font, centerX - 150, startY + 20, 300, 20, Component.literal("Search"));
        searchBox.setMaxLength(100);
        searchBox.setResponder(this::onSearchChanged);
        widgets.add(searchBox);

        // Create block icon slots (8 columns × 5 rows = 40 slots)
        int slotSize = 18;
        int slotSpacing = 2;
        int gridWidth = SLOTS_PER_ROW * (slotSize + slotSpacing);
        int gridStartX = centerX - gridWidth / 2;

        for (int i = 0; i < MAX_SUGGESTIONS; i++) {
            int index = i;
            int col = i % SLOTS_PER_ROW;
            int row = i / SLOTS_PER_ROW;
            int slotX = gridStartX + col * (slotSize + slotSpacing);
            int slotY = startY + 45 + row * (slotSize + slotSpacing);

            BlockSlotWidget slot = new BlockSlotWidget(slotX, slotY, () -> onSlotClicked(index));
            slotWidgets.add(slot);
            widgets.add(slot);
        }

        statusLabel = new StringWidget(
            centerX - 150, startY + 165, 300, 10,
            getStatusText(),
            mc.font
        );
        widgets.add(statusLabel);

        // Radius controls
        radiusLabel = new StringWidget(
            centerX - 60, startY + 180, 120, 20,
            getRadiusText(),
            mc.font
        );
        widgets.add(radiusLabel);

        widgets.add(Button.builder(Component.literal(lang.get("gui.button.minus")), b -> adjustRadius(-16))
            .bounds(centerX - 150, startY + 180, 30, 20).build());

        widgets.add(Button.builder(Component.literal(lang.get("gui.button.plus")), b -> adjustRadius(16))
            .bounds(centerX + 70, startY + 180, 30, 20).build());

        // Pre-fill the search box with the currently selected block, if any
        Block currentTarget = feature.getScanner().getTargetBlock();
        if (currentTarget != null) {
            searchBox.setValue(BuiltInRegistries.BLOCK.getKey(currentTarget).getPath());
        }
    }

    private void onSearchChanged(String text) {
        currentSuggestions.clear();
        String lowerSearch = text.toLowerCase();

        if (!lowerSearch.isEmpty()) {
            for (Identifier id : BuiltInRegistries.BLOCK.keySet()) {
                if (id.getPath().contains(lowerSearch)) {
                    currentSuggestions.add(BuiltInRegistries.BLOCK.get(id).orElseThrow().value());
                    if (currentSuggestions.size() >= MAX_SUGGESTIONS) break;
                }
            }
        }

        // Update slot widgets
        for (int i = 0; i < MAX_SUGGESTIONS; i++) {
            BlockSlotWidget slot = slotWidgets.get(i);
            if (i < currentSuggestions.size()) {
                slot.setBlock(currentSuggestions.get(i));
            } else {
                slot.clear();
            }
        }
    }

    private void onSlotClicked(int index) {
        if (index >= currentSuggestions.size()) {
            return;
        }
        Block block = currentSuggestions.get(index);
        onBlockSelected(block);
    }

    private void onBlockSelected(Block block) {
        feature.getScanner().setTargetBlock(block);
        searchBox.setValue(BuiltInRegistries.BLOCK.getKey(block).getPath());
        statusLabel.setMessage(getStatusText());
    }

    private void adjustRadius(int delta) {
        int current = feature.getConfig().getScanRadius();
        feature.getConfig().setScanRadius(current + delta);
        feature.getConfig().save();
        feature.getScanner().clear();
        radiusLabel.setMessage(getRadiusText());
    }

    private Component getStatusText() {
        Block target = feature.getScanner().getTargetBlock();
        if (target == null) {
            return Component.literal(lang.get("gui.blockfinder.selected.none"));
        }
        Identifier id = BuiltInRegistries.BLOCK.getKey(target);
        return Component.literal(lang.get("gui.blockfinder.selected", id.getPath()));
    }

    private Component getRadiusText() {
        return Component.literal(lang.get("gui.blockfinder.radius", feature.getConfig().getScanRadius()));
    }
}
