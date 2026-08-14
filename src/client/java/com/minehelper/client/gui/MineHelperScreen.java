package com.minehelper.client.gui;

import com.minehelper.client.LocalizationManager;
import com.minehelper.client.feature.autoclicker.AutoClickerFeature;
import com.minehelper.client.feature.autosprint.AutoSprintFeature;
import com.minehelper.client.feature.blockfinder.BlockFinderFeature;
import com.minehelper.client.feature.playeresp.PlayerEspFeature;
import com.minehelper.client.feature.mobesp.MobEspFeature;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MineHelperScreen extends Screen {
    private final LocalizationManager lang;
    private final String modVersion;
    private final BlockFinderFeature blockFinderFeature;
    private final PlayerEspFeature playerEspFeature;
    private final MobEspFeature mobEspFeature;
    private final AutoSprintFeature autoSprintFeature;
    private final AutoClickerFeature autoClickerFeature;
    private TabNavigationBar tabNavigationBar;
    private TabManager tabManager;

    public MineHelperScreen(LocalizationManager lang, String modVersion,
                            BlockFinderFeature blockFinderFeature,
                            PlayerEspFeature playerEspFeature,
                            MobEspFeature mobEspFeature,
                            AutoSprintFeature autoSprintFeature,
                            AutoClickerFeature autoClickerFeature) {
        super(Component.literal("MineHelper v" + modVersion));
        this.lang = lang;
        this.modVersion = modVersion;
        this.blockFinderFeature = blockFinderFeature;
        this.playerEspFeature = playerEspFeature;
        this.mobEspFeature = mobEspFeature;
        this.autoSprintFeature = autoSprintFeature;
        this.autoClickerFeature = autoClickerFeature;
    }

    @Override
    protected void init() {
        super.init();

        tabManager = new TabManager(this::addRenderableWidget, this::removeWidget);

        GeneralTab generalTab     = new GeneralTab(lang, modVersion, autoSprintFeature, autoClickerFeature);
        BlockFinderTab bfTab      = new BlockFinderTab(lang, blockFinderFeature);
        PlayerEspTab espTab       = new PlayerEspTab(lang, playerEspFeature, mobEspFeature);

        tabNavigationBar = TabNavigationBar.builder(tabManager, width)
            .addTabs(generalTab, bfTab, espTab)
            .build();
        addRenderableWidget(tabNavigationBar);
        tabNavigationBar.arrangeElements();

        int navBarBottom = tabNavigationBar.getRectangle().bottom();
        ScreenRectangle tabArea = new ScreenRectangle(
            10, navBarBottom + 10,
            width - 20, height - navBarBottom - 40
        );

        generalTab.doLayout(tabArea);
        bfTab.doLayout(tabArea);
        espTab.doLayout(tabArea);

        tabNavigationBar.selectTab(0, false);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
