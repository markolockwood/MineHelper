package com.minehelper.client.render;

import com.minehelper.MineHelper;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.textures.TextureFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.MineHelperRenderTypeAccessor;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

// Clones vanilla's LINES pipeline (same shaders, uniforms, samplers, vertex
// format - required for the matrices/fog uniforms the shader expects) but
// swaps the depth test to ALWAYS_PASS so lines draw through terrain instead
// of being occluded by it.
public final class CustomRenderTypes {
    private static final RenderPipeline LINES_NO_DEPTH_PIPELINE = buildNoDepthLinesPipeline();

    public static final RenderType LINES_NO_DEPTH = MineHelperRenderTypeAccessor.create(
        "minehelper_lines_no_depth",
        RenderSetup.builder(LINES_NO_DEPTH_PIPELINE)
            .setOutputTarget(OutputTarget.MAIN_TARGET)
            .createRenderSetup()
    );

    private static RenderPipeline buildNoDepthLinesPipeline() {
        RenderPipeline vanillaLines = RenderPipelines.LINES;

        RenderPipeline.Builder builder = RenderPipeline.builder()
            .withLocation(MineHelper.id("pipeline/lines_no_depth"))
            .withVertexShader(vanillaLines.getVertexShader())
            .withFragmentShader(vanillaLines.getFragmentShader())
            .withColorTargetState(vanillaLines.getColorTargetState())
            .withCull(vanillaLines.isCull())
            .withPolygonMode(vanillaLines.getPolygonMode())
            .withVertexFormat(vanillaLines.getVertexFormat(), vanillaLines.getVertexFormatMode())
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false));

        for (String sampler : vanillaLines.getSamplers()) {
            builder.withSampler(sampler);
        }

        for (RenderPipeline.UniformDescription uniform : vanillaLines.getUniforms()) {
            UniformType type = uniform.type();
            TextureFormat textureFormat = uniform.textureFormat();
            if (type != null && textureFormat != null) {
                builder.withUniform(uniform.name(), type, textureFormat);
            } else if (type != null) {
                builder.withUniform(uniform.name(), type);
            }
        }

        return builder.build();
    }

    private CustomRenderTypes() {
    }
}
