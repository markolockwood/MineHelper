package com.minehelper.client.gui;

import com.minehelper.client.feature.keybind.KeyBind;
import com.minehelper.client.feature.keybind.KeyBindFeature;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class KeyBindEditScreen extends Screen {
    private final Screen parent;
    private final KeyBindFeature feature;
    private final KeyBind bind;
    private final boolean isNew;

    private Button keyButton;
    private EditBox commandField;
    private int selectedKeyCode = InputConstants.UNKNOWN.getValue();
    private boolean waitingForKey = false;

    public KeyBindEditScreen(Screen parent, KeyBindFeature feature, KeyBind existingBind) {
        super(Component.literal(existingBind == null ? "New Key Bind" : "Edit Key Bind"));
        this.parent = parent;
        this.feature = feature;
        this.isNew = existingBind == null;
        this.bind = existingBind != null ? existingBind : new KeyBind();

        if (existingBind != null) {
            this.selectedKeyCode = existingBind.getKeyCode();
        }
    }

    @Override
    protected void init() {
        super.init();

        int centerX = width / 2;
        int startY = height / 2 - 40;

        // Key selection button
        keyButton = Button.builder(
            Component.literal(getKeyButtonText()),
            btn -> {
                waitingForKey = true;
                btn.setMessage(Component.literal("Press any key..."));
            }
        ).bounds(centerX - 150, startY, 140, 20).build();
        addRenderableWidget(keyButton);

        // Command input field
        commandField = new EditBox(font, centerX + 10, startY, 140, 20, Component.literal("Command"));
        commandField.setHint(Component.literal("/command"));
        commandField.setValue(bind.getCommand() != null ? bind.getCommand() : "");
        commandField.setMaxLength(256);
        addRenderableWidget(commandField);

        // Save button
        addRenderableWidget(Button.builder(
            Component.literal("Save"),
            btn -> {
                if (selectedKeyCode != InputConstants.UNKNOWN.getValue() && !commandField.getValue().isEmpty()) {
                    bind.setKeyCode(selectedKeyCode);
                    bind.setCommand(commandField.getValue());

                    if (isNew) {
                        feature.getConfig().addBind(bind);
                    }
                    feature.getConfig().save();

                    minecraft.setScreen(parent);
                    if (parent instanceof KeyBindListScreen) {
                        ((KeyBindListScreen) parent).refreshList();
                    }
                }
            }
        ).bounds(centerX - 100, startY + 60, 200, 20).build());

        // Cancel button
        addRenderableWidget(Button.builder(
            Component.literal("Cancel"),
            btn -> minecraft.setScreen(parent)
        ).bounds(centerX - 100, startY + 85, 200, 20).build());
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (waitingForKey) {
            if (event.key() == InputConstants.KEY_ESCAPE) {
                waitingForKey = false;
                keyButton.setMessage(Component.literal(getKeyButtonText()));
                return true;
            }

            selectedKeyCode = event.key();
            waitingForKey = false;
            keyButton.setMessage(Component.literal(getKeyButtonText()));
            return true;
        }

        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean withinBounds) {
        if (waitingForKey) {
            // Map mouse buttons to key codes
            selectedKeyCode = InputConstants.Type.MOUSE.getOrCreate(event.button()).getValue();
            waitingForKey = false;
            keyButton.setMessage(Component.literal(getKeyButtonText()));
            return true;
        }

        return super.mouseClicked(event, withinBounds);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(extractor, mouseX, mouseY, partialTick);

        extractor.centeredText(font, title, width / 2, 20, 0xFFFFFF);

        int centerX = width / 2;
        int startY = height / 2 - 40;

        extractor.text(font, "Key:", centerX - 150, startY - 15, 0xAAAAAA);
        extractor.text(font, "Command:", centerX + 10, startY - 15, 0xAAAAAA);
    }

    private String getKeyButtonText() {
        if (selectedKeyCode == InputConstants.UNKNOWN.getValue()) {
            return "Click to bind...";
        }
        InputConstants.Key key = InputConstants.Type.KEYSYM.getOrCreate(selectedKeyCode);
        return key.getDisplayName().getString();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if (waitingForKey) {
            waitingForKey = false;
            keyButton.setMessage(Component.literal(getKeyButtonText()));
            return false;
        }
        return true;
    }
}
