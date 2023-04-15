package dustw.libgui.mixin;

import com.mojang.datafixers.util.Pair;
import dustw.libgui.event.CoreShaderRegistrationCallback;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author DustW
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(
            method = "reloadShaders",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", remap = false, shift = At.Shift.AFTER),
            slice = @Slice(from = @At(value = "NEW", target = "Lnet/minecraft/client/renderer/ShaderInstance;<init>(Lnet/minecraft/server/packs/resources/ResourceProvider;Ljava/lang/String;Lcom/mojang/blaze3d/vertex/VertexFormat;)V", ordinal = 0)),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void registerShaders(ResourceProvider factory, CallbackInfo info, List<?> shaderStages,
                                 List<Pair<ShaderInstance, Consumer<ShaderInstance>>> programs) throws IOException {
        CoreShaderRegistrationCallback.RegistrationContext context = (id, vertexFormat, loadCallback) -> {
            ShaderInstance program = new ShaderInstance(factory, id, vertexFormat);
            programs.add(Pair.of(program, loadCallback));
        };
        CoreShaderRegistrationCallback.EVENTS.forEach(c -> {
            try {
                c.registerShaders(context);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
