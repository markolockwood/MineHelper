package com.minehelper.client.gui;

import com.minehelper.client.LocalizationManager;
import com.minehelper.client.feature.autoclicker.AutoClickerFeature;
import com.minehelper.client.feature.autosprint.AutoSprintFeature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

public class GeneralTab extends MineHelperTab {
    private final String modVersion;
    private final AutoSprintFeature autoSprintFeature;
    private final AutoClickerFeature autoClickerFeature;
    private StringWidget autoSprintStatus;
    private StringWidget autoClickerStatus;
    private StringWidget autoClickerCpsLabel;

    public GeneralTab(LocalizationManager lang, String modVersion, AutoSprintFeature autoSprintFeature, AutoClickerFeature autoClickerFeature) {
        super(Component.literal("General"), lang);
        this.modVersion = modVersion;
        this.autoSprintFeature = autoSprintFeature;
        this.autoClickerFeature = autoClickerFeature;
    }

    @Override
    protected void buildWidgets(ScreenRectangle tabArea) {
        int centerX = tabArea.left() + tabArea.width() / 2;
        int startY = tabArea.top() + 20;
        Minecraft mc = Minecraft.getInstance();

        widgets.add(new StringWidget(
            centerX - 100, startY, 200, 20,
            Component.literal(lang.get("gui.general.version", modVersion)),
            mc.font
        ));

        widgets.add(new StringWidget(
            centerX - 100, startY + 40, 200, 20,
            Component.literal(lang.get("gui.general.language")),
            mc.font
        ));

        widgets.add(Button.builder(
            Component.literal("English"),
            btn -> lang.setLang("en")
        ).bounds(centerX - 105, startY + 65, 100, 20).build());

        widgets.add(Button.builder(
            Component.literal("Русский"),
            btn -> lang.setLang("ru")
        ).bounds(centerX + 5, startY + 65, 100, 20).build());

        // AutoSprint
        int sprintY = startY + 100;
        widgets.add(new StringWidget(
            centerX - 100, sprintY, 200, 20,
            Component.literal("§7AutoSprint:"),
            mc.font
        ));

        autoSprintStatus = new StringWidget(
            centerX - 100, sprintY + 20, 200, 20,
            getAutoSprintStatusText(),
            mc.font
        );
        widgets.add(autoSprintStatus);

        widgets.add(Button.builder(
            Component.literal(lang.get("playeresp.toggle")),
            btn -> {
                autoSprintFeature.setEnabled(!autoSprintFeature.isEnabled());
                autoSprintStatus.setMessage(getAutoSprintStatusText());
            }
        ).bounds(centerX - 100, sprintY + 45, 100, 20).build());

        // AutoClicker
        int clickerY = sprintY + 80;
        widgets.add(new StringWidget(
            centerX - 100, clickerY, 200, 20,
            Component.literal("§7AutoClicker:"),
            mc.font
        ));

        autoClickerStatus = new StringWidget(
            centerX - 100, clickerY + 20, 200, 20,
            getAutoClickerStatusText(),
            mc.font
        );
        widgets.add(autoClickerStatus);

        widgets.add(Button.builder(
            Component.literal(lang.get("playeresp.toggle")),
            btn -> {
                autoClickerFeature.setEnabled(!autoClickerFeature.isEnabled());
                autoClickerStatus.setMessage(getAutoClickerStatusText());
            }
        ).bounds(centerX - 100, clickerY + 45, 100, 20).build());

        // CPS control
        autoClickerCpsLabel = new StringWidget(
            centerX - 100, clickerY + 75, 200, 20,
            getCpsText(),
            mc.font
        );
        widgets.add(autoClickerCpsLabel);

        widgets.add(Button.builder(Component.literal("-"), btn -> {
            autoClickerFeature.getConfig().setCps(autoClickerFeature.getConfig().getCps() - 1);
            autoClickerFeature.getConfig().save();
            autoClickerCpsLabel.setMessage(getCpsText());
        }).bounds(centerX - 100, clickerY + 95, 30, 20).build());

        widgets.add(Button.builder(Component.literal("+"), btn -> {
            autoClickerFeature.getConfig().setCps(autoClickerFeature.getConfig().getCps() + 1);
            autoClickerFeature.getConfig().save();
            autoClickerCpsLabel.setMessage(getCpsText());
        }).bounds(centerX - 65, clickerY + 95, 30, 20).build());

        int infoY = clickerY + 130;
        addInfoLine(mc, centerX, infoY, lang.get("gui.general.info.keybind"));
        addInfoLine(mc, centerX, infoY + 20, lang.get("gui.general.info.commands"));
    }

    private Component getAutoClickerStatusText() {
        String status = autoClickerFeature.isEnabled() ? lang.get("gui.yes") : lang.get("gui.no");
        return Component.literal("§7Status: " + status);
    }

    private Component getCpsText() {
        return Component.literal(lang.get("autoclicker.cps", autoClickerFeature.getConfig().getCps()));
    }

    private Component getAutoSprintStatusText() {
        String status = autoSprintFeature.isEnabled() ? lang.get("gui.yes") : lang.get("gui.no");
        return Component.literal("§7Status: " + status);
    }

    @Override
    protected void repositionWidgets(ScreenRectangle tabArea) {
        // Window resizes are rare enough that we just leave widgets at their
        // original position rather than re-deriving every layout constant.
    }

    private void addInfoLine(Minecraft mc, int centerX, int y, String text) {
        widgets.add(new StringWidget(
            centerX - 150, y, 300, 10,
            Component.literal(text),
            mc.font
        ));
    }
}
