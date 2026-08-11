package com.minehelper.client.feature.autosprint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minehelper.MineHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AutoSprintConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("mh_autosprint.json");

    private boolean enabled = true;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (IOException e) {
            MineHelper.LOGGER.error("Failed to save AutoSprint config", e);
        }
    }

    public static AutoSprintConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                AutoSprintConfig cfg = GSON.fromJson(Files.readString(CONFIG_PATH), AutoSprintConfig.class);
                if (cfg != null) return cfg;
            } catch (IOException e) {
                MineHelper.LOGGER.error("Failed to load AutoSprint config", e);
            }
        }
        AutoSprintConfig cfg = new AutoSprintConfig();
        cfg.save();
        return cfg;
    }
}
