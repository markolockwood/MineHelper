package com.minehelper.client.feature.playeresp;

import com.minehelper.client.render.CustomRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.List;

public class PlayerEspRenderer {
    private final PlayerEspConfig config;

    public PlayerEspRenderer(PlayerEspConfig config) {
        this.config = config;
    }

    // Only called for AABB mode. Glow mode is handled by the two mixins.
    public void render(PoseStack poseStack) {
        if (config.getRenderMode() != PlayerEspConfig.RenderMode.AABB) return;

        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null || mc.player == null) return;

        Vec3 camera = mc.gameRenderer.getMainCamera().position();
        List<AbstractClientPlayer> players = level.players();
        if (players.isEmpty()) return;

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        RenderType lineType = CustomRenderTypes.LINES_NO_DEPTH;
        VertexConsumer buffer = bufferSource.getBuffer(lineType);

        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);
        Matrix4f matrix = poseStack.last().pose();

        double radiusSq = (double) config.getScanRadius() * config.getScanRadius();

        for (AbstractClientPlayer player : players) {
            if (player == mc.player) continue;
            if (mc.player.distanceToSqr(player) > radiusSq) continue;

            float[] color = config.resolveColor(player);
            renderAabb(matrix, buffer, player.getBoundingBox(), color[0], color[1], color[2], color[3]);
        }

        poseStack.popPose();
        bufferSource.endBatch(lineType);
    }

    private void renderAabb(Matrix4f m, VertexConsumer buf, AABB box,
                            float r, float g, float b, float a) {
        float x0 = (float) box.minX, y0 = (float) box.minY, z0 = (float) box.minZ;
        float x1 = (float) box.maxX, y1 = (float) box.maxY, z1 = (float) box.maxZ;
        float w = config.getLineWidth();
        line(m, buf, x0,y0,z0, x1,y0,z0, r,g,b,a,w);
        line(m, buf, x1,y0,z0, x1,y0,z1, r,g,b,a,w);
        line(m, buf, x1,y0,z1, x0,y0,z1, r,g,b,a,w);
        line(m, buf, x0,y0,z1, x0,y0,z0, r,g,b,a,w);
        line(m, buf, x0,y1,z0, x1,y1,z0, r,g,b,a,w);
        line(m, buf, x1,y1,z0, x1,y1,z1, r,g,b,a,w);
        line(m, buf, x1,y1,z1, x0,y1,z1, r,g,b,a,w);
        line(m, buf, x0,y1,z1, x0,y1,z0, r,g,b,a,w);
        line(m, buf, x0,y0,z0, x0,y1,z0, r,g,b,a,w);
        line(m, buf, x1,y0,z0, x1,y1,z0, r,g,b,a,w);
        line(m, buf, x1,y0,z1, x1,y1,z1, r,g,b,a,w);
        line(m, buf, x0,y0,z1, x0,y1,z1, r,g,b,a,w);
    }

    private void line(Matrix4f m, VertexConsumer buf,
                      float x1, float y1, float z1,
                      float x2, float y2, float z2,
                      float r, float g, float b, float a, float w) {
        float dx = x2-x1, dy = y2-y1, dz = z2-z1;
        float len = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
        if (len == 0) return;
        dx /= len; dy /= len; dz /= len;
        buf.addVertex(m, x1, y1, z1).setColor(r, g, b, a).setNormal(dx, dy, dz).setLineWidth(w);
        buf.addVertex(m, x2, y2, z2).setColor(r, g, b, a).setNormal(dx, dy, dz).setLineWidth(w);
    }
}
