package com.minehelper.client.gui;

import com.minehelper.client.feature.keybind.KeyBind;
import com.minehelper.client.feature.keybind.KeyBindFeature;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class KeyBindListScreen extends Screen {
    private final KeyBindFeature feature;
    private KeyBindList bindList;
    private Button addButton;

    public KeyBindListScreen(KeyBindFeature feature) {
        super(Component.literal("Custom Key Binds"));
        this.feature = feature;
    }

    @Override
    protected void init() {
        super.init();

        // List ends at height - 80 to leave space for buttons below
        bindList = new KeyBindList(this.minecraft, width, height - 80, 32, 25);
        addRenderableWidget(bindList);

        addButton = Button.builder(
            Component.literal("+ Add Bind"),
            btn -> minecraft.setScreen(new KeyBindEditScreen(this, feature, null))
        ).bounds(width / 2 - 100, height - 52, 200, 20).build();
        addRenderableWidget(addButton);

        addRenderableWidget(Button.builder(
            Component.literal("Done"),
            btn -> minecraft.setScreen(null)
        ).bounds(width / 2 - 100, height - 28, 200, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(extractor, mouseX, mouseY, partialTick);
        extractor.centeredText(font, title, width / 2, 12, 0xFFFFFF);
    }

    public void refreshList() {
        if (bindList != null) {
            bindList.refreshEntries();
        }
    }

    class KeyBindList extends ObjectSelectionList<KeyBindList.Entry> {
        public KeyBindList(net.minecraft.client.Minecraft minecraft, int width, int height, int y, int itemHeight) {
            super(minecraft, width, height, y, itemHeight);
            refreshEntries();
        }

        public void refreshEntries() {
            clearEntries();
            for (KeyBind bind : feature.getConfig().getBinds()) {
                addEntry(new Entry(bind));
            }
        }

        @Override
        public int getRowWidth() {
            return 400;
        }

        class Entry extends ObjectSelectionList.Entry<Entry> {
            private final KeyBind bind;
            private final Button deleteButton;
            private final net.minecraft.client.gui.components.StringWidget keyLabel;
            private final net.minecraft.client.gui.components.StringWidget cmdLabel;

            public Entry(KeyBind bind) {
                this.bind = bind;
                this.deleteButton = Button.builder(
                    Component.literal("Delete"),
                    btn -> {
                        feature.getConfig().removeBind(bind);
                        feature.getConfig().save();
                        refreshEntries();
                    }
                ).bounds(0, 0, 60, 20).build();

                InputConstants.Key key = InputConstants.Type.KEYSYM.getOrCreate(bind.getKeyCode());
                String keyName = key.getDisplayName().getString();
                String command = bind.getCommand();

                this.keyLabel = new net.minecraft.client.gui.components.StringWidget(
                    0, 0, 110, 20,
                    Component.literal("Key: " + keyName),
                    minecraft.font
                );

                this.cmdLabel = new net.minecraft.client.gui.components.StringWidget(
                    0, 0, 200, 20,
                    Component.literal("Cmd: " + command),
                    minecraft.font
                );
            }

            @Override
            public void extractContent(GuiGraphicsExtractor extractor, int index, int y, boolean hovered, float partialTick) {
                // Use content coordinates from Entry methods
                int renderX = getContentX();
                int renderY = getContentY();

                // Draw background rectangle
                extractor.fill(renderX, renderY, renderX + getContentWidth(), renderY + getContentHeight(),
                    hovered ? 0x80808080 : 0x40404040);

                // Position and render labels
                keyLabel.setX(renderX + 5);
                keyLabel.setY(renderY + 2);
                keyLabel.extractRenderState(extractor, 0, 0, partialTick);

                cmdLabel.setX(renderX + 120);
                cmdLabel.setY(renderY + 2);
                cmdLabel.extractRenderState(extractor, 0, 0, partialTick);

                // Position and render delete button
                deleteButton.setX(getContentX() + getContentWidth() - 65);
                deleteButton.setY(getContentY());
                deleteButton.extractRenderState(extractor, 0, 0, partialTick);
            }

            @Override
            public boolean mouseClicked(MouseButtonEvent event, boolean withinBounds) {
                return deleteButton.mouseClicked(event, withinBounds);
            }

            @Override
            public Component getNarration() {
                return Component.literal("Key bind entry");
            }
        }
    }
}
