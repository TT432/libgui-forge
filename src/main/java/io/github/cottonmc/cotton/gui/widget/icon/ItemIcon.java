package io.github.cottonmc.cotton.gui.widget.icon;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

/**
 * An icon that draws an item stack.
 *
 * @since 2.2.0
 */
public class ItemIcon implements Icon {
    private final ItemStack stack;

    /**
     * Constructs an item icon.
     *
     * @param stack the drawn item stack
     * @throws NullPointerException if the stack is null
     */
    public ItemIcon(ItemStack stack) {
        this.stack = Objects.requireNonNull(stack, "stack");
    }

    /**
     * Constructs an item icon with the item's default stack.
     *
     * @param item the drawn item
     * @throws NullPointerException if the item is null
     * @since 3.2.0
     */
    public ItemIcon(Item item) {
        this(Objects.requireNonNull(item, "item").getDefaultInstance());
    }


    @Override
    public void paint(PoseStack matrices, int x, int y, int size) {
        // TODO: Make this not ignore the actual matrices
        Minecraft client = Minecraft.getInstance();
        ItemRenderer renderer = client.getItemRenderer();
        PoseStack modelViewMatrices = RenderSystem.getModelViewStack();

        float scale = size != 16 ? ((float) size / 16f) : 1f;

        modelViewMatrices.pushPose();
        modelViewMatrices.translate(x, y, 0);
        modelViewMatrices.scale(scale, scale, 1);
        RenderSystem.applyModelViewMatrix();
        renderer.renderAndDecorateFakeItem(stack, 0, 0);
        modelViewMatrices.popPose();
        RenderSystem.applyModelViewMatrix();
    }
}
