package com.minehelper.client.feature.autoclicker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minehelper.MineHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AutoClickerConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("mh_autoclicker.json");

    private boolean enabled = false;
    private int cps = 10; // 8-12 диапазон, дефолт 10

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public int getCps() { return cps; }
    public void setCps(int cps) {
        this.cps = Math.max(1, Math.min(20, cps)); // Ограничение 1-20 CPS
    }

    public void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (IOException e) {
            MineHelper.LOGGER.error("Failed to save AutoClicker config", e);
        }
    }

    public static AutoClickerConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                AutoClickerConfig cfg = GSON.fromJson(Files.readString(CONFIG_PATH), AutoClickerConfig.class);
                if (cfg != null) return cfg;
            } catch (IOException e) {
                MineHelper.LOGGER.error("Failed to load AutoClicker config", e);
            }
        }
        AutoClickerConfig cfg = new AutoClickerConfig();
        cfg.save();
        return cfg;
    }
}
