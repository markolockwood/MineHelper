package net.minecraft.client.renderer.rendertype;

/**
 * RenderType.create(String, RenderSetup) is package-private. This bridge
 * lives in the same vanilla package so the mod can build a custom RenderType
 * without mixins.
 */
public final class MineHelperRenderTypeAccessor {
    private MineHelperRenderTypeAccessor() {
    }

    public static RenderType create(String name, RenderSetup setup) {
        return RenderType.create(name, setup);
    }
}
