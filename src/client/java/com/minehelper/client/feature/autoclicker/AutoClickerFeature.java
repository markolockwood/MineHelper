package com.minehelper.client.feature.autoclicker;

import com.minehelper.client.LocalizationManager;
import com.minehelper.client.feature.Feature;
import com.minehelper.client.mixin.MinecraftAttackAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.Random;

public class AutoClickerFeature implements Feature {
    private final AutoClickerConfig config;
    private final LocalizationManager localization;
    private final Random random = new Random();
    private int clickDelay = 0;

    public AutoClickerFeature(LocalizationManager localization) {
        this.localization = localization;
        this.config = AutoClickerConfig.load();
    }

    @Override
    public String getId() {
        return "autoclicker";
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
        if (client.player == null) return;
        if (client.screen != null) return; // Не в GUI/инвентаре

        if (clickDelay > 0) {
            clickDelay--;
            return;
        }

        // Проверяем зажат ли ЛКМ
        if (client.options.keyAttack.isDown()) {
            MinecraftAttackAccessor accessor = (MinecraftAttackAccessor)client;

            // Сбрасываем cooldown атаки
            accessor.setMissTime(0);

            // Вызываем ванильный метод атаки
            accessor.invokeStartAttack();

            // Задержка в тиках: CPS → ticks
            // 20 ticks = 1 second, поэтому delay = 20 / CPS
            int baseCps = config.getCps();
            int baseDelay = 20 / baseCps;

            // Добавляем рандом ±30% для большей человечности
            int randomRange = Math.max(1, baseDelay * 3 / 10); // 30% от базовой задержки
            int randomOffset = random.nextInt(randomRange * 2 + 1) - randomRange;

            clickDelay = Math.max(1, baseDelay + randomOffset);
        }
    }

    public void toggleWithChatFeedback(Minecraft client) {
        setEnabled(!isEnabled());
        if (client.player != null) {
            String status = isEnabled()
                ? localization.get("autoclicker.enabled")
                : localization.get("autoclicker.disabled");
            client.player.sendSystemMessage(Component.literal(status));
        }
    }

    public AutoClickerConfig getConfig() {
        return config;
    }
}
