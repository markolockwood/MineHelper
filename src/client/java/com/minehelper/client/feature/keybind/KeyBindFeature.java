package com.minehelper.client.feature.keybind;

import com.minehelper.client.LocalizationManager;
import com.minehelper.client.feature.Feature;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom key binding system - maps arbitrary keys to client commands.
 */
public class KeyBindFeature implements Feature {
    private final LocalizationManager lang;
    private final KeyBindConfig config;
    private final Map<Integer, Boolean> keyStates = new HashMap<>();

    public KeyBindFeature(LocalizationManager lang) {
        this.lang = lang;
        this.config = KeyBindConfig.load();
    }

    @Override
    public String getId() {
        return "keybind";
    }

    @Override
    public boolean isEnabled() {
        return true; // Always enabled
    }

    @Override
    public void setEnabled(boolean enabled) {
        // Always enabled, no-op
    }

    @Override
    public void onTick(Minecraft client) {
        for (KeyBind bind : config.getBinds()) {
            int keyCode = bind.getKeyCode();
            boolean pressed = InputConstants.isKeyDown(client.getWindow(), keyCode);
            boolean wasPressedBefore = keyStates.getOrDefault(keyCode, false);

            // Trigger on press (rising edge)
            if (pressed && !wasPressedBefore) {
                executeCommand(client, bind.getCommand());
            }

            keyStates.put(keyCode, pressed);
        }
    }

    private void executeCommand(Minecraft client, String command) {
        if (client.player != null && command != null && !command.isEmpty()) {
            String fullCommand = command.startsWith("/") ? command : "/" + command;
            client.player.connection.sendCommand(fullCommand.substring(1));
        }
    }

    public KeyBindConfig getConfig() {
        return config;
    }

    public LocalizationManager getLang() {
        return lang;
    }
}
