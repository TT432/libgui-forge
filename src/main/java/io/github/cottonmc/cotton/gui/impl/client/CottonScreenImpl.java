package io.github.cottonmc.cotton.gui.impl.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;


public interface CottonScreenImpl {
    GuiDescription getDescription();

    @Nullable
    WWidget getLastResponder();

    void setLastResponder(@Nullable WWidget lastResponder);

    void renderTextHover(PoseStack matrices, @Nullable Style textStyle, int x, int y);
}
