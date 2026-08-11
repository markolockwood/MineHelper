package com.minehelper.client.feature.blockfinder;

import com.minehelper.client.render.CustomRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class BlockHighlightRenderer {
    private final BlockScanner scanner;
    private final BlockFinderConfig config;

    public BlockHighlightRenderer(BlockScanner scanner, BlockFinderConfig config) {
        this.scanner = scanner;
        this.config = config;
    }

    public void render(LevelRenderContext context) {
        if (!scanner.isEnabled()) {
            return;
        }

        var foundBlocks = scanner.getFoundBlocks();
        if (foundBlocks.isEmpty()) {
            return;
        }

        PoseStack poseStack = context.poseStack();
        if (poseStack == null) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().position();

        // Use immediate buffer instead of context buffer
        MultiBufferSource.BufferSource immediateBuffer = mc.renderBuffers().bufferSource();

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        Matrix4f matrix = poseStack.last().pose();

        float[] color = config.getHighlightColor();
        float r = color[0];
        float g = color[1];
        float b = color[2];
        float a = color[3];

        // Render with depth test disabled (ALWAYS_PASS), so lines draw through terrain.
        RenderType lineType = CustomRenderTypes.LINES_NO_DEPTH;
        VertexConsumer buffer = immediateBuffer.getBuffer(lineType);
        for (BlockPos pos : foundBlocks) {
            renderBlockOutline(matrix, buffer, pos, r, g, b, a);
        }
        immediateBuffer.endBatch(lineType);

        poseStack.popPose();
    }

    private void renderBlockOutline(Matrix4f matrix, VertexConsumer buffer, BlockPos pos, float r, float g, float b, float a) {
        double minX = pos.getX();
        double minY = pos.getY();
        double minZ = pos.getZ();
        double maxX = pos.getX() + 1.0;
        double maxY = pos.getY() + 1.0;
        double maxZ = pos.getZ() + 1.0;

        float x1 = (float) minX;
        float y1 = (float) minY;
        float z1 = (float) minZ;
        float x2 = (float) maxX;
        float y2 = (float) maxY;
        float z2 = (float) maxZ;

        // Bottom face
        line(matrix, buffer, x1, y1, z1, x2, y1, z1, r, g, b, a);
        line(matrix, buffer, x2, y1, z1, x2, y1, z2, r, g, b, a);
        line(matrix, buffer, x2, y1, z2, x1, y1, z2, r, g, b, a);
        line(matrix, buffer, x1, y1, z2, x1, y1, z1, r, g, b, a);

        // Top face
        line(matrix, buffer, x1, y2, z1, x2, y2, z1, r, g, b, a);
        line(matrix, buffer, x2, y2, z1, x2, y2, z2, r, g, b, a);
        line(matrix, buffer, x2, y2, z2, x1, y2, z2, r, g, b, a);
        line(matrix, buffer, x1, y2, z2, x1, y2, z1, r, g, b, a);

        // Vertical edges
        line(matrix, buffer, x1, y1, z1, x1, y2, z1, r, g, b, a);
        line(matrix, buffer, x2, y1, z1, x2, y2, z1, r, g, b, a);
        line(matrix, buffer, x2, y1, z2, x2, y2, z2, r, g, b, a);
        line(matrix, buffer, x1, y1, z2, x1, y2, z2, r, g, b, a);
    }

    private void line(Matrix4f matrix, VertexConsumer buffer, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b, float a) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;
        float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (length > 0.0001f) {
            dx /= length;
            dy /= length;
            dz /= length;
        }

        buffer.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a).setNormal(dx, dy, dz).setLineWidth(config.getLineWidth());
        buffer.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a).setNormal(dx, dy, dz).setLineWidth(config.getLineWidth());
    }
}
