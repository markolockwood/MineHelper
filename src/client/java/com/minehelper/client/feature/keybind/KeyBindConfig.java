package com.minehelper.client.feature.keybind;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minehelper.MineHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class KeyBindConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("mh_keybinds.json");

    private List<KeyBind> binds = new ArrayList<>();

    public List<KeyBind> getBinds() {
        return binds;
    }

    public void setBinds(List<KeyBind> binds) {
        this.binds = binds;
    }

    public void addBind(KeyBind bind) {
        binds.add(bind);
    }

    public void removeBind(KeyBind bind) {
        binds.remove(bind);
    }

    public void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (IOException e) {
            MineHelper.LOGGER.error("Failed to save KeyBind config", e);
        }
    }

    public static KeyBindConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                KeyBindConfig cfg = GSON.fromJson(Files.readString(CONFIG_PATH), KeyBindConfig.class);
                if (cfg != null) return cfg;
            } catch (IOException e) {
                MineHelper.LOGGER.error("Failed to load KeyBind config", e);
            }
        }
        KeyBindConfig cfg = new KeyBindConfig();
        cfg.save();
        return cfg;
    }
}
