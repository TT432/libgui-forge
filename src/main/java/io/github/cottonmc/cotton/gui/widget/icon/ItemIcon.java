package io.github.cottonmc.cotton.gui.widget.icon;

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
        Minecraft client = Minecraft.getInstance();
        ItemRenderer renderer = client.getItemRenderer();
        float scale = size != 16 ? ((float) size / 16f) : 1f;

        matrices.pushPose();
        matrices.translate(x, y, 0);
        matrices.scale(scale, scale, 1);
        renderer.renderAndDecorateFakeItem(matrices, stack, 0, 0);
        matrices.popPose();
    }
}
