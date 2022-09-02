package dustw.libgui.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dustw.libgui.event.HudRenderCallback;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author fabric mc
 */
@Mixin(Gui.class)
public class InGameHudMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"))
    public void render(PoseStack matrixStack, float tickDelta, CallbackInfo callbackInfo) {
        HudRenderCallback.EVENTS.forEach(e -> e.onHudRender(matrixStack, tickDelta));
    }
}
