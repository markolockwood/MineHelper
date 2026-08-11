package com.minehelper.client.gui;

import com.minehelper.client.LocalizationManager;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Base for MineHelper's GUI tabs. Widgets are created exactly once (on the
 * first {@link #doLayout}) and kept alive across tab switches - mirroring
 * vanilla's {@code GridLayoutTab}. {@link TabManager} re-adds the same
 * widget instances via {@link #visitChildren} every time this tab is
 * selected, so recreating them on every layout pass would desync the
 * widgets it just added from the ones actually rendered.
 */
public abstract class MineHelperTab implements Tab {
    protected final Component title;
    protected final LocalizationManager lang;
    protected final List<AbstractWidget> widgets = new ArrayList<>();
    private boolean built = false;

    public MineHelperTab(Component title, LocalizationManager lang) {
        this.title = title;
        this.lang = lang;
    }

    @Override
    public Component getTabTitle() {
        return title;
    }

    @Override
    public Component getTabExtraNarration() {
        return title;
    }

    @Override
    public void doLayout(ScreenRectangle tabArea) {
        if (!built) {
            buildWidgets(tabArea);
            built = true;
        } else {
            repositionWidgets(tabArea);
        }
    }

    /** Called once to create all widgets for this tab. */
    protected abstract void buildWidgets(ScreenRectangle tabArea);

    /** Called on every subsequent layout pass (e.g. window resize) to move existing widgets. */
    protected void repositionWidgets(ScreenRectangle tabArea) {
    }

    @Override
    public void visitChildren(Consumer<AbstractWidget> visitor) {
        widgets.forEach(visitor);
    }
}
