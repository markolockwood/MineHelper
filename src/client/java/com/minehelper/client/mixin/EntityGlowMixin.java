package com.minehelper.client.mixin;

import com.minehelper.client.MineHelperClient;
import com.minehelper.client.feature.playeresp.PlayerEspConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityGlowMixin {
    // Forces vanilla to treat ESP-targeted players as glowing so they're always
    // submitted to the outline buffer, even when occluded by blocks (fixes culling).
    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    private void minehelper_forceGlowForEsp(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        if (!(self instanceof AbstractClientPlayer player)) return;

        var esp = MineHelperClient.getPlayerEspFeature();
        if (esp == null || !esp.isEnabled()) return;
        if (esp.getConfig().getRenderMode() != PlayerEspConfig.RenderMode.GLOW) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || player == mc.player) return;

        double radiusSq = (double) esp.getConfig().getScanRadius() * esp.getConfig().getScanRadius();
        if (mc.player.distanceToSqr(player) > radiusSq) return;

        // Force glowing = true so vanilla always extracts and renders outline
        cir.setReturnValue(true);
    }
}
