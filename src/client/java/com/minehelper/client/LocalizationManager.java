package com.minehelper.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minehelper.MineHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LocalizationManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("minehelper_lang.json");

    private String currentLang = "en";
    private final Map<String, Map<String, String>> translations = new HashMap<>();

    public LocalizationManager() {
        loadTranslations();
        loadConfig();
    }

    private void loadTranslations() {
        translations.put("en", new HashMap<>());
        translations.put("ru", new HashMap<>());

        // English
        Map<String, String> en = translations.get("en");
        en.put("welcome.title", "§eMineHelper v%s");
        en.put("welcome.help", "§7For settings type §b/minehelper");
        en.put("info.header", "§e=== MineHelper v%s ===");
        en.put("info.keybind", "§7• Keybind:§r Bind a key in §bControls → Misc → Toggle Block Finder");
        en.put("info.search", "§7• Search:§r §b/blockfinder set <block>§r (e.g. ancient_debris)");
        en.put("info.toggle", "§7• Toggle:§r §b/blockfinder toggle§r or use your keybind");
        en.put("info.commands", "§7• Commands:§r §b/blockfinder status§r, §b/blockfinder clear§r, §b/blockfinder radius <16-128>§r, §b/blockfinder color <r> <g> <b>");
        en.put("info.lang", "§7• Language:§r §b/minehelper lang <en|ru>");
        en.put("lang.changed", "Language changed to: %s");
        en.put("lang.unknown", "Unknown language: %s. Available: en, ru");

        // GUI - General tab
        en.put("gui.general.version", "§e=== MineHelper v%s ===");
        en.put("gui.general.language", "§7Language:");
        en.put("gui.general.info.keybind", "§7• Keybind (enable/disable block finder):§r Controls → Misc → Toggle Block Finder");
        en.put("gui.general.info.commands", "§7• Commands:§r /blockfinder, /minehelper");

        // GUI - BlockFinder tab
        en.put("gui.blockfinder.search", "§7Search block (e.g., ancient_debris):");
        en.put("gui.blockfinder.doubleclick", "§7Double-click icon to select");
        en.put("gui.blockfinder.selected", "§7Selected: §e%s");
        en.put("gui.blockfinder.selected.none", "§7Selected: §8None");
        en.put("gui.blockfinder.radius", "Radius: §b%d");
        en.put("gui.button.minus", "-");
        en.put("gui.button.plus", "+");

        // BlockFinder chat messages
        en.put("blockfinder.enabled", "§a✓ BlockFinder enabled");
        en.put("blockfinder.disabled", "§c✗ BlockFinder disabled");
        en.put("blockfinder.target.set", "§eTarget set: §b%s");
        en.put("blockfinder.target.none", "§cNo target block selected");
        en.put("blockfinder.found", "§aFound §e%d§a blocks in radius §b%d");
        en.put("blockfinder.notfound", "§7No §e%s§7 found nearby");
        en.put("blockfinder.cleared", "§7Scanner cache cleared");
        en.put("blockfinder.radius.set", "§eScan radius set to: §b%d");
        en.put("blockfinder.color.set", "§eHighlight color set to §bRGB(%d, %d, %d)");
        en.put("blockfinder.status.header", "§e=== BlockFinder Status ===");
        en.put("blockfinder.status.enabled", "§7Enabled: %s");
        en.put("blockfinder.status.target", "§7Target: §b%s");
        en.put("blockfinder.status.found", "§7Found: §e%d §7blocks");
        en.put("blockfinder.status.radius", "§7Radius: §b%d");
        en.put("blockfinder.check.result", "§eBlock name: §b%s");
        en.put("blockfinder.check.none", "§7Not looking at any block");
        en.put("gui.yes", "§aYes");
        en.put("gui.no", "§cNo");

        // PlayerESP
        en.put("playeresp.enabled", "§a✓ PlayerESP enabled");
        en.put("playeresp.disabled", "§c✗ PlayerESP disabled");
        en.put("playeresp.mode.set", "§eRender mode: §b%s");
        en.put("playeresp.colormode.set", "§eColor mode: §b%s");
        en.put("playeresp.color.set", "§eColor: §bRGB(%d, %d, %d)");
        en.put("playeresp.radius.set", "§eRadius: §b%d");
        en.put("playeresp.toggle", "Toggle");
        en.put("playeresp.status", "§7Status: %s");
        en.put("playeresp.mode", "§7Render mode: §b%s");
        en.put("playeresp.colormode", "§7Color mode: §b%s");
        en.put("playeresp.color", "§7Color: §bRGB(%d, %d, %d)");
        en.put("playeresp.radius", "Radius: §b%d");

        // AutoSprint
        en.put("autosprint.enabled", "§a✓ AutoSprint enabled");
        en.put("autosprint.disabled", "§c✗ AutoSprint disabled");
        en.put("autosprint.status.enabled", "§7AutoSprint: §aEnabled");
        en.put("autosprint.status.disabled", "§7AutoSprint: §cDisabled");

        // AutoClicker
        en.put("autoclicker.enabled", "§a✓ AutoClicker enabled");
        en.put("autoclicker.disabled", "§c✗ AutoClicker disabled");
        en.put("autoclicker.cps", "CPS: §b%d");

        // Russian
        Map<String, String> ru = translations.get("ru");
        ru.put("welcome.title", "§eMineHelper v%s");
        ru.put("welcome.help", "§7Для настроек введи §b/minehelper");
        ru.put("welcome", "§eMineHelper v%s§r загружен. §b/minehelper info§r для справки.");
        ru.put("info.header", "§e=== MineHelper v%s ===");
        ru.put("info.keybind", "§7• Кейбинд:§r Назначь клавишу в §bУправление → Misc → Toggle Block Finder");
        ru.put("info.search", "§7• Поиск:§r §b/blockfinder set <блок>§r (например ancient_debris)");
        ru.put("info.toggle", "§7• Включить:§r §b/blockfinder toggle§r или назначенная клавиша");
        ru.put("info.commands", "§7• Команды:§r §b/blockfinder status§r, §b/blockfinder clear§r, §b/blockfinder radius <16-128>§r, §b/blockfinder color <r> <g> <b>");
        ru.put("info.lang", "§7• Язык:§r §b/minehelper lang <en|ru>");
        ru.put("lang.changed", "Язык изменён на: %s");
        ru.put("lang.unknown", "Неизвестный язык: %s. Доступны: en, ru");

        // GUI - General tab
        ru.put("gui.general.version", "§e=== MineHelper v%s ===");
        ru.put("gui.general.language", "§7Язык:");
        ru.put("gui.general.info.keybind", "§7• Кейбинд (вкл/выкл. поиск блока):§r Управление → Misc → Toggle Block Finder");
        ru.put("gui.general.info.commands", "§7• Команды:§r /blockfinder, /minehelper");

        // GUI - BlockFinder tab
        ru.put("gui.blockfinder.search", "§7Поиск блока (например, ancient_debris):");
        ru.put("gui.blockfinder.doubleclick", "§7Двойной клик для выбора");
        ru.put("gui.blockfinder.selected", "§7Выбрано: §e%s");
        ru.put("gui.blockfinder.selected.none", "§7Выбрано: §8Ничего");
        ru.put("gui.blockfinder.radius", "Радиус: §b%d");
        ru.put("gui.button.minus", "-");
        ru.put("gui.button.plus", "+");

        // BlockFinder chat messages
        ru.put("blockfinder.enabled", "§a✓ BlockFinder включен");
        ru.put("blockfinder.disabled", "§c✗ BlockFinder выключен");
        ru.put("blockfinder.target.set", "§eЦель установлена: §b%s");
        ru.put("blockfinder.target.none", "§cБлок для поиска не выбран");
        ru.put("blockfinder.found", "§aНайдено §e%d§a блоков в радиусе §b%d");
        ru.put("blockfinder.notfound", "§7§e%s§7 не найден поблизости");
        ru.put("blockfinder.cleared", "§7Кэш сканера очищен");
        ru.put("blockfinder.radius.set", "§eРадиус сканирования: §b%d");
        ru.put("blockfinder.color.set", "§eЦвет подсветки: §bRGB(%d, %d, %d)");
        ru.put("blockfinder.status.header", "§e=== Статус BlockFinder ===");
        ru.put("blockfinder.status.enabled", "§7Включен: %s");
        ru.put("blockfinder.status.target", "§7Цель: §b%s");
        ru.put("blockfinder.status.found", "§7Найдено: §e%d §7блоков");
        ru.put("blockfinder.status.radius", "§7Радиус: §b%d");
        ru.put("blockfinder.check.result", "§eИмя блока: §b%s");
        ru.put("blockfinder.check.none", "§7Вы не смотрите на блок");
        ru.put("gui.yes", "§aДа");
        ru.put("gui.no", "§cНет");

        // PlayerESP
        ru.put("playeresp.enabled", "§a✓ PlayerESP включен");
        ru.put("playeresp.disabled", "§c✗ PlayerESP выключен");
        ru.put("playeresp.mode.set", "§eРежим отображения: §b%s");
        ru.put("playeresp.colormode.set", "§eРежим цвета: §b%s");
        ru.put("playeresp.color.set", "§eЦвет: §bRGB(%d, %d, %d)");
        ru.put("playeresp.radius.set", "§eРадиус: §b%d");
        ru.put("playeresp.toggle", "Вкл/Выкл");
        ru.put("playeresp.status", "§7Статус: %s");
        ru.put("playeresp.mode", "§7Режим: §b%s");
        ru.put("playeresp.colormode", "§7Цвет: §b%s");
        ru.put("playeresp.color", "§7Цвет: §bRGB(%d, %d, %d)");
        ru.put("playeresp.radius", "Радиус: §b%d");

        // AutoSprint
        ru.put("autosprint.enabled", "§a✓ АвтоСпринт включен");
        ru.put("autosprint.disabled", "§c✗ АвтоСпринт выключен");
        ru.put("autosprint.status.enabled", "§7АвтоСпринт: §aВключен");
        ru.put("autosprint.status.disabled", "§7АвтоСпринт: §cВыключен");

        // AutoClicker
        ru.put("autoclicker.enabled", "§a✓ АвтоКликер включен");
        ru.put("autoclicker.disabled", "§c✗ АвтоКликер выключен");
        ru.put("autoclicker.cps", "CPS: §b%d");
    }

    public String get(String key, Object... args) {
        Map<String, String> lang = translations.get(currentLang);
        if (lang == null) {
            lang = translations.get("en");
        }
        String template = lang.getOrDefault(key, key);
        if (args.length > 0) {
            return String.format(template, args);
        }
        return template;
    }

    public String getCurrentLang() {
        return currentLang;
    }

    public boolean setLang(String lang) {
        if (!translations.containsKey(lang)) {
            return false;
        }
        this.currentLang = lang;
        saveConfig();
        return true;
    }

    private void loadConfig() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                LangConfig config = GSON.fromJson(json, LangConfig.class);
                if (translations.containsKey(config.lang)) {
                    this.currentLang = config.lang;
                }
                MineHelper.LOGGER.info("Language config loaded: {}", currentLang);
            } catch (IOException e) {
                MineHelper.LOGGER.error("Failed to load language config", e);
            }
        }
    }

    private void saveConfig() {
        try {
            LangConfig config = new LangConfig();
            config.lang = currentLang;
            String json = GSON.toJson(config);
            Files.writeString(CONFIG_PATH, json);
            MineHelper.LOGGER.info("Language config saved: {}", currentLang);
        } catch (IOException e) {
            MineHelper.LOGGER.error("Failed to save language config", e);
        }
    }

    private static class LangConfig {
        String lang = "en";
    }
}
