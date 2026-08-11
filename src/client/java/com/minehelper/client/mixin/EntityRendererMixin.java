package com.minehelper.client.mixin;

import com.minehelper.client.MineHelperClient;
import com.minehelper.client.feature.playeresp.PlayerEspConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
    // Runs after vanilla's own extractRenderState so we override outlineColor last.
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void minehelper_injectGlowColor(T entity, S state, float partialTick, CallbackInfo ci) {
        if (!(entity instanceof AbstractClientPlayer player)) return;

        var esp = MineHelperClient.getPlayerEspFeature();
        if (esp == null || !esp.isEnabled()) return;
        if (esp.getConfig().getRenderMode() != PlayerEspConfig.RenderMode.GLOW) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || player == mc.player) return;

        double radiusSq = (double) esp.getConfig().getScanRadius() * esp.getConfig().getScanRadius();
        if (mc.player.distanceToSqr(player) > radiusSq) return;

        float[] rgba = esp.getConfig().resolveColor(player);
        int r = Math.round(rgba[0] * 255);
        int g = Math.round(rgba[1] * 255);
        int b = Math.round(rgba[2] * 255);
        // outlineColor must be non-zero for appearsGlowing() to return true
        state.outlineColor = 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
