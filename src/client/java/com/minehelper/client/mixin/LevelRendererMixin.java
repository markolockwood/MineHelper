package com.minehelper.client.mixin;

import com.minehelper.client.MineHelperClient;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    // Forces the vanilla entity outline post-process pass to run while ESP is
    // active. Without this the pass is skipped unless the player is a spectator
    // watching a glowing entity, so glow outlines would never appear.
    @Inject(method = "shouldShowEntityOutlines", at = @At("HEAD"), cancellable = true)
    private void minehelper_forceOutlines(CallbackInfoReturnable<Boolean> cir) {
        var playerEsp = MineHelperClient.getPlayerEspFeature();
        if (playerEsp != null && playerEsp.isEnabled()
                && playerEsp.getConfig().getRenderMode() == com.minehelper.client.feature.playeresp.PlayerEspConfig.RenderMode.GLOW) {
            cir.setReturnValue(true);
            return;
        }

        var mobEsp = MineHelperClient.getMobEspFeature();
        if (mobEsp != null && mobEsp.isEnabled()
                && mobEsp.getConfig().getRenderMode() == com.minehelper.client.feature.mobesp.MobEspConfig.RenderMode.GLOW) {
            cir.setReturnValue(true);
        }
    }
}
