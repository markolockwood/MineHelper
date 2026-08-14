package com.minehelper.client.mixin;

import com.minehelper.client.MineHelperClient;
import com.minehelper.client.feature.playeresp.PlayerEspConfig;
import com.minehelper.client.feature.mobesp.MobEspConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityGlowMixin {
    // Forces vanilla to treat ESP-targeted entities as glowing so they're always
    // submitted to the outline buffer, even when occluded by blocks (fixes culling).
    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    private void minehelper_forceGlowForEsp(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Handle PlayerESP
        if (self instanceof AbstractClientPlayer player) {
            var playerEsp = MineHelperClient.getPlayerEspFeature();
            if (playerEsp != null && playerEsp.isEnabled()
                    && playerEsp.getConfig().getRenderMode() == PlayerEspConfig.RenderMode.GLOW
                    && player != mc.player) {
                double radiusSq = (double) playerEsp.getConfig().getScanRadius() * playerEsp.getConfig().getScanRadius();
                if (mc.player.distanceToSqr(player) <= radiusSq) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }

        // Handle MobESP
        if (self instanceof LivingEntity mob && !(mob instanceof Player)) {
            var mobEsp = MineHelperClient.getMobEspFeature();
            if (mobEsp != null && mobEsp.isEnabled()
                    && mobEsp.getConfig().getRenderMode() == MobEspConfig.RenderMode.GLOW
                    && mobEsp.getConfig().shouldRender(mob)) {
                double radiusSq = (double) mobEsp.getConfig().getScanRadius() * mobEsp.getConfig().getScanRadius();
                if (mc.player.distanceToSqr(mob) <= radiusSq) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
