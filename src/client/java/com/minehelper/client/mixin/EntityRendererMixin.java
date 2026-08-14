package com.minehelper.client.mixin;

import com.minehelper.client.MineHelperClient;
import com.minehelper.client.feature.playeresp.PlayerEspConfig;
import com.minehelper.client.feature.mobesp.MobEspConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
    // Runs after vanilla's own extractRenderState so we override outlineColor last.
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void minehelper_injectGlowColor(T entity, S state, float partialTick, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Handle PlayerESP
        if (entity instanceof AbstractClientPlayer player) {
            var playerEsp = MineHelperClient.getPlayerEspFeature();
            if (playerEsp != null && playerEsp.isEnabled()
                    && playerEsp.getConfig().getRenderMode() == PlayerEspConfig.RenderMode.GLOW
                    && player != mc.player) {
                double radiusSq = (double) playerEsp.getConfig().getScanRadius() * playerEsp.getConfig().getScanRadius();
                if (mc.player.distanceToSqr(player) <= radiusSq) {
                    float[] rgba = playerEsp.getConfig().resolveColor(player);
                    int r = Math.round(rgba[0] * 255);
                    int g = Math.round(rgba[1] * 255);
                    int b = Math.round(rgba[2] * 255);
                    state.outlineColor = 0xFF000000 | (r << 16) | (g << 8) | b;
                }
            }
            return;
        }

        // Handle MobESP
        if (entity instanceof LivingEntity mob && !(mob instanceof Player)) {
            var mobEsp = MineHelperClient.getMobEspFeature();
            if (mobEsp != null && mobEsp.isEnabled()
                    && mobEsp.getConfig().getRenderMode() == MobEspConfig.RenderMode.GLOW
                    && mobEsp.getConfig().shouldRender(mob)) {
                double radiusSq = (double) mobEsp.getConfig().getScanRadius() * mobEsp.getConfig().getScanRadius();
                if (mc.player.distanceToSqr(mob) <= radiusSq) {
                    float[] rgba = mobEsp.getConfig().resolveColor(mob);
                    int r = Math.round(rgba[0] * 255);
                    int g = Math.round(rgba[1] * 255);
                    int b = Math.round(rgba[2] * 255);
                    state.outlineColor = 0xFF000000 | (r << 16) | (g << 8) | b;
                }
            }
        }
    }
}
