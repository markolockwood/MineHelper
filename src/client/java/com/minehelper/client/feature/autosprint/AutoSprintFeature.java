package com.minehelper.client.feature.autosprint;

import com.minehelper.client.LocalizationManager;
import com.minehelper.client.feature.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

public class AutoSprintFeature implements Feature {
    private final AutoSprintConfig config;
    private final LocalizationManager localization;

    public AutoSprintFeature(LocalizationManager localization) {
        this.localization = localization;
        this.config = AutoSprintConfig.load();
    }

    @Override
    public String getId() {
        return "autosprint";
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        config.setEnabled(enabled);
        config.save();
    }

    @Override
    public void onTick(Minecraft client) {
        if (!isEnabled()) return;

        LocalPlayer player = client.player;
        if (player == null) return;

        // Проверяем условия для спринта
        if (shouldSprint(player)) {
            player.setSprinting(true);
        }
    }

    private boolean shouldSprint(LocalPlayer player) {
        // Игрок должен двигаться вперёд
        if (!player.input.hasForwardImpulse()) {
            return false;
        }

        // Не в воде/лаве (если нет Dolphin's Grace)
        if (player.isInWater() && !player.hasEffect(net.minecraft.world.effect.MobEffects.DOLPHINS_GRACE)) {
            return false;
        }

        // Достаточно еды
        if (player.getFoodData().getFoodLevel() <= 6) {
            return false;
        }

        // Не использует предмет (еда, лук и т.п.)
        if (player.isUsingItem()) {
            return false;
        }

        // Не приседает
        if (player.isShiftKeyDown()) {
            return false;
        }

        // Не в воздухе при старте спринта (можно бежать в воздухе, но нельзя начать)
        // На самом деле vanilla позволяет спринтить в воздухе если уже спринтишь
        // Упростим: просто пытаемся включить спринт, vanilla сам решит можно ли

        return true;
    }

    public void toggleWithChatFeedback(Minecraft client) {
        setEnabled(!isEnabled());
        if (client.player != null) {
            String status = isEnabled()
                ? localization.get("autosprint.enabled")
                : localization.get("autosprint.disabled");
            client.player.sendSystemMessage(Component.literal(status));
        }
    }

    public AutoSprintConfig getConfig() {
        return config;
    }
}
