package com.minehelper.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class BlockSlotWidget extends AbstractWidget {
    private Block block;
    private ItemStack itemStack;
    private final Runnable onClick;

    public BlockSlotWidget(int x, int y, Runnable onClick) {
        super(x, y, 18, 18, Component.empty());
        this.onClick = onClick;
        this.visible = false;
    }

    public void setBlock(Block block) {
        this.block = block;
        this.itemStack = new ItemStack(block.asItem());
        this.visible = true;
        setTooltip(Tooltip.create(itemStack.getHoverName()));
    }

    public void clear() {
        this.block = null;
        this.itemStack = null;
        this.visible = false;
        setTooltip(null);
    }

    public Block getBlock() {
        return block;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        if (itemStack != null) {
            extractor.item(itemStack, getX(), getY());

            // Draw hover highlight
            if (isHovered()) {
                extractor.fill(getX(), getY(), getX() + 18, getY() + 18, 0x80FFFFFF);
            }
        }
    }

    @Override
    public void onClick(net.minecraft.client.input.MouseButtonEvent event, boolean withinBounds) {
        if (withinBounds && event.button() == 0 && onClick != null) {
            onClick.run();
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        if (itemStack != null) {
            output.add(net.minecraft.client.gui.narration.NarratedElementType.TITLE, itemStack.getHoverName());
        }
    }
}
